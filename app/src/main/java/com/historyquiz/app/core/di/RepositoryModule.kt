package com.historyquiz.app.core.di

import com.historyquiz.app.data.repository.QuestionRepositoryImpl
import com.historyquiz.app.data.repository.QuizResultRepositoryImpl
import com.historyquiz.app.domain.repository.QuestionRepository
import com.historyquiz.app.domain.repository.QuizResultRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<QuestionRepository> {
        QuestionRepositoryImpl(
            questionDao = get(),
            questionApiService = get(),
            networkChecker = get()
        )
    }
    single<QuizResultRepository> {
        QuizResultRepositoryImpl(
            quizResultDao = get()
        )
    }
}
