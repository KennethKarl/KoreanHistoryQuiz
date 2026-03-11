package com.historyquiz.app.core.di

import com.historyquiz.app.presentation.home.HomeViewModel
import com.historyquiz.app.presentation.quiz.play.QuizPlayViewModel
import com.historyquiz.app.presentation.settings.SettingsViewModel
import com.historyquiz.app.presentation.statistics.StatisticsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(getStreakUseCase = get(), getStatisticsUseCase = get()) }
    viewModel { QuizPlayViewModel(getQuestionsUseCase = get(), submitQuizUseCase = get()) }
    viewModel { SettingsViewModel(userPreferencesDataStore = get()) }
    viewModel { StatisticsViewModel(getStatisticsUseCase = get()) }
}
