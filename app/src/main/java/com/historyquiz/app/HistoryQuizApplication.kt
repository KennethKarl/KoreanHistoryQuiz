package com.historyquiz.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.historyquiz.app.core.di.databaseModule
import com.historyquiz.app.core.di.networkModule
import com.historyquiz.app.core.di.repositoryModule
import com.historyquiz.app.core.di.useCaseModule
import com.historyquiz.app.core.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class HistoryQuizApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
        initKoin()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        // TODO: 실제 Firebase 프로젝트 연결(google-services.json 교체) 후 Crashlytics 설정
    }

    private fun initKoin() {
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.NONE)
            androidContext(this@HistoryQuizApplication)
            modules(
                networkModule,
                databaseModule,
                repositoryModule,
                useCaseModule,
                viewModelModule,
            )
        }
    }
}
