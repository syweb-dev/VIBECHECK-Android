/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import top.lucanex.top.vibecheck.data.LocaleManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import kotlinx.coroutines.flow.catch
import top.lucanex.top.vibecheck.data.TransactionViewModel
import top.lucanex.top.vibecheck.ui.screens.AddEntryScreen
import top.lucanex.top.vibecheck.ui.screens.AnalyticsScreen
import top.lucanex.top.vibecheck.ui.screens.DashboardScreen
import top.lucanex.top.vibecheck.ui.screens.SettingsScreen
import top.lucanex.top.vibecheck.ui.screens.PrivacyScreen
import top.lucanex.top.vibecheck.ui.screens.OnboardingScreen
import top.lucanex.top.vibecheck.ui.theme.VIBECHECKTheme
import android.content.Context

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        LocaleManager.applySavedLocale(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            var isTableTop by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                runCatching {
                    WindowInfoTracker.getOrCreate(this@MainActivity)
                        .windowLayoutInfo(this@MainActivity)
                        .catch { isTableTop = false }
                        .collect { newLayoutInfo ->
                            val foldingFeature = newLayoutInfo.displayFeatures
                                .filterIsInstance<FoldingFeature>()
                                .firstOrNull()
                            isTableTop = if (foldingFeature != null) {
                                foldingFeature.state == FoldingFeature.State.HALF_OPENED &&
                                        foldingFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
                            } else {
                                false
                            }
                        }
                }.onFailure {
                    isTableTop = false
                }
            }

            VIBECHECKTheme {
                val navController = rememberNavController()
                val viewModel: TransactionViewModel = viewModel()
                
                val sharedPrefs = getSharedPreferences("vibe_prefs", Context.MODE_PRIVATE)
                val isOnboardingCompleted = sharedPrefs.getBoolean("onboarding_completed", false)
                val startDest = if (isOnboardingCompleted) "dashboard" else "onboarding"

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDest,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                onFinish = {
                                    sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
                                    navController.navigate("dashboard") {
                                        popUpTo("onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToAddEntry = { navController.navigate("add_entry") },
                                onNavigateToAnalytics = { navController.navigate("analytics") },
                                onNavigateToSettings = { navController.navigate("settings") }
                            )
                        }
                        composable("add_entry") {
                            AddEntryScreen(
                                viewModel = viewModel,
                                isTableTop = isTableTop,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable("analytics") {
                            AnalyticsScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                viewModel = viewModel,
                                onBackClick = { navController.popBackStack() },
                                onOpenPrivacy = { navController.navigate("privacy") }
                            )
                        }
                        composable("privacy") {
                            PrivacyScreen(
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
