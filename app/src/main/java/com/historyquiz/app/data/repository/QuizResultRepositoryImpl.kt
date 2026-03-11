package com.historyquiz.app.data.repository

import com.historyquiz.app.data.local.dao.EraCount
import com.historyquiz.app.data.local.dao.QuizResultDao
import com.historyquiz.app.data.local.entity.QuizResultEntity
import com.historyquiz.app.domain.model.QuizResult
import com.historyquiz.app.domain.repository.QuizResultRepository

class QuizResultRepositoryImpl(
    private val quizResultDao: QuizResultDao
) : QuizResultRepository {

    override suspend fun saveResult(quizResult: QuizResult): Long {
        val entity = QuizResultEntity.fromDomain(quizResult)
        return quizResultDao.insertResult(entity)
    }

    override suspend fun getRecentResults(limit: Int): List<QuizResult> {
        return quizResultDao.getRecentResults(limit).map { it.toDomain() }
    }

    override suspend fun getResultsAfter(startTime: Long): List<QuizResult> {
        return quizResultDao.getResultsAfter(startTime).map { it.toDomain() }
    }

    override suspend fun getWrongErasAfter(startTime: Long): List<EraCount> {
        return quizResultDao.getWrongErasAfter(startTime)
    }
}
