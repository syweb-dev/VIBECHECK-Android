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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
 import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ASIA.TPD.vibecheck.ui.theme.NeoBlack
import ASIA.TPD.vibecheck.ui.theme.NeoWhite
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.toSize

@Composable
fun VibeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
     color: Color = NeoWhite,
     enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val shadowOffset = if (isPressed) 0.dp else 4.dp
    val contentOffset = if (isPressed) 4.dp else 0.dp

    // Single surface with a drawn hard-offset shadow exactly matching surface size
    Box(
        modifier = modifier
            .drawBehind {
                val px = shadowOffset.toPx()
                drawRect(
                    color = NeoBlack,
                    topLeft = androidx.compose.ui.geometry.Offset(px, px),
                    size = this.size
                )
            }
            .background(color)
            .border(BorderStroke(2.dp, NeoBlack))
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else {
                    Modifier
                }
            )
            .alpha(if (enabled) 1f else 0.6f)
            .padding(horizontal = 24.dp, vertical = 12.dp)
            ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (enabled) NeoBlack else Color.Gray,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}
