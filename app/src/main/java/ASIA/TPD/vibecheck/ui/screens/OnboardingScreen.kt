/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package ASIA.TPD.vibecheck.ui.screens

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ASIA.TPD.vibecheck.R
import ASIA.TPD.vibecheck.ui.components.VibeButton
import ASIA.TPD.vibecheck.ui.theme.NeoBackground
import ASIA.TPD.vibecheck.ui.theme.NeoBlack
import ASIA.TPD.vibecheck.ui.theme.NeoGreen
import java.io.File
import java.util.Locale

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val context = LocalContext.current
    
    // Helper to get strings in SYSTEM locale
    fun sysStr(id: Int, vararg args: Any): String {
        return getSystemString(context, id, *args)
    }

    val filePath = remember {
        File(context.filesDir, "accounting_records.txt").absolutePath
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (page) {
                    0 -> {
                        Text(
                            text = sysStr(R.string.onboarding_welcome_title),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeoBlack,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = sysStr(R.string.onboarding_welcome_desc),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeoBlack,
                            textAlign = TextAlign.Center
                        )
                    }
                    1 -> {
                        Text(
                            text = sysStr(R.string.onboarding_local_title),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeoBlack,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = sysStr(R.string.onboarding_local_desc),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeoBlack,
                            textAlign = TextAlign.Center
                        )
                    }
                    2 -> {
                        Text(
                            text = sysStr(R.string.onboarding_storage_title),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = NeoBlack,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = sysStr(R.string.onboarding_storage_desc, filePath),
                            style = MaterialTheme.typography.bodyLarge,
                            color = NeoBlack,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            repeat(3) { iteration ->
                val color = if (pagerState.currentPage == iteration) NeoBlack else NeoBlack.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(12.dp)
                        .background(color)
                )
            }
        }

        if (pagerState.currentPage == 2) {
            VibeButton(
                text = sysStr(R.string.onboarding_start),
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth(),
                color = NeoGreen
            )
        } else {
            // Placeholder space for button
            Spacer(modifier = Modifier.height(56.dp)) 
        }
    }
}

private fun getSystemString(context: Context, resId: Int, vararg args: Any): String {
    // We create a configuration based on the System's global configuration
    // This bypasses any app-level overrides (like LocaleManager)
    val systemConfig = Resources.getSystem().configuration
    val config = Configuration(context.resources.configuration)
    config.setLocale(systemConfig.locales.get(0))
    
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.getString(resId, *args)
}
