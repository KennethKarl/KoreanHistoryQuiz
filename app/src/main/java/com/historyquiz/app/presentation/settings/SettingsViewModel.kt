package com.historyquiz.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.historyquiz.app.data.datastore.UserPreferencesDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationEnabled: Boolean = true,
    val weeklyGoal: Int = 20,
    val appTheme: String = "dancheong"
)

class SettingsViewModel(
    private val userPreferencesDataStore: UserPreferencesDataStore
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        userPreferencesDataStore.notificationEnabled,
        userPreferencesDataStore.weeklyGoal,
        userPreferencesDataStore.appTheme
    ) { notification, goal, theme ->
        SettingsUiState(notification, goal, theme)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun logout() {
        // TODO: Firebase Auth signOut 연결
    }

    fun toggleNotification(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesDataStore.setNotificationEnabled(enabled)
        }
    }

    fun updateWeeklyGoal(goal: Int) {
        viewModelScope.launch {
            userPreferencesDataStore.setWeeklyGoal(goal)
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            userPreferencesDataStore.setAppTheme(theme)
        }
    }
}
