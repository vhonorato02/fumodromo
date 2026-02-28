package com.fumodromo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fumodromo.ui.navigation.NavRoutes
import com.fumodromo.ui.screens.HomeScreen
import com.fumodromo.ui.screens.LogsScreen
import com.fumodromo.ui.screens.OnboardingScreen
import com.fumodromo.ui.screens.SettingsScreen
import com.fumodromo.ui.screens.StatsScreen
import com.fumodromo.ui.theme.FumodromoTheme
import com.fumodromo.util.NotificationWorker

class MainActivity : ComponentActivity() {
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                NotificationWorker.agendar(this)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = AppContainer(this)
        garantirPermissaoOuAgendarNotificacoes()

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

    private fun garantirPermissaoOuAgendarNotificacoes() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            NotificationWorker.agendar(this)
            return
        }

        val permissaoJaConcedida = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (permissaoJaConcedida) {
            NotificationWorker.agendar(this)
        } else {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
