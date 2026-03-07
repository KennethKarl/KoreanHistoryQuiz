package com.historyquiz.app.core.di

import androidx.room.Room
import com.historyquiz.app.data.datastore.UserPreferencesDataStore
import com.historyquiz.app.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    // Room Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs (TASK-004/007에서 엔티티 추가 후 활성화)
    // single { get<AppDatabase>().questionDao() }
    // single { get<AppDatabase>().quizResultDao() }

    // DataStore
    single { UserPreferencesDataStore(androidContext()) }
}
