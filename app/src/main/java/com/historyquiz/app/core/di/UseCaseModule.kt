package com.historyquiz.app.core.di

import com.historyquiz.app.domain.usecase.quiz.GetQuestionsUseCase
import com.historyquiz.app.domain.usecase.quiz.MixQuestionsUseCase
import com.historyquiz.app.domain.usecase.quiz.SubmitQuizUseCase
import com.historyquiz.app.domain.usecase.result.GetStreakUseCase
import com.historyquiz.app.domain.usecase.statistics.GetStatisticsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetQuestionsUseCase(questionRepository = get()) }
    factory { MixQuestionsUseCase() }
    factory { GetStreakUseCase(repository = get()) }
    factory { SubmitQuizUseCase(quizResultRepository = get()) }
    factory { GetStatisticsUseCase(repository = get()) }
}
