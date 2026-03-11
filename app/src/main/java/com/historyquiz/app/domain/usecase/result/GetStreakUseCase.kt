package com.historyquiz.app.domain.usecase.result

import com.historyquiz.app.domain.repository.QuizResultRepository
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * 현재 연속 학습 스트릭 일수를 반환한다.
 */
class GetStreakUseCase(
    private val repository: QuizResultRepository
) {

    suspend operator fun invoke(): Int {
        val now = System.currentTimeMillis()
        val allResults = repository.getResultsAfter(0) // 전체 기록 가져옴 (최적화 여지 있음)
        if (allResults.isEmpty()) return 0

        // 날짜별로 그룹화 (시간 제거)
        val playedDates = allResults.map { 
            val cal = Calendar.getInstance().apply { timeInMillis = it.playedAt }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }.distinct().sortedDescending()

        if (playedDates.isEmpty()) return 0

        val todayCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val today = todayCal.timeInMillis
        
        val yesterdayCal = (todayCal.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterday = yesterdayCal.timeInMillis

        // 마지막 학습일이 오늘이나 어제가 아니면 스트릭 끊김
        val lastStudyDate = playedDates.first()
        if (lastStudyDate < yesterday) return 0

        var streak = 0
        var currentCheckDate = lastStudyDate

        for (date in playedDates) {
            if (date == currentCheckDate) {
                streak++
                // 다음 체크할 날짜는 하루 전
                val cal = Calendar.getInstance().apply { 
                    timeInMillis = currentCheckDate
                    add(Calendar.DAY_OF_YEAR, -1)
                }
                currentCheckDate = cal.timeInMillis
            } else {
                break
            }
        }

        return streak
    }
}
