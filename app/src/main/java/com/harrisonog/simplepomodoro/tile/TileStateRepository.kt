package com.harrisonog.simplepomodoro.tile

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.tileDataStore: DataStore<Preferences> by preferencesDataStore(name = "tile_state")

data class TileState(
    val status: String = "idle",
    val phase: String = "focus",
    val remainingMillis: Long = 0L,
    val totalMillis: Long = 0L
)

class TileStateRepository(private val context: Context) {

    private object PreferencesKeys {
        val STATUS = stringPreferencesKey("tile_status")
        val PHASE = stringPreferencesKey("tile_phase")
        val REMAINING_MILLIS = longPreferencesKey("tile_remaining_millis")
        val TOTAL_MILLIS = longPreferencesKey("tile_total_millis")
    }

    suspend fun updateState(
        status: String,
        phase: String,
        remainingMillis: Long,
        totalMillis: Long
    ) {
        context.tileDataStore.edit { preferences ->
            preferences[PreferencesKeys.STATUS] = status
            preferences[PreferencesKeys.PHASE] = phase
            preferences[PreferencesKeys.REMAINING_MILLIS] = remainingMillis
            preferences[PreferencesKeys.TOTAL_MILLIS] = totalMillis
        }
    }

    fun getCurrentStateBlocking(): TileState {
        return runBlocking {
            context.tileDataStore.data.map { preferences ->
                TileState(
                    status = preferences[PreferencesKeys.STATUS] ?: "idle",
                    phase = preferences[PreferencesKeys.PHASE] ?: "focus",
                    remainingMillis = preferences[PreferencesKeys.REMAINING_MILLIS] ?: 0L,
                    totalMillis = preferences[PreferencesKeys.TOTAL_MILLIS] ?: 0L
                )
            }.first()
        }
    }
}
