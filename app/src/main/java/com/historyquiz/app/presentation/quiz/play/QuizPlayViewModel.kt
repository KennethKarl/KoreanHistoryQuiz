package com.historyquiz.app.presentation.quiz.play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.historyquiz.app.domain.model.Question
import com.historyquiz.app.domain.usecase.quiz.GetQuestionsUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface QuizUiState {
    data object Loading : QuizUiState
    data class Error(val message: String) : QuizUiState
    data class Ready(
        val question: Question,
        val currentIndex: Int,
        val totalCount: Int
    ) : QuizUiState
    data class Answered(
        val question: Question,
        val currentIndex: Int,
        val totalCount: Int,
        val selectedAnswer: Int,
        val isCorrect: Boolean
    ) : QuizUiState
    data class Finished(
        val totalScore: Int,
        val correctCount: Int,
        val totalCount: Int
    ) : QuizUiState
}

class QuizPlayViewModel(
    private val getQuestionsUseCase: GetQuestionsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var questions: List<Question> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswersCount = 0

    // 기본 난이도("basic") 혹은 심화 난이도("advanced") 로드
    fun loadQuestions(level: String = "basic") {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                questions = getQuestionsUseCase(level)
                currentQuestionIndex = 0
                correctAnswersCount = 0
                
                if (questions.isEmpty()) {
                    _uiState.value = QuizUiState.Error("문제를 불러올 수 없습니다.")
                } else {
                    emitReadyState()
                }
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error(e.message ?: "문제를 불러오는 데 실패했습니다.")
            }
        }
    }

    fun submitAnswer(selectedChoiceIndex: Int) {
        val currentState = _uiState.value
        if (currentState !is QuizUiState.Ready) return

        val question = questions[currentQuestionIndex]
        // selectedChoiceIndex는 1-based, answerIndex는 0-based
        val isCorrect = (selectedChoiceIndex == question.answerIndex + 1)

        if (isCorrect) {
            correctAnswersCount++
        }

        _uiState.value = QuizUiState.Answered(
            question = question,
            currentIndex = currentQuestionIndex,
            totalCount = questions.size,
            selectedAnswer = selectedChoiceIndex,
            isCorrect = isCorrect
        )

        // 1.5초 후 다음 문제로 넘어가거나 결과 화면으로 전환
        viewModelScope.launch {
            delay(1500)
            moveToNextQuestion()
        }
    }

    private fun moveToNextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            emitReadyState()
        } else {
            // 모든 문제 완료
            val score = if (questions.isNotEmpty()) {
                (correctAnswersCount * 100) / questions.size
            } else 0

            _uiState.value = QuizUiState.Finished(
                totalScore = score,
                correctCount = correctAnswersCount,
                totalCount = questions.size
            )
        }
    }

    private fun emitReadyState() {
        _uiState.value = QuizUiState.Ready(
            question = questions[currentQuestionIndex],
            currentIndex = currentQuestionIndex,
            totalCount = questions.size
        )
    }
}
