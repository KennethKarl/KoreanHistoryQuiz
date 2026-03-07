package com.historyquiz.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.historyquiz.app.domain.model.Question

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val content: String,
    val options: String,                           // JSON 배열 문자열
    @ColumnInfo(name = "answer_index") val answerIndex: Int,
    val level: String,
    val category: String,
    val era: String = "미정",
    @ColumnInfo(name = "cached_at") val cachedAt: Long = System.currentTimeMillis(),
    val source: String = "local"
) {
    fun toDomain(): Question {
        val type = object : TypeToken<List<String>>() {}.type
        val optionList: List<String> = Gson().fromJson(options, type)
        return Question(
            id = id,
            content = content,
            options = optionList,
            answerIndex = answerIndex,
            level = level,
            category = category,
            era = era,
            cachedAt = cachedAt,
            source = source
        )
    }

    companion object {
        fun fromDomain(question: Question): QuestionEntity {
            return QuestionEntity(
                id = question.id,
                content = question.content,
                options = Gson().toJson(question.options),
                answerIndex = question.answerIndex,
                level = question.level,
                category = question.category,
                era = question.era,
                cachedAt = if (question.cachedAt > 0) question.cachedAt else System.currentTimeMillis(),
                source = question.source
            )
        }
    }
}
