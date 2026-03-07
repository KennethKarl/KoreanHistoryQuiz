package com.historyquiz.app.core.di

import com.historyquiz.app.BuildConfig
import com.historyquiz.app.core.network.AuthInterceptor
import com.historyquiz.app.core.network.NetworkStatusChecker
import com.historyquiz.app.data.remote.api.QuestionApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    // 네트워크 상태 체커
    single { NetworkStatusChecker(androidContext()) }

    // Firebase Auth 토큰 인터셉터
    single { AuthInterceptor() }

    // OkHttpClient
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(logging)
            .build()
    }

    // Retrofit
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // API Services
    single<QuestionApiService> { get<Retrofit>().create(QuestionApiService::class.java) }
}
