package com.historyquiz.app.domain.model

data class Question(
    val id: String,
    val content: String,
    val options: List<String>,
    val answerIndex: Int,
    val level: String,        // "basic" | "advanced"
    val category: String,
    val cachedAt: Long = 0L,
    val source: String = "local"  // "local" | "remote"
)
