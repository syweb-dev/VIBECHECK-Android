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
    private val ioMutex = Mutex()

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
            try {
                if (file.exists()) {
                    file.writeText("")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
