package com.historyquiz.app.core.di

import com.historyquiz.app.data.repository.QuestionRepositoryImpl
import com.historyquiz.app.domain.repository.QuestionRepository
import org.koin.dsl.module

// TASK-003(Auth), TASK-004(Question), TASK-007(QuizResult) 구현 시 채운다.
val repositoryModule = module {
    single<QuestionRepository> {
        QuestionRepositoryImpl(
            questionDao = get(),
            questionApiService = get(),
            networkChecker = get()
        )
    }
}
