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
                val line = RecurringLineCodec.toLine(rt)
                file.appendText(line + "\n")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun readRecurringTransactions(file: File): MutableList<RecurringTransaction> {
         val list = mutableListOf<RecurringTransaction>()
         if (!file.exists()) return list
         file.forEachLine { line ->
             RecurringLineCodec.fromLineSafe(line)?.let { list.add(it) }
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
                    // Append directly to transaction file (we are already inside ioMutex lock)
                    // But we should verify we aren't duplicating.
                    // This logic assumes we only run this once per session or it's safe.
                    // The mutex protects concurrent access.
                    val newTransaction = Transaction(
                        id = java.util.UUID.randomUUID().toString(),
                        date = now,
                        type = rt.type,
                        amount = rt.amount,
                        notes = rt.notes,
                        mood = rt.mood
                    )
                    tFile.appendText(TransactionLineCodec.toLine(newTransaction) + "\n")
                    rt.copy(lastGeneratedTime = now)
                } else {
                    rt
                }
            }

            if (changesMade) {
                val tmp = File(context.filesDir, "${recurringFileName}.tmp")
                tmp.writeText("")
                updatedList.forEach { rt ->
                    tmp.appendText(RecurringLineCodec.toLine(rt) + "\n")
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
                    TransactionLineCodec.fromLineSafe(line)?.let { transactions.add(it) }
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
                val line = TransactionLineCodec.toLine(transaction)
                file.appendText(line + "\n")
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
