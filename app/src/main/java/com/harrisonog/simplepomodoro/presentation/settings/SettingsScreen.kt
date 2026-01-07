package com.harrisonog.simplepomodoro.presentation.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.harrisonog.simplepomodoro.data.model.PomodoroSettings

@Composable
fun SettingsScreen(
    currentSettings: PomodoroSettings,
    onSettingsChanged: (PomodoroSettings) -> Unit,
    onNavigateBack: () -> Unit
) {
    val listState = rememberScalingLazyListState()

    Scaffold(
        timeText = { TimeText() },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                ListHeader {
                    Text(
                        text = "Timer Length",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Chip(
                    label = {
                        Text(
                            text = "Standard",
                            style = MaterialTheme.typography.button
                        )
                    },
                    secondaryLabel = {
                        Text("25 min / 5 min")
                    },
                    onClick = {
                        onSettingsChanged(PomodoroSettings.STANDARD)
                        onNavigateBack()
                    },
                    colors = if (currentSettings == PomodoroSettings.STANDARD) {
                        ChipDefaults.primaryChipColors()
                    } else {
                        ChipDefaults.secondaryChipColors()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            item {
                Chip(
                    label = {
                        Text(
                            text = "Extended",
                            style = MaterialTheme.typography.button
                        )
                    },
                    secondaryLabel = {
                        Text("50 min / 10 min")
                    },
                    onClick = {
                        onSettingsChanged(PomodoroSettings.EXTENDED)
                        onNavigateBack()
                    },
                    colors = if (currentSettings == PomodoroSettings.EXTENDED) {
                        ChipDefaults.primaryChipColors()
                    } else {
                        ChipDefaults.secondaryChipColors()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }
}
