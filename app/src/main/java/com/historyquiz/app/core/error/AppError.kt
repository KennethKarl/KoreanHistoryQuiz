package com.historyquiz.app.core.error

/**
 * 앱 공통 오류 모델
 *
 * @param code      오류 코드 (ErrorCode.kt 상수 참조) — 로그 집계·Firebase Custom Key용
 * @param errorType 예외 클래스명 (AWS식) — 개발자 가독성·IDE 자동완성용
 * @param message   사용자/로그용 오류 메시지
 * @param cause     원인 예외 (옵션)
 *
 * 사용 예:
 *   AppError(ErrorCode.AUT_0005_AND, "SocialLoginException", "Google 로그인 실패", e)
 */
data class AppError(
    val code: String,
    val errorType: String,
    val message: String,
    val cause: Throwable? = null,
)
