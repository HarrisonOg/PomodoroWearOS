package com.harrisonog.simplepomodoro.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.harrisonog.simplepomodoro.data.model.PomodoroSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "pomodoro_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val FOCUS_MINUTES_KEY = intPreferencesKey("focus_minutes")
        val BREAK_MINUTES_KEY = intPreferencesKey("break_minutes")
    }

    val settingsFlow: Flow<PomodoroSettings> = context.dataStore.data.map { preferences ->
        PomodoroSettings(
            focusMinutes = preferences[FOCUS_MINUTES_KEY] ?: 25,
            breakMinutes = preferences[BREAK_MINUTES_KEY] ?: 5
        )
    }

    suspend fun updateSettings(settings: PomodoroSettings) {
        context.dataStore.edit { preferences ->
            preferences[FOCUS_MINUTES_KEY] = settings.focusMinutes
            preferences[BREAK_MINUTES_KEY] = settings.breakMinutes
        }
    }
}
