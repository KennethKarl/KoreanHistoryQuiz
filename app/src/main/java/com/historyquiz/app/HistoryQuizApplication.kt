package com.historyquiz.app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
        // DEBUG 빌드에서 Crashlytics 비활성화 (google-services.json PLACEHOLDER 상태)
        // 실제 Firebase 프로젝트 연결 후 이 줄 제거
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
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
