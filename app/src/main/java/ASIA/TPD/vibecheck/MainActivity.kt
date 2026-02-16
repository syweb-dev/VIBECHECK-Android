/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package ASIA.TPD.vibecheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ASIA.TPD.vibecheck.data.LocaleManager
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
import ASIA.TPD.vibecheck.data.TransactionViewModel
import ASIA.TPD.vibecheck.ui.screens.AddEntryScreen
import ASIA.TPD.vibecheck.ui.screens.AnalyticsScreen
import ASIA.TPD.vibecheck.ui.screens.DashboardScreen
import ASIA.TPD.vibecheck.ui.screens.SettingsScreen
import ASIA.TPD.vibecheck.ui.screens.OnboardingScreen
import ASIA.TPD.vibecheck.ui.theme.VIBECHECKTheme
import android.content.Context

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.applySavedLocale(this)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false) // Explicitly requested for Edge-to-Edge
        setContent {
            var isTableTop by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                WindowInfoTracker.getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
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
            }

            VIBECHECKTheme {
                val navController = rememberNavController()
                val viewModel: TransactionViewModel = viewModel()
                
                // Check if onboarding is completed
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
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
