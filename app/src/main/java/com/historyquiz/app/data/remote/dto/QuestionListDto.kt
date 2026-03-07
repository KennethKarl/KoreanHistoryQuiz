package com.historyquiz.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuestionListDto(
    @SerializedName("questions") val questions: List<QuestionDto>,
    @SerializedName("total") val total: Int = 0
)
