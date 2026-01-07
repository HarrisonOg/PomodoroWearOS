package com.harrisonog.simplepomodoro.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable

sealed class Screen(val route: String) {
    data object Timer : Screen("timer")
    data object Settings : Screen("settings")
}

@Composable
fun PomodoroNavHost(
    navController: NavHostController
) {
    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ) {
        composable(Screen.Timer.route) {
            // Timer screen will be added in Phase 3
            TimerScreenPlaceholder(
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Settings.route) {
            // Settings screen will be added in Phase 4
            SettingsScreenPlaceholder(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun TimerScreenPlaceholder(onNavigateToSettings: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Timer Screen - Coming in Phase 3")
    }
}

@Composable
private fun SettingsScreenPlaceholder(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Settings Screen - Coming in Phase 4")
    }
}
