package com.historyquiz.app.domain.repository

import com.historyquiz.app.data.local.dao.EraCount
import com.historyquiz.app.domain.model.QuizResult

interface QuizResultRepository {
    suspend fun saveResult(quizResult: QuizResult): Long
    suspend fun getRecentResults(limit: Int): List<QuizResult>
    suspend fun getResultsAfter(startTime: Long): List<QuizResult>
    suspend fun getWrongErasAfter(startTime: Long): List<EraCount>
}
