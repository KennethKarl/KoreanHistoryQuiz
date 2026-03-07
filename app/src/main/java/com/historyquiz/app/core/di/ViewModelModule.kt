package com.historyquiz.app.core.di

import com.historyquiz.app.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// TASK-003(AuthViewModel), TASK-005(HomeViewModel), TASK-006(QuizPlayViewModel),
// TASK-008(SettingsViewModel), TASK-009(StatisticsViewModel) 구현 시 채운다.
val viewModelModule = module {
    viewModel { HomeViewModel(getStreakUseCase = get()) }
}
