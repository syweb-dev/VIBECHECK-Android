/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.data

import java.util.Base64

object LineCodecUtils {
    const val DELIM = "|"
    
    fun b64(s: String): String = Base64.getEncoder().encodeToString(s.toByteArray(Charsets.UTF_8))
    
    fun ub64(s: String): String = try {
        String(Base64.getDecoder().decode(s), Charsets.UTF_8)
    } catch (e: Exception) {
        ""
    }
}

object TransactionLineCodec {
    private const val VER = "T1"

    fun toLine(t: Transaction): String {
        val notes = LineCodecUtils.b64(t.notes)
        return listOf(
            t.id,
            VER,
            t.date.toString(),
            t.type.name,
            t.amount.toString(),
            notes,
            t.mood.score.toString()
        ).joinToString(LineCodecUtils.DELIM)
    }

    fun fromLine(line: String): Transaction? {
        val p = line.split(LineCodecUtils.DELIM)
        return when {
            // New format: id|VER|date|type|amount|notesB64|mood
            p.size >= 7 && p[1] == VER -> {
                val id = p[0]
                val date = p[2].toLongOrNull() ?: return null
                val type = runCatching { TransactionType.valueOf(p[3]) }.getOrDefault(TransactionType.EXPENSE)
                val amount = p[4].toDoubleOrNull() ?: return null
                val notes = LineCodecUtils.ub64(p[5])
                val mood = Mood.fromScore(p[6].toIntOrNull() ?: 3)
                Transaction(id, date, type, amount, notes, mood)
            }
            // Old format: id|date|type|amount|notes|mood
            p.size >= 6 -> {
                val id = p[0]
                val date = p[1].toLongOrNull() ?: return null
                val type = runCatching { TransactionType.valueOf(p[2]) }.getOrDefault(TransactionType.EXPENSE)
                val amount = p[3].toDoubleOrNull() ?: return null
                val notes = p[4]
                val mood = Mood.fromScore(p[5].toIntOrNull() ?: 3)
                Transaction(id, date, type, amount, notes, mood)
            }
            else -> null
        }
    }

    fun fromLineSafe(line: String): Transaction? = runCatching { fromLine(line) }.getOrNull()
}

object RecurringLineCodec {
    private const val VER = "R1"

    fun toLine(rt: RecurringTransaction): String {
        val notes = LineCodecUtils.b64(rt.notes)
        return listOf(
            rt.id,
            VER,
            rt.type.name,
            rt.amount.toString(),
            notes,
            rt.mood.score.toString(),
            rt.frequency.name,
            rt.dayOfMonth.toString(),
            rt.monthOfYear.toString(),
            rt.lastGeneratedTime.toString()
        ).joinToString(LineCodecUtils.DELIM)
    }

    fun fromLine(line: String): RecurringTransaction? {
        val p = line.split(LineCodecUtils.DELIM)
        return when {
            // New format: id|VER|type|amount|notesB64|mood|freq|day|month|lastGen
            p.size >= 10 && p[1] == VER -> {
                val id = p[0]
                val type = runCatching { TransactionType.valueOf(p[2]) }.getOrDefault(TransactionType.EXPENSE)
                val amount = p[3].toDoubleOrNull() ?: return null
                val notes = LineCodecUtils.ub64(p[4])
                val mood = Mood.fromScore(p[5].toIntOrNull() ?: 3)
                val freq = runCatching { Frequency.valueOf(p[6]) }.getOrDefault(Frequency.MONTHLY)
                val day = p[7].toIntOrNull() ?: 1
                val month = p[8].toIntOrNull() ?: 1
                val lastGen = p[9].toLongOrNull() ?: 0L
                RecurringTransaction(id, type, amount, notes, mood, freq, day, month, lastGen)
            }
            // Old format: id|type|amount|notes|mood|freq|day|month|lastGen
            p.size >= 9 -> {
                val id = p[0]
                val type = runCatching { TransactionType.valueOf(p[1]) }.getOrDefault(TransactionType.EXPENSE)
                val amount = p[2].toDoubleOrNull() ?: return null
                val notes = p[3]
                val mood = Mood.fromScore(p[4].toIntOrNull() ?: 3)
                val freq = runCatching { Frequency.valueOf(p[5]) }.getOrDefault(Frequency.MONTHLY)
                val day = p[6].toIntOrNull() ?: 1
                val month = p[7].toIntOrNull() ?: 1
                val lastGen = p[8].toLongOrNull() ?: 0L
                RecurringTransaction(id, type, amount, notes, mood, freq, day, month, lastGen)
            }
            else -> null
        }
    }

    fun fromLineSafe(line: String): RecurringTransaction? = runCatching { fromLine(line) }.getOrNull()
}
