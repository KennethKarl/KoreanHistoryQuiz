package com.historyquiz.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.historyquiz.app.domain.usecase.result.GetStreakUseCase
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
    private val getStreakUseCase: GetStreakUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // TODO: TASK-007 완성 후 실제 데이터 fetch
            val streak = getStreakUseCase()

            _uiState.update {
                it.copy(
                    streakDays = streak,
                    isLoading = false
                )
            }
        }
    }
}
