package com.historyquiz.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.historyquiz.app.data.local.dao.QuestionDao
import com.historyquiz.app.data.local.entity.QuestionEntity

// TASK-007에서 QuizResultEntity, WrongAnswerEntity 추가 예정
@Database(
    entities = [QuestionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    // abstract fun quizResultDao(): QuizResultDao   // TASK-007에서 활성화

    companion object {
        const val DATABASE_NAME = "history_quiz_db"
    }
}
