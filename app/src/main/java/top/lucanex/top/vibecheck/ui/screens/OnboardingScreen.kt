/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.ui.screens

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
import top.lucanex.top.vibecheck.R
import top.lucanex.top.vibecheck.ui.components.VibeButton
import top.lucanex.top.vibecheck.ui.theme.NeoBackground
import top.lucanex.top.vibecheck.ui.theme.NeoBlack
import top.lucanex.top.vibecheck.ui.theme.NeoGreen
import java.io.File
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import top.lucanex.top.vibecheck.data.OnboardingViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val currentPage by viewModel.currentPage.collectAsState()
    val pagerState = rememberPagerState(initialPage = currentPage, pageCount = { 3 })
    
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { 
             viewModel.onPageChanged(it)
        }
    }
    
    val context = LocalContext.current
    
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
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = sysStr(R.string.onboarding_currency_note),
                            style = MaterialTheme.typography.bodyMedium,
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

        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
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
            Spacer(modifier = Modifier.height(56.dp)) 
        }
    }
}

private fun getSystemString(context: Context, resId: Int, vararg args: Any): String {
    val systemConfig = Resources.getSystem().configuration
    val config = Configuration(context.resources.configuration)
    val locale = if (systemConfig.locales.isEmpty) Locale.getDefault() else systemConfig.locales[0]
    config.setLocale(locale)
    
    val localizedContext = context.createConfigurationContext(config)
    return if (args.isEmpty()) {
        localizedContext.getString(resId)
    } else {
        localizedContext.getString(resId, *args)
    }
}
