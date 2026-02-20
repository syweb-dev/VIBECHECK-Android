/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.data

import org.junit.Assert.*
import org.junit.Test
import java.util.UUID

class LineCodecsTest {

    @Test
    fun `TransactionLineCodec should serialize and deserialize correctly`() {
        val original = Transaction(
            id = UUID.randomUUID().toString(),
            date = System.currentTimeMillis(),
            type = TransactionType.EXPENSE,
            amount = 123.45,
            notes = "Test notes with | pipe and \n newline and emoji 😊",
            mood = Mood.HAPPY
        )

        val line = TransactionLineCodec.toLine(original)
        val deserialized = TransactionLineCodec.fromLine(line)

        assertNotNull(deserialized)
        assertEquals(original.id, deserialized?.id)
        assertEquals(original.date, deserialized?.date)
        assertEquals(original.type, deserialized?.type)
        assertEquals(original.amount, deserialized?.amount ?: 0.0, 0.001)
        assertEquals(original.notes, deserialized?.notes)
        assertEquals(original.mood, deserialized?.mood)
    }

    @Test
    fun `TransactionLineCodec should handle old format correctly`() {
        val id = UUID.randomUUID().toString()
        val date = 1678888888000L
        val type = "EXPENSE"
        val amount = "50.0"
        val notes = "Old format notes"
        val moodScore = "4"
        
        // Old format: id|date|type|amount|notes|mood
        val oldLine = "$id|$date|$type|$amount|$notes|$moodScore"
        
        val deserialized = TransactionLineCodec.fromLine(oldLine)

        assertNotNull(deserialized)
        assertEquals(id, deserialized?.id)
        assertEquals(date, deserialized?.date)
        assertEquals(TransactionType.EXPENSE, deserialized?.type)
        assertEquals(50.0, deserialized?.amount ?: 0.0, 0.001)
        assertEquals(notes, deserialized?.notes)
        assertEquals(Mood.HAPPY, deserialized?.mood)
    }

    @Test
    fun `RecurringLineCodec should serialize and deserialize correctly`() {
        val original = RecurringTransaction(
            id = UUID.randomUUID().toString(),
            type = TransactionType.INCOME,
            amount = 2000.0,
            notes = "Monthly salary | bonus",
            mood = Mood.VERY_HAPPY,
            frequency = Frequency.MONTHLY,
            dayOfMonth = 15,
            monthOfYear = 1,
            lastGeneratedTime = System.currentTimeMillis()
        )

        val line = RecurringLineCodec.toLine(original)
        val deserialized = RecurringLineCodec.fromLine(line)

        assertNotNull(deserialized)
        assertEquals(original.id, deserialized?.id)
        assertEquals(original.type, deserialized?.type)
        assertEquals(original.amount, deserialized?.amount ?: 0.0, 0.001)
        assertEquals(original.notes, deserialized?.notes)
        assertEquals(original.mood, deserialized?.mood)
        assertEquals(original.frequency, deserialized?.frequency)
        assertEquals(original.dayOfMonth, deserialized?.dayOfMonth)
        assertEquals(original.monthOfYear, deserialized?.monthOfYear)
        assertEquals(original.lastGeneratedTime, deserialized?.lastGeneratedTime)
    }

    @Test
    fun `RecurringLineCodec should handle old format correctly`() {
        val id = UUID.randomUUID().toString()
        val type = "INCOME"
        val amount = "1000.0"
        val notes = "Old recurring notes"
        val moodScore = "5" // VERY_HAPPY
        val freq = "MONTHLY"
        val day = "1"
        val month = "1"
        val lastGen = "0"

        // Old format: id|type|amount|notes|mood|freq|day|month|lastGen
        val oldLine = "$id|$type|$amount|$notes|$moodScore|$freq|$day|$month|$lastGen"

        val deserialized = RecurringLineCodec.fromLine(oldLine)

        assertNotNull(deserialized)
        assertEquals(id, deserialized?.id)
        assertEquals(TransactionType.INCOME, deserialized?.type)
        assertEquals(1000.0, deserialized?.amount ?: 0.0, 0.001)
        assertEquals(notes, deserialized?.notes)
        assertEquals(Mood.VERY_HAPPY, deserialized?.mood)
        assertEquals(Frequency.MONTHLY, deserialized?.frequency)
    }
    
    @Test
    fun `Codec should return null for invalid lines`() {
        assertNull(TransactionLineCodec.fromLine("invalid|line"))
        assertNull(TransactionLineCodec.fromLine(""))
        assertNull(RecurringLineCodec.fromLine("invalid|line|recurring"))
    }
}
