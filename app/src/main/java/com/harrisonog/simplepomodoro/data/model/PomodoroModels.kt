package com.harrisonog.simplepomodoro.data.model

enum class Phase {
    FOCUS, BREAK
}

data class PomodoroSettings(
    val focusMinutes: Int = 25,
    val breakMinutes: Int = 5
) {
    companion object {
        val STANDARD = PomodoroSettings(25, 5)
        val EXTENDED = PomodoroSettings(50, 10)
    }
}

sealed class PomodoroState {
    data object Idle : PomodoroState()

    data class Running(
        val phase: Phase,
        val remainingMillis: Long,
        val totalMillis: Long
    ) : PomodoroState()

    data class Paused(
        val phase: Phase,
        val remainingMillis: Long,
        val totalMillis: Long
    ) : PomodoroState()
}
