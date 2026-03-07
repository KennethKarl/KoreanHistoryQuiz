package com.historyquiz.app.domain.usecase.quiz

import com.historyquiz.app.domain.model.QuizResult
import com.historyquiz.app.domain.repository.QuizResultRepository

class SubmitQuizUseCase(
    private val quizResultRepository: QuizResultRepository
) {
    suspend operator fun invoke(quizResult: QuizResult): Long {
        return quizResultRepository.saveResult(quizResult)
    }
}
