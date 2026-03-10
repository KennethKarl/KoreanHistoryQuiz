package com.historyquiz.app.domain.usecase.statistics

import com.historyquiz.app.domain.model.QuizResult
import com.historyquiz.app.domain.repository.QuizResultRepository
import java.util.Calendar

data class PeriodStats(
    val periodName: String,
    val totalQuestions: Int,
    val correctQuestions: Int,
    val accuracy: Int,
    val quizCount: Int
)

data class Statistics(
    val daily: PeriodStats,
    val weekly: PeriodStats,
    val monthly: PeriodStats,
    val levelDistribution: Map<String, Int> // "basic" to count, "advanced" to count
)

class GetStatisticsUseCase(
    private val repository: QuizResultRepository
) {
    suspend operator fun invoke(): Statistics {
        val now = System.currentTimeMillis()
        
        // 오늘 00:00:00
        val dayStart = getStartOfToday()
        // 이번 주 월요일 00:00:00
        val weekStart = getStartOfWeek()
        // 이번 달 1일 00:00:00
        val monthStart = getStartOfMonth()

        val allResults = repository.getResultsAfter(monthStart)
        
        val dailyResults = allResults.filter { it.playedAt >= dayStart }
        val weeklyResults = allResults.filter { it.playedAt >= weekStart }
        val monthlyResults = allResults // 이미 monthStart 이후임

        return Statistics(
            daily = calculateStats("오늘", dailyResults),
            weekly = calculateStats("이번 주", weeklyResults),
            monthly = calculateStats("이번 달", monthlyResults),
            levelDistribution = allResults.groupingBy { it.level }.eachCount()
        )
    }

    private fun calculateStats(name: String, results: List<QuizResult>): PeriodStats {
        val total = results.sumOf { it.totalCount }
        val correct = results.sumOf { it.correctCount }
        val accuracy = if (total > 0) (correct * 100) / total else 0
        return PeriodStats(name, total, correct, accuracy, results.size)
    }

    private fun getStartOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun getStartOfWeek(): Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    private fun getStartOfMonth(): Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
