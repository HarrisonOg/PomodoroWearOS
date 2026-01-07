package com.harrisonog.simplepomodoro.presentation.timer

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.harrisonog.simplepomodoro.data.model.Phase
import com.harrisonog.simplepomodoro.data.model.PomodoroState
import com.harrisonog.simplepomodoro.presentation.theme.BreakColor
import com.harrisonog.simplepomodoro.presentation.theme.FocusColor
import com.harrisonog.simplepomodoro.presentation.theme.TextColor
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun PomodoroScreen(
    state: PomodoroState,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    ambientMode: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (ambientMode) Color.Black else MaterialTheme.colors.background)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onTap() },
                    onLongPress = { onLongPress() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is PomodoroState.Running -> {
                RunningTimerContent(
                    phase = state.phase,
                    remainingMillis = state.remainingMillis,
                    totalMillis = state.totalMillis,
                    ambientMode = ambientMode
                )
            }
            is PomodoroState.Paused -> {
                PausedTimerContent(
                    phase = state.phase,
                    remainingMillis = state.remainingMillis,
                    totalMillis = state.totalMillis,
                    ambientMode = ambientMode
                )
            }
            is PomodoroState.Idle -> {
                IdleContent(ambientMode = ambientMode)
            }
        }
    }
}

@Composable
private fun RunningTimerContent(
    phase: Phase,
    remainingMillis: Long,
    totalMillis: Long,
    ambientMode: Boolean
) {
    val progress = remainingMillis.toFloat() / totalMillis.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = if (ambientMode) tween(durationMillis = 0) else tween(durationMillis = 1000),
        label = "progress"
    )

    val color = if (ambientMode) {
        Color.White
    } else {
        if (phase == Phase.FOCUS) FocusColor else BreakColor
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!ambientMode) {
            CircularProgressIndicator(
                progress = animatedProgress,
                color = color,
                modifier = Modifier.size(160.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTime(remainingMillis),
                style = MaterialTheme.typography.display1,
                color = if (ambientMode) Color.White else TextColor,
                textAlign = TextAlign.Center
            )
            if (!ambientMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (phase == Phase.FOCUS) "Focus" else "Rest",
                    style = MaterialTheme.typography.title3,
                    color = TextColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun PausedTimerContent(
    phase: Phase,
    remainingMillis: Long,
    totalMillis: Long,
    ambientMode: Boolean
) {
    val progress = remainingMillis.toFloat() / totalMillis.toFloat()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!ambientMode) {
            CircularProgressIndicator(
                progress = progress,
                color = if (phase == Phase.FOCUS) FocusColor.copy(alpha = 0.5f) else BreakColor.copy(alpha = 0.5f),
                modifier = Modifier.size(160.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTime(remainingMillis),
                style = MaterialTheme.typography.display1,
                color = if (ambientMode) Color.White else TextColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            if (!ambientMode) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Paused",
                    style = MaterialTheme.typography.title3,
                    color = TextColor.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to resume",
                    style = MaterialTheme.typography.caption1,
                    color = TextColor.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun IdleContent(ambientMode: Boolean) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("pomodoro_prefs", 0) }
    val hasSeenHint = remember { prefs.getBoolean("has_seen_hint", false) }
    var showHint by remember { mutableStateOf(!hasSeenHint && !ambientMode) }

    LaunchedEffect(showHint) {
        if (showHint) {
            delay(5000) // Hide hint after 5 seconds
            showHint = false
            prefs.edit().putBoolean("has_seen_hint", true).apply()
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "hint")
    val hintAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hintAlpha"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (ambientMode) "Tap" else "Tap to start",
            style = MaterialTheme.typography.title1,
            color = if (ambientMode) Color.White else TextColor,
            textAlign = TextAlign.Center
        )
        if (!ambientMode) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Long press for settings",
                style = MaterialTheme.typography.caption1,
                color = if (showHint) {
                    TextColor.copy(alpha = hintAlpha)
                } else {
                    TextColor.copy(alpha = 0.5f)
                },
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CircularProgressIndicator(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val diameter = size.minDimension
        val radius = diameter / 2f
        val topLeft = Offset(
            x = (size.width - diameter) / 2f,
            y = (size.height - diameter) / 2f
        )

        // Background circle
        drawCircle(
            color = color.copy(alpha = 0.2f),
            radius = radius - strokeWidth / 2,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Progress arc
        val sweepAngle = 360f * progress
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = Size(diameter, diameter),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds)
}
