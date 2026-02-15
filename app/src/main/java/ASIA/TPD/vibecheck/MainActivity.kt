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
import ASIA.TPD.vibecheck.data.TransactionViewModel
import ASIA.TPD.vibecheck.ui.screens.AddEntryScreen
import ASIA.TPD.vibecheck.ui.screens.AnalyticsScreen
import ASIA.TPD.vibecheck.ui.screens.DashboardScreen
import ASIA.TPD.vibecheck.ui.screens.SettingsScreen
import ASIA.TPD.vibecheck.ui.theme.VIBECHECKTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager.applySavedLocale(this)
        enableEdgeToEdge()
        setContent {
            VIBECHECKTheme {
                val navController = rememberNavController()
                val viewModel: TransactionViewModel = viewModel()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
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
