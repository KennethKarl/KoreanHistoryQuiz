package com.historyquiz.app.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/** Unix timestamp(ms) → LocalDate 변환 */
fun Long.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate =
    Instant.ofEpochMilli(this).atZone(zoneId).toLocalDate()

/** LocalDate → Unix timestamp(ms, 해당 일의 시작 시각) */
fun LocalDate.toEpochMilli(zoneId: ZoneId = ZoneId.systemDefault()): Long =
    atStartOfDay(zoneId).toInstant().toEpochMilli()

/** 현재 시각을 Unix timestamp(ms)로 반환 */
fun currentTimeMillis(): Long = System.currentTimeMillis()

/** LocalDate → "yyyy-MM-dd" 포맷 문자열 */
fun LocalDate.toDisplayString(): String =
    format(DateTimeFormatter.ISO_LOCAL_DATE)

/** 두 날짜의 연속 날 수 계산 (streak) */
fun calculateStreak(dates: List<LocalDate>): Int {
    if (dates.isEmpty()) return 0
    val sortedDesc = dates.distinct().sortedDescending()
    val today = LocalDate.now()
    // 오늘 또는 어제 학습이 없으면 streak 0
    if (sortedDesc.first() < today.minusDays(1)) return 0
    var streak = 0
    var expected = sortedDesc.first()
    for (date in sortedDesc) {
        if (date == expected) {
            streak++
            expected = expected.minusDays(1)
        } else {
            break
        }
    }
    return streak
}
