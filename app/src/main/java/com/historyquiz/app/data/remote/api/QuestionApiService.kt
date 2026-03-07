package com.historyquiz.app.data.remote.api

import com.historyquiz.app.data.remote.dto.QuestionListDto
import retrofit2.http.GET
import retrofit2.http.Query

interface QuestionApiService {

    @GET("questions")
    suspend fun getQuestions(
        @Query("level") level: String,
        @Query("count") count: Int
    ): QuestionListDto
}
