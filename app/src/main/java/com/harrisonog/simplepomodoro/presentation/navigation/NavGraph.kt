package com.harrisonog.simplepomodoro.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.harrisonog.simplepomodoro.presentation.PomodoroViewModel
import com.harrisonog.simplepomodoro.presentation.timer.PomodoroScreen

sealed class Screen(val route: String) {
    data object Timer : Screen("timer")
    data object Settings : Screen("settings")
}

@Composable
fun PomodoroNavHost(
    navController: NavHostController,
    viewModel: PomodoroViewModel = viewModel()
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        viewModel.bindService(context)
        onDispose {
            viewModel.unbindService(context)
        }
    }

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.Timer.route
    ) {
        composable(Screen.Timer.route) {
            val state by viewModel.state.collectAsState()

            PomodoroScreen(
                state = state,
                onTap = {
                    if (state is com.harrisonog.simplepomodoro.data.model.PomodoroState.Idle) {
                        viewModel.startPomodoro(context)
                    } else {
                        viewModel.togglePauseResume()
                    }
                },
                onLongPress = { navController.navigate(Screen.Settings.route) }
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
private fun SettingsScreenPlaceholder(onNavigateBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Settings Screen - Coming in Phase 4")
    }
}
