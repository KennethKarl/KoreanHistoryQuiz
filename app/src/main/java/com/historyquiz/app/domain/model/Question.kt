package com.historyquiz.app.domain.model

data class Question(
    val id: String,
    val content: String,
    val options: List<String>,
    val answerIndex: Int,
    val level: String,        // "basic" | "advanced"
    val category: String,
    val era: String = "미정",   // 시대별 분류 필드 추가
    val cachedAt: Long = 0L,
    val source: String = "local"  // "local" | "remote"
)
