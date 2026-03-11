package com.historyquiz.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.historyquiz.app.data.local.entity.QuizResultEntity
import com.historyquiz.app.data.local.entity.WrongAnswerEntity

@Dao
interface QuizResultDao {

    @Insert
    suspend fun insertResult(result: QuizResultEntity): Long

    @Insert
    suspend fun insertWrongAnswers(answers: List<WrongAnswerEntity>)

    @Query("SELECT * FROM quiz_results ORDER BY played_at DESC LIMIT :limit")
    suspend fun getRecentResults(limit: Int): List<QuizResultEntity>

    @Query("SELECT * FROM quiz_results WHERE played_at >= :startTime ORDER BY played_at DESC")
    suspend fun getResultsAfter(startTime: Long): List<QuizResultEntity>

    @Query("""
        SELECT q.era, COUNT(*) as count 
        FROM wrong_answers w
        JOIN questions q ON w.question_id = q.id
        JOIN quiz_results r ON w.result_id = r.id
        WHERE r.played_at >= :startTime
        GROUP BY q.era
        ORDER BY count DESC
    """)
    suspend fun getWrongErasAfter(startTime: Long): List<EraCount>
}

data class EraCount(
    val era: String,
    val count: Int
)
