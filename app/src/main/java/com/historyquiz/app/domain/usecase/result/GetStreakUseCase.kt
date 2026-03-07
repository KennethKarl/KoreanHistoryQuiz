package com.historyquiz.app.domain.usecase.result

/**
 * 현재 연속 학습 스트릭 일수를 반환한다.
 * TASK-007 완료 후 QuizResultRepository와 연동되어 실제 구현된다.
 * 현재는 stub: 항상 0 반환
 */
class GetStreakUseCase {

    suspend operator fun invoke(): Int {
        // TODO(TASK-007): QuizResultRepository와 연동하여 실제 streak 계산
        // - 최근 quiz_results에서 playedAt 기준으로 연속된 날들을 계산
        // - 오늘부터 역순으로 몇 일 연속인지 확인
        return 0
    }
}
