package top.lucanex.top.vibecheck.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import top.lucanex.top.vibecheck.ui.components.VibeButton
import top.lucanex.top.vibecheck.ui.theme.NeoBackground
import top.lucanex.top.vibecheck.ui.theme.NeoBlack
import top.lucanex.top.vibecheck.ui.theme.NeoWhite
import top.lucanex.top.vibecheck.R
import top.lucanex.top.vibecheck.data.LocaleManager
import java.util.Locale

@Composable
fun PrivacyScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val localeTag = remember {
        val saved = LocaleManager.getSavedLocaleTag(context)
        if (saved == "system") {
            Locale.getDefault().toLanguageTag()
        } else saved
    }

    val content = remember(localeTag) { loadPrivacyText(context, localeTag) }

    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeoBackground)
            .padding(16.dp)
            .verticalScroll(scroll)
    ) {
        VibeButton(
            text = context.getString(R.string.back),
            onClick = onBackClick,
            color = NeoWhite
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = context.getString(R.string.privacy_policy),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = NeoBlack
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = NeoBlack,
            textAlign = TextAlign.Start
        )
    }
}

private fun loadPrivacyText(context: Context, tag: String): String {
    val candidates = buildList {
        add("privacy_${tag}.txt")
        val language = tag.substringBefore('-')
        add("privacy_${language}.txt")
        add("privacy_zh-CN.txt")
    }
    for (name in candidates) {
        try {
            context.assets.open(name).bufferedReader(Charsets.UTF_8).use {
                return it.readText()
            }
        } catch (_: Exception) {
        }
    }
    return "Privacy Policy not available."
}
