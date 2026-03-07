package com.historyquiz.app.presentation.statistics

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class StatisticsUiState(
    val totalQuizzes: Int = 0,
    val totalCorrect: Int = 0,
    val totalAccuracy: Int = 0,
    val basicAttempts: Int = 0,
    val advancedAttempts: Int = 0
)

class StatisticsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    // TODO(TASK-009): GetStatisticsUseCase, GetQuizHistoryUseCase 연결
}
