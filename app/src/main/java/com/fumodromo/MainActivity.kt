package com.fumodromo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fumodromo.ui.navigation.NavRoutes
import com.fumodromo.util.NotificationWorker
import com.fumodromo.ui.screens.HomeScreen
import com.fumodromo.ui.screens.LogsScreen
import com.fumodromo.ui.screens.OnboardingScreen
import com.fumodromo.ui.screens.SettingsScreen
import com.fumodromo.ui.screens.StatsScreen
import com.fumodromo.ui.theme.FumodromoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = AppContainer(this)
        NotificationWorker.agendar(this)

        setContent {
            FumodromoTheme {
                val vm: FumoViewModel = viewModel(factory = FumoViewModelFactory(container.repository))
                val nav = rememberNavController()
                val state by vm.state.collectAsStateWithLifecycle()

                NavHost(
                    navController = nav,
                    startDestination = if (state.perfil.onboardingConcluido) NavRoutes.Home else NavRoutes.Onboarding,
                ) {
                    composable(NavRoutes.Onboarding) {
                        OnboardingScreen(vm) { nav.navigate(NavRoutes.Home) { popUpTo(NavRoutes.Onboarding) { inclusive = true } } }
                    }
                    composable(NavRoutes.Home) { HomeScreen(vm) { nav.navigate(it) } }
                    composable(NavRoutes.Stats) { StatsScreen(vm) }
                    composable(NavRoutes.Logs) { LogsScreen(vm) }
                    composable(NavRoutes.Settings) { SettingsScreen(vm) }
                }
            }
        }
    }
}
