package com.historyquiz.app.core.di

import androidx.room.Room
import com.historyquiz.app.data.datastore.UserPreferencesDataStore
import com.historyquiz.app.data.local.db.AppDatabase
import com.historyquiz.app.data.local.db.SeedDataHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    // Room Database
    // TASK-014: createFromAsset("quiz.db") — 앱 설치 시 assets/quiz.db를 DB 디렉토리로 자동 복사
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .createFromAsset("quiz.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs
    single { get<AppDatabase>().questionDao() }
    single { get<AppDatabase>().quizResultDao() }

    // Seed Helper
    single { SeedDataHelper(get(), androidContext()) }

    // DataStore
    single { UserPreferencesDataStore(androidContext()) }
}
