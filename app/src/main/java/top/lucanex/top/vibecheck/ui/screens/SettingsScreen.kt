/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import top.lucanex.top.vibecheck.data.TransactionViewModel
import top.lucanex.top.vibecheck.data.LocaleManager
import top.lucanex.top.vibecheck.ui.components.VibeButton
import top.lucanex.top.vibecheck.ui.theme.NeoBackground
import top.lucanex.top.vibecheck.ui.theme.NeoPink
import top.lucanex.top.vibecheck.ui.theme.NeoWhite
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import top.lucanex.top.vibecheck.R
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import top.lucanex.top.vibecheck.ui.theme.NeoYellow
import top.lucanex.top.vibecheck.ui.theme.NeoBlack

import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.clickable
import java.io.File
import top.lucanex.top.vibecheck.ui.theme.NeoGreen

@Composable
fun SettingsScreen(
    viewModel: TransactionViewModel,
    onBackClick: () -> Unit,
    onOpenPrivacy: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var current by remember { mutableStateOf(LocaleManager.getSavedLocaleTag(context)) }
    var showConfirm by remember { mutableStateOf(false) }
    var confirmText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val filePath = remember {
        File(context.filesDir, "accounting_records.txt").absolutePath
    }

    Scaffold(
        containerColor = NeoBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = NeoYellow,
                    contentColor = NeoBlack
                ) { Text(data.visuals.message) }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                VibeButton(
                    text = stringResource(id = R.string.back),
                    onClick = onBackClick,
                    color = NeoWhite
                )
            }

            Text(
                text = stringResource(id = R.string.settings),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VibeButton(
                    text = stringResource(id = R.string.privacy_policy),
                    onClick = onOpenPrivacy,
                    color = NeoWhite,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VibeButton(
                        text = stringResource(id = R.string.follow_system),
                        onClick = {
                            current = "system"
                            LocaleManager.setLocale(context, "system")
                        },
                        color = if (current == "system") NeoWhite else NeoWhite,
                        modifier = Modifier.weight(1f)
                    )
                    VibeButton(
                        text = "简体中文",
                        onClick = {
                            current = "zh-CN"
                            LocaleManager.setLocale(context, "zh-CN")
                        },
                        color = NeoWhite,
                        modifier = Modifier.weight(1f)
                    )
                    VibeButton(
                        text = "English",
                        onClick = {
                            current = "en"
                            LocaleManager.setLocale(context, "en")
                        },
                        color = NeoWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    VibeButton(
                        text = "繁體中文",
                        onClick = {
                            current = "zh-TW"
                            LocaleManager.setLocale(context, "zh-TW")
                        },
                        color = NeoWhite,
                        modifier = Modifier.weight(1f)
                    )
                    VibeButton(
                        text = "한국어",
                        onClick = {
                            current = "ko"
                            LocaleManager.setLocale(context, "ko")
                        },
                        color = NeoWhite,
                        modifier = Modifier.weight(1f)
                    )
                    VibeButton(
                        text = "日本語",
                        onClick = {
                            current = "ja"
                            LocaleManager.setLocale(context, "ja")
                        },
                        color = NeoWhite,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            
            VibeButton(
                text = stringResource(id = R.string.onboarding_replay),
                onClick = {
                    val prefs = context.getSharedPreferences("vibe_prefs", android.content.Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("onboarding_completed", false).commit()
                    val pm = context.packageManager
                    val launch = pm.getLaunchIntentForPackage("top.lucanex.top.vibecheck")
                        ?: pm.getLaunchIntentForPackage(context.packageName)
                    launch?.addFlags(
                        android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                android.content.Intent.FLAG_ACTIVITY_NEW_TASK or
                                android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    if (launch != null) context.startActivity(launch)
                    if (context is android.app.Activity) context.finish()
                },
                color = top.lucanex.top.vibecheck.ui.theme.NeoGreen,
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        clipboardManager.setText(AnnotatedString(filePath))
                        scope.launch {
                            snackbarHostState.showSnackbar(context.getString(R.string.copied_to_clipboard))
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.storage_location),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = NeoBlack
                )
                Text(
                    text = filePath,
                    style = MaterialTheme.typography.bodySmall,
                    color = NeoBlack,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.storage_location_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }

            VibeButton(
                text = stringResource(id = R.string.reset_data),
                onClick = {
                    showConfirm = true
                },
                color = NeoPink,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(id = R.string.copyright),
                style = MaterialTheme.typography.labelSmall,
                color = NeoBlack
            )

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(text = stringResource(id = R.string.confirm_reset_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(id = R.string.confirm_reset_message))
                    TextField(
                        value = confirmText,
                        onValueChange = { confirmText = it },
                        singleLine = true,
                        label = { Text(text = stringResource(id = R.string.type_delete_hint)) }
                    )
                }
            },
            confirmButton = {
                VibeButton(
                    text = stringResource(id = R.string.confirm_reset),
                    onClick = {
                        if (confirmText == "delete") {
                            showConfirm = false
                            confirmText = ""
                            viewModel.clearAll()
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = context.getString(R.string.reset_success)
                                )
                            }
                        }
                    },
                    enabled = confirmText == "delete",
                    color = NeoPink
                )
            },
            dismissButton = {
                VibeButton(
                    text = stringResource(id = R.string.cancel),
                    onClick = { showConfirm = false }
                )
            }
        )
    }
}
