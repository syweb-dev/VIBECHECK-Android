/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = NeoYellow,
    onPrimary = NeoBlack,
    secondary = NeoPink,
    onSecondary = NeoBlack,
    tertiary = NeoGreen,
    onTertiary = NeoBlack,
    background = NeoBlack,
    onBackground = NeoWhite,
    surface = NeoBlack,
    onSurface = NeoWhite,
    surfaceVariant = NeoBlack,
    onSurfaceVariant = NeoWhite,
)

private val LightColorScheme = lightColorScheme(
    primary = NeoYellow,
    onPrimary = NeoBlack,
    secondary = NeoPink,
    onSecondary = NeoBlack,
    tertiary = NeoGreen,
    onTertiary = NeoBlack,
    background = NeoWhite,
    onBackground = NeoBlack,
    surface = NeoWhite,
    onSurface = NeoBlack,
    surfaceVariant = NeoWhite,
    onSurfaceVariant = NeoBlack,
)

@Composable
fun VIBECHECKTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    // We disable dynamic color by default to enforce the Neo-Brutalism theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
