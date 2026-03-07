package com.historyquiz.app.presentation.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsUiState(
    val notificationEnabled: Boolean = true,
    val weeklyGoal: Int = 20
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun logout() {
        // TODO: Firebase Auth signOut 연결
    }

    fun toggleNotification(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationEnabled = enabled)
    }

    fun updateWeeklyGoal(goal: Int) {
        _uiState.value = _uiState.value.copy(weeklyGoal = goal)
    }
}
