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
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class FileRepository(private val context: Context) {

    private val fileName = "accounting_records.json"
    private val budgetFileName = "budget.json"
    private val recurringFileName = "recurring.json"
    private val legacyFileName = "accounting_records.txt"
    private val legacyBudgetFileName = "budget.txt"
    private val legacyRecurringFileName = "recurring.txt"
    private val ioMutex = Mutex()

    private data class BudgetData(
        val amount: Double,
        val autoResetDay: Int,
        val lastResetPeriod: String
    )

    private fun transactionToJson(t: Transaction): JSONObject {
        return JSONObject().apply {
            put("id", t.id)
            put("date", t.date)
            put("type", t.type.name)
            put("amount", t.amount)
            put("notes", t.notes)
            put("mood", t.mood.score)
        }
    }

    private fun transactionFromJson(obj: JSONObject): Transaction? {
        val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return null
        val date = obj.optLong("date", Long.MIN_VALUE)
        if (date == Long.MIN_VALUE) return null
        val type = runCatching { TransactionType.valueOf(obj.optString("type", "EXPENSE")) }
            .getOrDefault(TransactionType.EXPENSE)
        val amount = if (obj.has("amount")) obj.optDouble("amount", Double.NaN) else Double.NaN
        if (!amount.isFinite()) return null
        val notes = obj.optString("notes", "")
        val mood = Mood.fromScore(obj.optInt("mood", 3))
        return Transaction(id, date, type, amount, notes, mood)
    }

    private fun recurringToJson(rt: RecurringTransaction): JSONObject {
        return JSONObject().apply {
            put("id", rt.id)
            put("type", rt.type.name)
            put("amount", rt.amount)
            put("notes", rt.notes)
            put("mood", rt.mood.score)
            put("frequency", rt.frequency.name)
            put("dayOfMonth", rt.dayOfMonth)
            put("monthOfYear", rt.monthOfYear)
            put("lastGeneratedTime", rt.lastGeneratedTime)
        }
    }

    private fun recurringFromJson(obj: JSONObject): RecurringTransaction? {
        val id = obj.optString("id").takeIf { it.isNotBlank() } ?: return null
        val type = runCatching { TransactionType.valueOf(obj.optString("type", "EXPENSE")) }
            .getOrDefault(TransactionType.EXPENSE)
        val amount = if (obj.has("amount")) obj.optDouble("amount", Double.NaN) else Double.NaN
        if (!amount.isFinite()) return null
        val notes = obj.optString("notes", "")
        val mood = Mood.fromScore(obj.optInt("mood", 3))
        val freq = runCatching { Frequency.valueOf(obj.optString("frequency", "MONTHLY")) }
            .getOrDefault(Frequency.MONTHLY)
        val day = obj.optInt("dayOfMonth", 1)
        val month = obj.optInt("monthOfYear", 1)
        val lastGen = obj.optLong("lastGeneratedTime", 0L)
        return RecurringTransaction(id, type, amount, notes, mood, freq, day, month, lastGen)
    }

    private fun readTransactions(file: File): MutableList<Transaction> {
        val list = mutableListOf<Transaction>()
        if (!file.exists()) return list
        val text = file.readText().trim()
        if (text.isBlank()) return list
        val parsedJson = runCatching { JSONArray(text) }.getOrNull()
        if (parsedJson != null) {
            for (index in 0 until parsedJson.length()) {
                val obj = parsedJson.optJSONObject(index) ?: continue
                transactionFromJson(obj)?.let { list.add(it) }
            }
            return list
        }
        text.lineSequence().forEach { line ->
            TransactionLineCodec.fromLineSafe(line)?.let { list.add(it) }
        }
        return list
    }

    private fun readRecurringTransactions(file: File): MutableList<RecurringTransaction> {
        val list = mutableListOf<RecurringTransaction>()
        if (!file.exists()) return list
        val text = file.readText().trim()
        if (text.isBlank()) return list
        val parsedJson = runCatching { JSONArray(text) }.getOrNull()
        if (parsedJson != null) {
            for (index in 0 until parsedJson.length()) {
                val obj = parsedJson.optJSONObject(index) ?: continue
                recurringFromJson(obj)?.let { list.add(it) }
            }
            return list
        }
        text.lineSequence().forEach { line ->
            RecurringLineCodec.fromLineSafe(line)?.let { list.add(it) }
        }
        return list
    }

    private fun writeTransactions(file: File, transactions: List<Transaction>) {
        val jsonArray = JSONArray()
        transactions.forEach { jsonArray.put(transactionToJson(it)) }
        atomicWrite(file, jsonArray.toString())
    }

    private fun writeRecurring(file: File, recurringList: List<RecurringTransaction>) {
        val jsonArray = JSONArray()
        recurringList.forEach { jsonArray.put(recurringToJson(it)) }
        atomicWrite(file, jsonArray.toString())
    }

    private fun atomicWrite(target: File, content: String) {
        val tmp = File(context.filesDir, "${target.name}.tmp")
        tmp.writeText(content)
        try {
            Files.move(
                tmp.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        } catch (e: Exception) {
            tmp.copyTo(target, overwrite = true)
            tmp.delete()
        }
    }

    private fun currentBudgetPeriod(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        return String.format("%04d-%02d", year, month)
    }

    private fun readBudgetData(file: File): BudgetData {
        if (!file.exists()) {
            return BudgetData(amount = 0.0, autoResetDay = 1, lastResetPeriod = currentBudgetPeriod())
        }
        val text = file.readText().trim()
        if (text.isBlank()) {
            return BudgetData(amount = 0.0, autoResetDay = 1, lastResetPeriod = currentBudgetPeriod())
        }

        val asJson = runCatching { JSONObject(text) }.getOrNull()
        if (asJson != null) {
            val amount = asJson.optDouble("amount", 0.0)
            val autoResetDay = asJson.optInt("autoResetDay", 1).coerceIn(1, 31)
            val lastResetPeriod = asJson.optString("lastResetPeriod", currentBudgetPeriod())
                .ifBlank { currentBudgetPeriod() }
            return BudgetData(
                amount = if (amount.isFinite()) amount else 0.0,
                autoResetDay = autoResetDay,
                lastResetPeriod = lastResetPeriod
            )
        }

        val amount = text.toDoubleOrNull() ?: 0.0
        return BudgetData(amount = amount, autoResetDay = 1, lastResetPeriod = currentBudgetPeriod())
    }

    private fun writeBudgetData(file: File, budgetData: BudgetData) {
        val json = JSONObject()
            .put("amount", budgetData.amount)
            .put("autoResetDay", budgetData.autoResetDay.coerceIn(1, 31))
            .put("lastResetPeriod", budgetData.lastResetPeriod)
        atomicWrite(file, json.toString())
    }

    private fun applyBudgetAutoReset(file: File, budgetData: BudgetData): BudgetData {
        val calendar = Calendar.getInstance()
        val currentPeriod = currentBudgetPeriod()
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val resetDayThisMonth = budgetData.autoResetDay.coerceIn(1, 31).coerceAtMost(maxDay)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val shouldReset = currentDay >= resetDayThisMonth && budgetData.lastResetPeriod != currentPeriod

        if (!shouldReset) return budgetData

        val resetBudget = budgetData.copy(amount = 0.0, lastResetPeriod = currentPeriod)
        writeBudgetData(file, resetBudget)
        return resetBudget
    }

    private fun migrateLegacyIfNeeded() {
        val transactionFile = File(context.filesDir, fileName)
        val legacyTransactionFile = File(context.filesDir, legacyFileName)
        if (!transactionFile.exists() && legacyTransactionFile.exists()) {
            val migrated = readTransactions(legacyTransactionFile)
            writeTransactions(transactionFile, migrated)
        }

        val recurringFile = File(context.filesDir, recurringFileName)
        val legacyRecurringFile = File(context.filesDir, legacyRecurringFileName)
        if (!recurringFile.exists() && legacyRecurringFile.exists()) {
            val migrated = readRecurringTransactions(legacyRecurringFile)
            writeRecurring(recurringFile, migrated)
        }

        val budgetFile = File(context.filesDir, budgetFileName)
        val legacyBudgetFile = File(context.filesDir, legacyBudgetFileName)
        if (!budgetFile.exists() && legacyBudgetFile.exists()) {
            val amount = legacyBudgetFile.readText().trim().toDoubleOrNull() ?: 0.0
            writeBudgetData(
                budgetFile,
                BudgetData(
                    amount = amount,
                    autoResetDay = 1,
                    lastResetPeriod = currentBudgetPeriod()
                )
            )
        }
    }

    suspend fun getBudget(): Double = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, budgetFileName)
            try {
                val budgetData = applyBudgetAutoReset(file, readBudgetData(file))
                budgetData.amount
            } catch (e: Exception) {
                0.0
            }
        }
    }

    suspend fun getBudgetResetDay(): Int = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, budgetFileName)
            try {
                val budgetData = applyBudgetAutoReset(file, readBudgetData(file))
                budgetData.autoResetDay
            } catch (e: Exception) {
                1
            }
        }
    }

    suspend fun setBudget(amount: Double, autoResetDay: Int) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, budgetFileName)
            try {
                writeBudgetData(
                    file,
                    BudgetData(
                        amount = amount,
                        autoResetDay = autoResetDay.coerceIn(1, 31),
                        lastResetPeriod = currentBudgetPeriod()
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun clearBudgetOnly() = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, budgetFileName)
            try {
                val current = readBudgetData(file)
                writeBudgetData(
                    file,
                    current.copy(amount = 0.0, lastResetPeriod = currentBudgetPeriod())
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun addRecurringTransaction(rt: RecurringTransaction) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, recurringFileName)
            try {
                val list = readRecurringTransactions(file)
                list.add(rt)
                writeRecurring(file, list)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun checkAndGenerateRecurringTransactions() = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val rFile = File(context.filesDir, recurringFileName)
            if (!rFile.exists()) return@withLock

            val recurringList = readRecurringTransactions(rFile)
            val tFile = File(context.filesDir, fileName)
            val transactionList = readTransactions(tFile)
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
                    val newTransaction = Transaction(
                        id = java.util.UUID.randomUUID().toString(),
                        date = now,
                        type = rt.type,
                        amount = rt.amount,
                        notes = rt.notes,
                        mood = rt.mood
                    )
                    transactionList.add(newTransaction)
                    rt.copy(lastGeneratedTime = now)
                } else {
                    rt
                }
            }

            if (changesMade) {
                writeTransactions(tFile, transactionList)
                writeRecurring(rFile, updatedList)
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
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withLock emptyList()

            val transactions = mutableListOf<Transaction>()
            try {
                transactions.addAll(readTransactions(file))
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return@withLock transactions.sortedByDescending { it.date }
        }
    }

    suspend fun addTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, fileName)
            try {
                val list = readTransactions(file)
                list.add(transaction)
                writeTransactions(file, list)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun deleteTransaction(id: String) = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return@withLock
            try {
                val list = readTransactions(file).filterNot { it.id == id }
                writeTransactions(file, list)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        ioMutex.withLock {
            migrateLegacyIfNeeded()
            val file = File(context.filesDir, fileName)
            val budgetFile = File(context.filesDir, budgetFileName)
            val recurringFile = File(context.filesDir, recurringFileName)
            try {
                if (file.exists()) {
                    writeTransactions(file, emptyList())
                }
                if (budgetFile.exists()) {
                    writeBudgetData(
                        budgetFile,
                        BudgetData(
                            amount = 0.0,
                            autoResetDay = 1,
                            lastResetPeriod = currentBudgetPeriod()
                        )
                    )
                }
                if (recurringFile.exists()) {
                    writeRecurring(recurringFile, emptyList())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
