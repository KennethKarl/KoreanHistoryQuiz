package com.historyquiz.app.domain.usecase.result

import com.historyquiz.app.domain.model.QuizResult

/**
 * 최근 quiz 결과를 조회한다.
 * TASK-007 완료 후 QuizResultRepository와 연동되어 실제 구현된다.
 * 현재는 stub: 빈 리스트 반환
 */
class GetQuizHistoryUseCase {

    suspend operator fun invoke(limit: Int = 10): List<QuizResult> {
        // TODO(TASK-007): QuizResultRepository와 연동하여 실제 조회
        // return quizResultRepository.getRecentResults(limit)
        return emptyList()
    }
}
