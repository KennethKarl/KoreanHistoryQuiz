package com.historyquiz.app.domain.usecase.quiz

import com.historyquiz.app.domain.model.Question
import com.historyquiz.app.domain.repository.QuestionRepository

class GetQuestionsUseCase(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(level: String, count: Int = 10): List<Question> {
        return questionRepository.getQuestions(level, count)
    }
}
