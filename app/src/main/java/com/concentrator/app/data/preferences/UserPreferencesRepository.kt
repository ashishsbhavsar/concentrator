package com.concentrator.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserPreferences(
    val reelsLimit: Int = 50,
    val shortsLimit: Int = 50,
    val totalLimit: Int = 100,
    val isOverlayEnabled: Boolean = true,
    val isNotificationsEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val autoHideOverlayOutsideSocial: Boolean = true,
    val overlayPositionX: Int = 50,
    val overlayPositionY: Int = 300
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val REELS_LIMIT = intPreferencesKey("reels_limit")
        val SHORTS_LIMIT = intPreferencesKey("shorts_limit")
        val TOTAL_LIMIT = intPreferencesKey("total_limit")
        val OVERLAY_ENABLED = booleanPreferencesKey("overlay_enabled")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val AUTO_HIDE_OVERLAY = booleanPreferencesKey("auto_hide_overlay")
        val OVERLAY_POS_X = intPreferencesKey("overlay_pos_x")
        val OVERLAY_POS_Y = intPreferencesKey("overlay_pos_y")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        UserPreferences(
            reelsLimit = preferences[PreferencesKeys.REELS_LIMIT] ?: 50,
            shortsLimit = preferences[PreferencesKeys.SHORTS_LIMIT] ?: 50,
            totalLimit = preferences[PreferencesKeys.TOTAL_LIMIT] ?: 100,
            isOverlayEnabled = preferences[PreferencesKeys.OVERLAY_ENABLED] ?: true,
            isNotificationsEnabled = preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            isVibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
            autoHideOverlayOutsideSocial = preferences[PreferencesKeys.AUTO_HIDE_OVERLAY] ?: true,
            overlayPositionX = preferences[PreferencesKeys.OVERLAY_POS_X] ?: 50,
            overlayPositionY = preferences[PreferencesKeys.OVERLAY_POS_Y] ?: 300
        )
    }

    suspend fun updateReelsLimit(limit: Int) {
        context.dataStore.edit { it[PreferencesKeys.REELS_LIMIT] = limit }
    }

    suspend fun updateShortsLimit(limit: Int) {
        context.dataStore.edit { it[PreferencesKeys.SHORTS_LIMIT] = limit }
    }

    suspend fun updateOverlayEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.OVERLAY_ENABLED] = enabled }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.VIBRATION_ENABLED] = enabled }
    }

    suspend fun updateOverlayPosition(x: Int, y: Int) {
        context.dataStore.edit {
            it[PreferencesKeys.OVERLAY_POS_X] = x
            it[PreferencesKeys.OVERLAY_POS_Y] = y
        }
    }
}
