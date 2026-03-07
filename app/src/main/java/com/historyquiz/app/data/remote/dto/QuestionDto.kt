package com.historyquiz.app.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.historyquiz.app.domain.model.Question

data class QuestionDto(
    @SerializedName("id") val id: String,
    @SerializedName("content") val content: String,
    @SerializedName("options") val options: List<String>,
    @SerializedName("answer_index") val answerIndex: Int,
    @SerializedName("level") val level: String,
    @SerializedName("category") val category: String
) {
    fun toDomain(): Question = Question(
        id = id,
        content = content,
        options = options,
        answerIndex = answerIndex,
        level = level,
        category = category,
        source = "remote"
    )
}
