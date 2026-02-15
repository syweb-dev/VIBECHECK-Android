/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package ASIA.TPD.vibecheck.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ASIA.TPD.vibecheck.data.Mood
import ASIA.TPD.vibecheck.ui.theme.NeoBlack
import ASIA.TPD.vibecheck.ui.theme.NeoWhite
import ASIA.TPD.vibecheck.ui.theme.NeoYellow

@Composable
fun MoodPicker(
    selectedMood: Mood,
    onMoodSelected: (Mood) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Mood.entries.forEach { mood ->
            MoodItem(
                mood = mood,
                isSelected = mood == selectedMood,
                onClick = { onMoodSelected(mood) }
            )
        }
    }
}

@Composable
private fun MoodItem(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) NeoYellow else NeoWhite

    Box(
        modifier = Modifier
            .size(56.dp)
            .clickable(onClick = onClick)
    ) {
        // Shadow
        Box(
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.BottomEnd)
                .background(NeoBlack)
        )

        // Content
        Box(
            modifier = Modifier
                .size(52.dp)
                .align(Alignment.TopStart)
                .background(backgroundColor)
                .border(BorderStroke(2.dp, NeoBlack)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = mood.emoji, fontSize = 24.sp)
        }
    }
}
