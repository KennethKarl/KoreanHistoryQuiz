package com.historyquiz.app.core.di

import com.historyquiz.app.domain.usecase.quiz.GetQuestionsUseCase
import com.historyquiz.app.domain.usecase.quiz.GetStreakUseCase
import com.historyquiz.app.domain.usecase.quiz.MixQuestionsUseCase
import org.koin.dsl.module

// TASK-003(Auth), TASK-005(Home), TASK-006(Quiz) 구현 시 채운다.
val useCaseModule = module {
    factory { GetQuestionsUseCase(questionRepository = get()) }
    factory { MixQuestionsUseCase() }
    factory { GetStreakUseCase() }
}
