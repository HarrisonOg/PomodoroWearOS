package com.harrisonog.simplepomodoro.service

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

object HapticPatterns {
    val PHASE_START = longArrayOf(0, 50, 100, 50)
    val PHASE_COMPLETE = longArrayOf(0, 100, 150, 200, 150, 100)
    val PAUSE = longArrayOf(0, 30)

    private val PHASE_START_AMPLITUDES = intArrayOf(0, 128, 0, 128)
    private val PHASE_COMPLETE_AMPLITUDES = intArrayOf(0, 100, 120, 140, 120, 100)
    private val PAUSE_AMPLITUDES = intArrayOf(0, 100)

    fun Vibrator.gentleNotify(pattern: LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val amplitudes = when (pattern) {
                PHASE_START -> PHASE_START_AMPLITUDES
                PHASE_COMPLETE -> PHASE_COMPLETE_AMPLITUDES
                PAUSE -> PAUSE_AMPLITUDES
                else -> IntArray(pattern.size) { if (it % 2 == 0) 0 else 128 }
            }

            vibrate(
                VibrationEffect.createWaveform(
                    pattern,
                    amplitudes,
                    -1
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrate(pattern, -1)
        }
    }
}
