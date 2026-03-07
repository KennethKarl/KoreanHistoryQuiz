package com.historyquiz.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

// entities는 TASK-004(QuestionEntity), TASK-007(QuizResultEntity, WrongAnswerEntity) 추가 예정
@Database(
    entities = [],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // abstract fun questionDao(): QuestionDao       // TASK-004에서 활성화
    // abstract fun quizResultDao(): QuizResultDao   // TASK-007에서 활성화

    companion object {
        const val DATABASE_NAME = "history_quiz_db"
    }
}
