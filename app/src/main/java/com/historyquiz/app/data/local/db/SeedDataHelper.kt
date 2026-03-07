package com.historyquiz.app.data.local.db

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.historyquiz.app.data.local.dao.QuestionDao
import com.historyquiz.app.data.local.entity.QuestionEntity
import java.io.InputStreamReader

class SeedDataHelper(
    private val questionDao: QuestionDao,
    private val context: Context
) {

    private val gson = Gson()

    suspend fun seedIfEmpty() {
        val basicCount = questionDao.countByLevel("basic")
        val advancedCount = questionDao.countByLevel("advanced")

        if (basicCount == 0 || advancedCount == 0) {
            loadQuestionsFromAssets()
        }
    }

    private suspend fun loadQuestionsFromAssets() {
        try {
            val inputStream = context.assets.open("korean_history_answers.json")
            val reader = InputStreamReader(inputStream, "UTF-8")
            val historyQuizData = gson.fromJson(reader, HistoryQuizJsonModel::class.java)
            reader.close()

            val entities = mutableListOf<QuestionEntity>()

            historyQuizData.exams.forEach { exam ->
                exam.answers.forEach { answer ->
                    // Generate a unique ID
                    val uniqueId = "seed_${exam.level}_${exam.session}_${answer.questionNo}"
                    
                    entities.add(
                        QuestionEntity(
                            id = uniqueId,
                            content = answer.content ?: "문제가 없습니다.",
                            options = gson.toJson(answer.options ?: emptyList<String>()),
                            answerIndex = answer.answer,
                            level = exam.level,
                            category = exam.session.toString() + "회", // category mapped to session temporarily
                            era = answer.era ?: "미정",
                            source = "local"
                        )
                    )
                }
            }
            
            if (entities.isNotEmpty()) {
                questionDao.insertAll(entities)
                Log.d("SeedDataHelper", "Seeded ${entities.size} questions from assets.")
            }
            
        } catch (e: Exception) {
            Log.e("SeedDataHelper", "Failed to load seed data from assets: ${e.message}")
        }
    }
}

// Data models for parsing the mock JSON
data class HistoryQuizJsonModel(
    val exams: List<ExamJsonModel>
)

data class ExamJsonModel(
    val session: Int,
    val level: String,
    val numChoices: Int,
    val answers: List<AnswerJsonModel>
)

data class AnswerJsonModel(
    val questionNo: Int,
    val answer: Int,
    val content: String?,
    val options: List<String>?,
    val era: String?
)
