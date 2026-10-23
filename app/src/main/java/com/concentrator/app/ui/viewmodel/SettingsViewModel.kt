package com.concentrator.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.concentrator.app.data.local.AppDatabase
import com.concentrator.app.data.preferences.UserPreferences
import com.concentrator.app.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepo = UserPreferencesRepository(application)
    private val db = AppDatabase.getInstance(application)
    private val dao = db.scrollDao()

    val preferences: StateFlow<UserPreferences> = preferencesRepo.userPreferencesFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserPreferences()
    )

    fun updateReelsLimit(limit: Int) {
        viewModelScope.launch { preferencesRepo.updateReelsLimit(limit) }
    }

    fun updateShortsLimit(limit: Int) {
        viewModelScope.launch { preferencesRepo.updateShortsLimit(limit) }
    }

    fun updateOverlayEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepo.updateOverlayEnabled(enabled) }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepo.updateNotificationsEnabled(enabled) }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepo.updateVibrationEnabled(enabled) }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}
