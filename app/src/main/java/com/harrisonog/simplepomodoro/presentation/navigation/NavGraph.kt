package com.harrisonog.simplepomodoro.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import com.harrisonog.simplepomodoro.presentation.PomodoroViewModel
import com.harrisonog.simplepomodoro.presentation.settings.SettingsScreen
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
            val currentSettings by viewModel.settings.collectAsState()

            SettingsScreen(
                currentSettings = currentSettings,
                onSettingsChanged = { newSettings ->
                    viewModel.updateSettings(newSettings)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
