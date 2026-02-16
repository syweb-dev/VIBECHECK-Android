/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import top.lucanex.top.vibecheck.ui.theme.NeoBlack
import top.lucanex.top.vibecheck.ui.theme.NeoWhite

@Composable
fun VibeCard(
    modifier: Modifier = Modifier,
    color: Color = NeoWhite,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                val px = 4.dp.toPx()
                drawRect(
                    color = NeoBlack,
                    topLeft = androidx.compose.ui.geometry.Offset(px, px),
                    size = this.size
                )
            }
            .background(color)
            .border(BorderStroke(2.dp, NeoBlack))
            .padding(16.dp)
    ) {
        content()
    }
}
