/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.data

import android.content.Context
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class FileRepository(private val context: Context) {

    private val fileName = "accounting_records.txt"
    private val budgetFileName = "budget.txt"
    private val recurringFileName = "recurring.txt"
    private val ioMutex = Mutex()

    suspend fun getBudget(): Double = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val file = File(context.filesDir, budgetFileName)
            if (!file.exists()) return@withLock 0.0
            try {
                file.readText().trim().toDoubleOrNull() ?: 0.0
            } catch (e: Exception) {
                0.0
            }
        }
    }

    suspend fun setBudget(amount: Double) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val file = File(context.filesDir, budgetFileName)
            try {
                file.writeText(amount.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addRecurringTransaction(rt: RecurringTransaction) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val file = File(context.filesDir, recurringFileName)
            try {
                val safeNotes = rt.notes.replace("|", " ")
                val line = "${rt.id}|${rt.type}|${rt.amount}|${safeNotes}|${rt.mood.score}|${rt.frequency}|${rt.dayOfMonth}|${rt.monthOfYear}|${rt.lastGeneratedTime}\n"
                file.appendText(line)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun readRecurringTransactions(file: File): MutableList<RecurringTransaction> {
         val list = mutableListOf<RecurringTransaction>()
         if (!file.exists()) return list
         file.forEachLine { line ->
             val parts = line.split("|")
             if (parts.size >= 9) {
                 val id = parts[0]
                 val type = try { TransactionType.valueOf(parts[1]) } catch (e: Exception) { TransactionType.EXPENSE }
                 val amount = parts[2].toDoubleOrNull() ?: 0.0
                 val notes = parts[3]
                 val moodScore = parts[4].toIntOrNull() ?: 3
                 val mood = Mood.fromScore(moodScore)
                 val freq = try { Frequency.valueOf(parts[5]) } catch (e: Exception) { Frequency.MONTHLY }
                 val day = parts[6].toIntOrNull() ?: 1
                 val month = parts[7].toIntOrNull() ?: 1
                 val lastGen = parts[8].toLongOrNull() ?: 0L
                 list.add(RecurringTransaction(id, type, amount, notes, mood, freq, day, month, lastGen))
             }
         }
         return list
    }

    suspend fun checkAndGenerateRecurringTransactions() = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val rFile = File(context.filesDir, recurringFileName)
            if (!rFile.exists()) return@withLock

            val recurringList = readRecurringTransactions(rFile)
            val tFile = File(context.filesDir, fileName)
            var changesMade = false
            val now = System.currentTimeMillis()
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = now
            val currentYear = calendar.get(java.util.Calendar.YEAR)
            val currentMonth = calendar.get(java.util.Calendar.MONTH) + 1 // 1-12
            val currentDay = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            val updatedList = recurringList.map { rt ->
                val lastGen = rt.lastGeneratedTime
                val lastGenCal = java.util.Calendar.getInstance()
                lastGenCal.timeInMillis = if (lastGen == 0L) 0L else lastGen

                var shouldGenerate = false
                when (rt.frequency) {
                    Frequency.DAILY -> {
                         if (lastGen == 0L || !isSameDay(lastGenCal, calendar)) {
                             shouldGenerate = true
                         }
                    }
                    Frequency.MONTHLY -> {
                        if (currentDay >= rt.dayOfMonth) {
                             if (lastGen == 0L || isBeforeMonth(lastGenCal, calendar)) {
                                 shouldGenerate = true
                             }
                        }
                    }
                    Frequency.YEARLY -> {
                        if (currentMonth > rt.monthOfYear || (currentMonth == rt.monthOfYear && currentDay >= rt.dayOfMonth)) {
                            if (lastGen == 0L || isBeforeYear(lastGenCal, calendar)) {
                                shouldGenerate = true
                            }
                        }
                    }
                }

                if (shouldGenerate) {
                    changesMade = true
                    val safeNotes = rt.notes.replace("|", " ")
                    // Append directly to transaction file (we are already inside ioMutex lock)
                    // But we should verify we aren't duplicating.
                    // This logic assumes we only run this once per session or it's safe.
                    // The mutex protects concurrent access.
                    val newLine = "${java.util.UUID.randomUUID()}|$now|${rt.type}|${rt.amount}|$safeNotes|${rt.mood.score}\n"
                    tFile.appendText(newLine)
                    rt.copy(lastGeneratedTime = now)
                } else {
                    rt
                }
            }

            if (changesMade) {
                val tmp = File(context.filesDir, "${recurringFileName}.tmp")
                tmp.writeText("")
                updatedList.forEach { rt ->
                    val safeNotes = rt.notes.replace("|", " ")
                    val line = "${rt.id}|${rt.type}|${rt.amount}|${safeNotes}|${rt.mood.score}|${rt.frequency}|${rt.dayOfMonth}|${rt.monthOfYear}|${rt.lastGeneratedTime}\n"
                    tmp.appendText(line)
                }
                try {
                    Files.move(
                        tmp.toPath(),
                        rFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                    )
                } catch (e: Exception) {
                    tmp.copyTo(rFile, overwrite = true)
                    tmp.delete()
                }
            }
        }
    }

    private fun isSameDay(cal1: java.util.Calendar, cal2: java.util.Calendar): Boolean {
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
               cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }

    private fun isBeforeMonth(cal1: java.util.Calendar, cal2: java.util.Calendar): Boolean {
        val y1 = cal1.get(java.util.Calendar.YEAR)
        val y2 = cal2.get(java.util.Calendar.YEAR)
        if (y1 < y2) return true
        if (y1 > y2) return false
        return cal1.get(java.util.Calendar.MONTH) < cal2.get(java.util.Calendar.MONTH)
    }

    private fun isBeforeYear(cal1: java.util.Calendar, cal2: java.util.Calendar): Boolean {
        return cal1.get(java.util.Calendar.YEAR) < cal2.get(java.util.Calendar.YEAR)
    }

    suspend fun getAllTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withLock emptyList()

            val transactions = mutableListOf<Transaction>()
            try {
                file.forEachLine { line ->
                    val parts = line.split("|")
                    if (parts.size >= 6) {
                        val id = parts[0]
                        val date = parts[1].toLongOrNull() ?: 0L
                        val type = try { TransactionType.valueOf(parts[2]) } catch (e: Exception) { TransactionType.EXPENSE }
                        val amount = parts[3].toDoubleOrNull() ?: 0.0
                        val notes = parts[4]
                        val moodScore = parts[5].toIntOrNull() ?: 3
                        val mood = Mood.fromScore(moodScore)
                        
                        transactions.add(Transaction(id, date, type, amount, notes, mood))
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return@withLock transactions.sortedByDescending { it.date }
        }
    }

    suspend fun addTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val file = File(context.filesDir, fileName)
            try {
                val safeNotes = transaction.notes.replace("|", " ")
                val line = "${transaction.id}|${transaction.date}|${transaction.type}|${transaction.amount}|${safeNotes}|${transaction.mood.score}\n"
                file.appendText(line)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteTransaction(id: String) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withLock
            try {
                val tmp = File(context.filesDir, "${fileName}.tmp")
                tmp.writeText("")
                file.forEachLine { line ->
                    val keep = line.substringBefore("|") != id
                    if (keep) tmp.appendText(line + if (line.endsWith("\n")) "" else "\n")
                }
                try {
                    Files.move(
                        tmp.toPath(),
                        file.toPath(),
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                    )
                } catch (e: Exception) {
                    try {
                        tmp.copyTo(file, overwrite = true)
                        tmp.delete()
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                        throw e2
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            val file = File(context.filesDir, fileName)
            val budgetFile = File(context.filesDir, budgetFileName)
            val recurringFile = File(context.filesDir, recurringFileName)
            try {
                if (file.exists()) {
                    file.writeText("")
                }
                if (budgetFile.exists()) {
                    budgetFile.writeText("0.0")
                }
                if (recurringFile.exists()) {
                    recurringFile.writeText("")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
