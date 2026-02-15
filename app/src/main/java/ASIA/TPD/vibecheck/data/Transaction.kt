/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package ASIA.TPD.vibecheck.data

import java.util.UUID

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Mood(val emoji: String, val score: Int) {
    VERY_HAPPY("😄", 5),
    HAPPY("🙂", 4),
    NEUTRAL("😐", 3),
    SAD("😔", 2),
    VERY_SAD("😢", 1);

    companion object {
        fun fromScore(score: Int): Mood {
            return entries.find { it.score == score } ?: NEUTRAL
        }
    }
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val date: Long, // Epoch millis
    val type: TransactionType,
    val amount: Double,
    val notes: String,
    val mood: Mood
)
