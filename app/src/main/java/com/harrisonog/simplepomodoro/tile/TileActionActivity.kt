package com.harrisonog.simplepomodoro.tile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.harrisonog.simplepomodoro.service.PomodoroService

class TileActionActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.getStringExtra("action")
        if (action != null) {
            val serviceIntent = Intent(this, PomodoroService::class.java).apply {
                this.action = action
            }
            startForegroundService(serviceIntent)
        }

        finish()
    }
}
