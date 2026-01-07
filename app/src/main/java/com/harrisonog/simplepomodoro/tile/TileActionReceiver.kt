package com.harrisonog.simplepomodoro.tile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.harrisonog.simplepomodoro.service.PomodoroService

class TileActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, PomodoroService::class.java).apply {
            action = intent.action
        }
        context.startForegroundService(serviceIntent)
    }
}
