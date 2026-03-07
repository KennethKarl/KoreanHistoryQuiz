package com.historyquiz.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.historyquiz.app.data.local.entity.QuestionEntity

@Dao
interface QuestionDao {

    @Query("SELECT * FROM questions WHERE level = :level ORDER BY RANDOM() LIMIT :count")
    suspend fun getByLevel(level: String, count: Int): List<QuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("SELECT COUNT(*) FROM questions WHERE level = :level")
    suspend fun countByLevel(level: String): Int

    @Query("DELETE FROM questions WHERE source = 'remote' AND cached_at < :cutoff")
    suspend fun deleteOldRemoteCache(cutoff: Long)
}
