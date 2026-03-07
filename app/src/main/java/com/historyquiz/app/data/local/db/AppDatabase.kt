package com.historyquiz.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.historyquiz.app.data.local.dao.QuestionDao
import com.historyquiz.app.data.local.dao.QuizResultDao
import com.historyquiz.app.data.local.entity.QuestionEntity
import com.historyquiz.app.data.local.entity.QuizResultEntity
import com.historyquiz.app.data.local.entity.WrongAnswerEntity

@Database(
    entities = [QuestionEntity::class, QuizResultEntity::class, WrongAnswerEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        const val DATABASE_NAME = "history_quiz_db"
    }
}
