package com.historyquiz.app.domain.model

data class QuizResult(
    val id: Long = 0,
    val playedAt: Long,
    val level: String,         // "basic" | "advanced"
    val totalCount: Int,
    val correctCount: Int,
    val durationSec: Int
) {
    val accuracy: Int get() = if (totalCount > 0) (correctCount * 100) / totalCount else 0
    val isCorrect: Boolean get() = correctCount == totalCount
}
