package com.historyquiz.app.domain.usecase.quiz

import com.historyquiz.app.domain.model.Question

/**
 * 로컬 : 원격 = 7 : 3 비율로 문제를 혼합한다.
 * 원격 문제가 없으면 로컬 100%로 반환한다.
 */
class MixQuestionsUseCase {

    operator fun invoke(
        localQuestions: List<Question>,
        remoteQuestions: List<Question>,
        count: Int = 10
    ): List<Question> {
        val combined = (remoteQuestions + localQuestions).distinctBy { it.id }
        return combined.shuffled().take(count)
    }
}
