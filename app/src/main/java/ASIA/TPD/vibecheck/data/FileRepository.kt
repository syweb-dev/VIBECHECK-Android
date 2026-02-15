/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package ASIA.TPD.vibecheck.data

import android.content.Context
import java.io.File
import java.io.IOException

class FileRepository(private val context: Context) {

    private val fileName = "accounting_records.txt"

    fun getAllTransactions(): List<Transaction> {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return emptyList()

        val transactions = mutableListOf<Transaction>()
        try {
            file.forEachLine { line ->
                val parts = line.split("|")
                if (parts.size >= 6) {
                    val id = parts[0]
                    val date = parts[1].toLongOrNull() ?: 0L
                    val type = try { TransactionType.valueOf(parts[2]) } catch (e: Exception) { TransactionType.EXPENSE }
                    val amount = parts[3].toDoubleOrNull() ?: 0.0
                    // Join remaining parts as notes in case notes contained delimiter (simple recovery)
                    // But better to sanitize input. For now, assuming basic structure.
                    val notes = parts[4]
                    val moodScore = parts[5].toIntOrNull() ?: 3
                    val mood = Mood.fromScore(moodScore)
                    
                    transactions.add(Transaction(id, date, type, amount, notes, mood))
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return transactions.sortedByDescending { it.date }
    }

    fun addTransaction(transaction: Transaction) {
        val file = File(context.filesDir, fileName)
        try {
            val safeNotes = transaction.notes.replace("|", " ")
            val line = "${transaction.id}|${transaction.date}|${transaction.type}|${transaction.amount}|${safeNotes}|${transaction.mood.score}\n"
            file.appendText(line)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun deleteTransaction(id: String) {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return
        try {
            val tmp = File(context.filesDir, "${fileName}.tmp")
            tmp.writeText("")
            file.forEachLine { line ->
                val keep = line.substringBefore("|") != id
                if (keep) tmp.appendText(line + if (line.endsWith("\n")) "" else "\n")
            }
            file.delete()
            tmp.renameTo(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun clearAll() {
        val file = File(context.filesDir, fileName)
        if (file.exists()) {
            file.delete()
        }
    }
}
