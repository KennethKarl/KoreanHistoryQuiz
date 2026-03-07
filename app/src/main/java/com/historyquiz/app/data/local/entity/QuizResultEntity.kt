package com.historyquiz.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.historyquiz.app.domain.model.QuizResult

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "played_at") val playedAt: Long,
    val level: String,
    @ColumnInfo(name = "total_count") val totalCount: Int,
    @ColumnInfo(name = "correct_count") val correctCount: Int,
    @ColumnInfo(name = "duration_sec") val durationSec: Int
) {
    fun toDomain(): QuizResult = QuizResult(
        id = id,
        playedAt = playedAt,
        level = level,
        totalCount = totalCount,
        correctCount = correctCount,
        durationSec = durationSec
    )

    companion object {
        fun fromDomain(quizResult: QuizResult): QuizResultEntity = QuizResultEntity(
            id = quizResult.id,
            playedAt = quizResult.playedAt,
            level = quizResult.level,
            totalCount = quizResult.totalCount,
            correctCount = quizResult.correctCount,
            durationSec = quizResult.durationSec
        )
    }
}
