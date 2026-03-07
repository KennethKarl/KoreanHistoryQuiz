package com.historyquiz.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "wrong_answers",
    foreignKeys = [
        ForeignKey(
            entity = QuizResultEntity::class,
            parentColumns = ["id"],
            childColumns = ["result_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WrongAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "result_id") val resultId: Long,
    @ColumnInfo(name = "question_id") val questionId: String,
    @ColumnInfo(name = "selected_index") val selectedIndex: Int
)
