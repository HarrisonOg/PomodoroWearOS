package com.harrisonog.simplepomodoro.presentation.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.harrisonog.simplepomodoro.data.model.Phase
import com.harrisonog.simplepomodoro.data.model.PomodoroState
import com.harrisonog.simplepomodoro.presentation.theme.BreakColor
import com.harrisonog.simplepomodoro.presentation.theme.FocusColor
import com.harrisonog.simplepomodoro.presentation.theme.TextColor
import java.util.Locale

@Composable
fun PomodoroScreen(
    state: PomodoroState,
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
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
                    totalMillis = state.totalMillis
                )
            }
            is PomodoroState.Paused -> {
                PausedTimerContent(
                    phase = state.phase,
                    remainingMillis = state.remainingMillis,
                    totalMillis = state.totalMillis
                )
            }
            is PomodoroState.Idle -> {
                IdleContent()
            }
        }
    }
}

@Composable
private fun RunningTimerContent(
    phase: Phase,
    remainingMillis: Long,
    totalMillis: Long
) {
    val progress = remainingMillis.toFloat() / totalMillis.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = animatedProgress,
            color = if (phase == Phase.FOCUS) FocusColor else BreakColor,
            modifier = Modifier.size(160.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTime(remainingMillis),
                style = MaterialTheme.typography.display1,
                color = TextColor,
                textAlign = TextAlign.Center
            )
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

@Composable
private fun PausedTimerContent(
    phase: Phase,
    remainingMillis: Long,
    totalMillis: Long
) {
    val progress = remainingMillis.toFloat() / totalMillis.toFloat()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            color = if (phase == Phase.FOCUS) FocusColor.copy(alpha = 0.5f) else BreakColor.copy(alpha = 0.5f),
            modifier = Modifier.size(160.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = formatTime(remainingMillis),
                style = MaterialTheme.typography.display1,
                color = TextColor.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
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

@Composable
private fun IdleContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Tap to start",
            style = MaterialTheme.typography.title1,
            color = TextColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Long press for settings",
            style = MaterialTheme.typography.caption1,
            color = TextColor.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
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
