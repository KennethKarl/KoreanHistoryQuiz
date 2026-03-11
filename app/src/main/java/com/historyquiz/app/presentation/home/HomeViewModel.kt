package com.historyquiz.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.historyquiz.app.domain.usecase.result.GetStreakUseCase
import com.historyquiz.app.domain.usecase.statistics.GetStatisticsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val questionsSolvedToday: Int = 0,
    val accuracyToday: Int = 0,
    val streakDays: Int = 0,
    val lastScore: Int? = null,
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val getStreakUseCase: GetStreakUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val streak = getStreakUseCase()
            val stats = getStatisticsUseCase()

            _uiState.update {
                it.copy(
                    streakDays = streak,
                    questionsSolvedToday = stats.daily.totalQuestions,
                    accuracyToday = stats.daily.accuracy,
                    isLoading = false
                )
            }
        }
    }
}
