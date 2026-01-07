package com.harrisonog.simplepomodoro.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.harrisonog.simplepomodoro.presentation.navigation.PomodoroNavHost
import com.harrisonog.simplepomodoro.presentation.theme.SimplePomodoroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    SimplePomodoroTheme {
        val navController = rememberSwipeDismissableNavController()
        PomodoroNavHost(navController = navController)
    }
}