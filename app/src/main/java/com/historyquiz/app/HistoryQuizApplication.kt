package com.historyquiz.app

import android.app.Application
import com.google.firebase.FirebaseApp
// TODO: 실제 Firebase 프로젝트 연결(google-services.json 교체) 후 주석 해제
// import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.historyquiz.app.core.di.databaseModule
import com.historyquiz.app.core.di.networkModule
import com.historyquiz.app.core.di.repositoryModule
import com.historyquiz.app.core.di.useCaseModule
import com.historyquiz.app.core.di.viewModelModule
import com.historyquiz.app.data.local.db.SeedDataHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.java.KoinJavaComponent.get

class HistoryQuizApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
        initKoin()
        initDatabase()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(this)
        // TODO: 실제 Firebase 프로젝트 연결(google-services.json 교체) 후 아래 줄 주석 해제
        // FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
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

    private fun initDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            val seedHelper = get<SeedDataHelper>(SeedDataHelper::class.java)
            seedHelper.seedIfEmpty()
        }
    }
}
