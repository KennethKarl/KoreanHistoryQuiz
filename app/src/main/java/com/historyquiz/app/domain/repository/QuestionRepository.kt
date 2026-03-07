package com.historyquiz.app.domain.repository

import com.historyquiz.app.domain.model.Question

interface QuestionRepository {
    suspend fun getQuestions(level: String, count: Int): List<Question>
    suspend fun getLocalQuestions(level: String, count: Int): List<Question>
    suspend fun saveQuestions(questions: List<Question>)
    suspend fun getLocalCount(level: String): Int
}
