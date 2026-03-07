package com.historyquiz.app.data.repository

import com.historyquiz.app.core.error.AppError
import com.historyquiz.app.core.error.AppException
import com.historyquiz.app.core.error.ErrorCode
import com.historyquiz.app.core.network.NetworkStatusChecker
import com.historyquiz.app.data.local.dao.QuestionDao
import com.historyquiz.app.data.local.entity.QuestionEntity
import com.historyquiz.app.data.remote.api.QuestionApiService
import com.historyquiz.app.domain.model.Question
import com.historyquiz.app.domain.repository.QuestionRepository

class QuestionRepositoryImpl(
    private val questionDao: QuestionDao,
    private val questionApiService: QuestionApiService,
    private val networkChecker: NetworkStatusChecker
) : QuestionRepository {

    override suspend fun getQuestions(level: String, count: Int): List<Question> {
        val localQuestions = questionDao.getByLevel(level, count * 2).map { it.toDomain() }

        if (!networkChecker.isNetworkAvailable()) {
            if (localQuestions.isEmpty()) {
                throw AppException(
                    AppError(
                        code = ErrorCode.QUZ_0002_AND,
                        errorType = "NoCachedQuestionsException",
                        message = "오프라인 상태에서는 저장된 문제가 없습니다."
                    )
                )
            }
            return localQuestions.shuffled().take(count)
        }

        val remoteQuestions = try {
            val dto = questionApiService.getQuestions(level, count)
            dto.questions.map { it.toDomain() }.also { remote ->
                questionDao.insertAll(remote.map { QuestionEntity.fromDomain(it) })
            }
        } catch (e: Exception) {
            emptyList()
        }

        val combined = (remoteQuestions + localQuestions).distinctBy { it.id }.shuffled()
        return combined.take(count).ifEmpty {
            throw AppException(
                AppError(
                    code = ErrorCode.QUZ_0001_AND,
                    errorType = "QuestionLoadFailedException",
                    message = "문제를 불러올 수 없습니다."
                )
            )
        }
    }

    override suspend fun getLocalQuestions(level: String, count: Int): List<Question> {
        return questionDao.getByLevel(level, count).map { it.toDomain() }
    }

    override suspend fun getLocalQuestionsByEra(level: String, era: String, count: Int): List<Question> {
        return questionDao.getByLevelAndEra(level, era, count).map { it.toDomain() }
    }

    override suspend fun saveQuestions(questions: List<Question>) {
        questionDao.insertAll(questions.map { QuestionEntity.fromDomain(it) })
    }

    override suspend fun getLocalCount(level: String): Int {
        return questionDao.countByLevel(level)
    }
}
