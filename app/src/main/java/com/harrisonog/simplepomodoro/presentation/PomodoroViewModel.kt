package com.harrisonog.simplepomodoro.presentation

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.harrisonog.simplepomodoro.data.model.PomodoroSettings
import com.harrisonog.simplepomodoro.data.model.PomodoroState
import com.harrisonog.simplepomodoro.data.repository.SettingsRepository
import com.harrisonog.simplepomodoro.service.PomodoroService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    val settings: StateFlow<PomodoroSettings> = settingsRepository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PomodoroSettings.STANDARD
        )

    private val _state = MutableStateFlow<PomodoroState>(PomodoroState.Idle)
    val state: StateFlow<PomodoroState> = _state.asStateFlow()

    private var pomodoroService: PomodoroService? = null
    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val serviceBinder = binder as PomodoroService.PomodoroServiceBinder
            pomodoroService = serviceBinder.getService()
            serviceBound = true

            viewModelScope.launch {
                pomodoroService?.state?.collect { newState ->
                    _state.value = newState
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            pomodoroService = null
            serviceBound = false
        }
    }

    fun bindService(context: Context) {
        val intent = Intent(context, PomodoroService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        if (serviceBound) {
            context.unbindService(serviceConnection)
            serviceBound = false
        }
    }

    fun startPomodoro(context: Context) {
        val intent = Intent(context, PomodoroService::class.java)
        context.startForegroundService(intent)

        if (!serviceBound) {
            bindService(context)
        }

        viewModelScope.launch {
            settings.collect { currentSettings ->
                pomodoroService?.startPomodoro(currentSettings)
                return@collect
            }
        }
    }

    fun togglePauseResume() {
        pomodoroService?.togglePauseResume()
    }

    fun stopPomodoro(context: Context) {
        pomodoroService?.stopPomodoro()
        if (serviceBound) {
            unbindService(context)
        }
    }

    fun updateSettings(newSettings: PomodoroSettings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(newSettings)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Only unbind if service is bound and timer is not running
        if (serviceBound && _state.value is PomodoroState.Idle) {
            try {
                getApplication<Application>().unbindService(serviceConnection)
            } catch (e: Exception) {
                // Service already unbound
            }
        }
    }
}
