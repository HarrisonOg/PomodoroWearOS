package com.harrisonog.simplepomodoro.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.harrisonog.simplepomodoro.R
import com.harrisonog.simplepomodoro.data.model.Phase
import com.harrisonog.simplepomodoro.data.model.PomodoroSettings
import com.harrisonog.simplepomodoro.data.model.PomodoroState
import com.harrisonog.simplepomodoro.presentation.MainActivity
import com.harrisonog.simplepomodoro.service.HapticPatterns.gentleNotify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class PomodoroService : Service() {

    private val binder = PomodoroServiceBinder()

    private val _state = MutableStateFlow<PomodoroState>(PomodoroState.Idle)
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private var countDownTimer: CountDownTimer? = null
    private val vibrator by lazy { getSystemService(Vibrator::class.java) }
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    private var currentSettings: PomodoroSettings = PomodoroSettings.STANDARD
    private var onCompleteCallback: (() -> Unit)? = null

    inner class PomodoroServiceBinder : Binder() {
        fun getService(): PomodoroService = this@PomodoroService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> togglePauseResume()
            ACTION_RESUME -> togglePauseResume()
            ACTION_STOP -> stopPomodoro()
        }
        return START_STICKY
    }

    fun startPomodoro(settings: PomodoroSettings) {
        currentSettings = settings
        startForeground(NOTIFICATION_ID, createNotification("Starting...", Phase.FOCUS))
        startFocusPhase(settings)
    }

    fun togglePauseResume() {
        when (val currentState = _state.value) {
            is PomodoroState.Running -> {
                countDownTimer?.cancel()
                vibrator.gentleNotify(HapticPatterns.PAUSE)
                _state.value = PomodoroState.Paused(
                    phase = currentState.phase,
                    remainingMillis = currentState.remainingMillis,
                    totalMillis = currentState.totalMillis
                )
                updateNotification("Paused", currentState.phase)
            }
            is PomodoroState.Paused -> {
                vibrator.gentleNotify(HapticPatterns.PAUSE)
                startTimer(
                    durationMillis = currentState.remainingMillis,
                    phase = currentState.phase,
                    totalMillis = currentState.totalMillis,
                    onComplete = {
                        if (currentState.phase == Phase.FOCUS) {
                            startBreakPhase(currentSettings)
                        } else {
                            startFocusPhase(currentSettings)
                        }
                    }
                )
            }
            is PomodoroState.Idle -> {
                startPomodoro(currentSettings)
            }
        }
    }

    fun stopPomodoro() {
        countDownTimer?.cancel()
        _state.value = PomodoroState.Idle
        try {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } catch (e: Exception) {
            // Already stopped
        }
        stopSelf()
    }

    private fun startFocusPhase(settings: PomodoroSettings) {
        vibrator.gentleNotify(HapticPatterns.PHASE_START)
        val durationMillis = settings.focusMinutes * 60 * 1000L
        startTimer(
            durationMillis = durationMillis,
            phase = Phase.FOCUS,
            totalMillis = durationMillis,
            onComplete = { startBreakPhase(settings) }
        )
    }

    private fun startBreakPhase(settings: PomodoroSettings) {
        vibrator.gentleNotify(HapticPatterns.PHASE_COMPLETE)
        val durationMillis = settings.breakMinutes * 60 * 1000L
        startTimer(
            durationMillis = durationMillis,
            phase = Phase.BREAK,
            totalMillis = durationMillis,
            onComplete = { startFocusPhase(settings) }
        )
    }

    private fun startTimer(
        durationMillis: Long,
        phase: Phase,
        totalMillis: Long,
        onComplete: () -> Unit
    ) {
        onCompleteCallback = onComplete
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _state.value = PomodoroState.Running(
                    phase = phase,
                    remainingMillis = millisUntilFinished,
                    totalMillis = totalMillis
                )
                updateNotification(formatTime(millisUntilFinished), phase)
            }

            override fun onFinish() {
                onCompleteCallback?.invoke()
            }
        }.start()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pomodoro Timer",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Shows the current pomodoro timer status"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(contentText: String, phase: Phase): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val phaseText = if (phase == Phase.FOCUS) "Focus" else "Break"

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(phaseText)
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)

        // Add pause/resume action
        when (_state.value) {
            is PomodoroState.Running -> {
                val pauseIntent = Intent(this, PomodoroService::class.java).apply {
                    action = ACTION_PAUSE
                }
                val pausePendingIntent = PendingIntent.getService(
                    this,
                    0,
                    pauseIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                builder.addAction(
                    R.drawable.ic_launcher_foreground,
                    "Pause",
                    pausePendingIntent
                )
            }
            is PomodoroState.Paused -> {
                val resumeIntent = Intent(this, PomodoroService::class.java).apply {
                    action = ACTION_RESUME
                }
                val resumePendingIntent = PendingIntent.getService(
                    this,
                    0,
                    resumeIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
                builder.addAction(
                    R.drawable.ic_launcher_foreground,
                    "Resume",
                    resumePendingIntent
                )
            }
            else -> {}
        }

        // Add stop action for all states except Idle
        if (_state.value !is PomodoroState.Idle) {
            val stopIntent = Intent(this, PomodoroService::class.java).apply {
                action = ACTION_STOP
            }
            val stopPendingIntent = PendingIntent.getService(
                this,
                1,
                stopIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            builder.addAction(
                R.drawable.ic_launcher_foreground,
                "Stop",
                stopPendingIntent
            )
        }

        return builder.build()
    }

    private fun updateNotification(contentText: String, phase: Phase) {
        val notification = createNotification(contentText, phase)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    companion object {
        private const val CHANNEL_ID = "pomodoro_channel"
        private const val NOTIFICATION_ID = 1
        private const val ACTION_PAUSE = "com.harrisonog.simplepomodoro.ACTION_PAUSE"
        private const val ACTION_RESUME = "com.harrisonog.simplepomodoro.ACTION_RESUME"
        private const val ACTION_STOP = "com.harrisonog.simplepomodoro.ACTION_STOP"
    }
}
