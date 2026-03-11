package com.historyquiz.app.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.historyquiz.app.domain.usecase.statistics.GetStatisticsUseCase
import com.historyquiz.app.domain.usecase.statistics.Statistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class StatisticsUiState {
    object Loading : StatisticsUiState()
    data class Success(val statistics: Statistics) : StatisticsUiState()
    data class Error(val message: String) : StatisticsUiState()
}

class StatisticsViewModel(
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { StatisticsUiState.Loading }
            try {
                val stats = getStatisticsUseCase()
                _uiState.update { StatisticsUiState.Success(stats) }
            } catch (e: Exception) {
                _uiState.update { StatisticsUiState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.") }
            }
        }
    }
}
