package com.historyquiz.app.core.error

/**
 * 오류 코드 상수 — guides/error-code-guide.md v1.4 기준
 * tasks/architecture.md §10 오류 코드 정의와 1:1 대응
 *
 * 형식: {카테고리}_{4자리 순번}_{플랫폼 suffix}
 * 예)  aut_0005_and  →  AUT_0005_AND
 *
 * [규칙]
 * - raw string 직접 사용 금지. 반드시 이 object의 상수를 사용한다.
 * - 새 오류 코드 추가 시 error-code-guide.md와 tasks/architecture.md도 함께 업데이트한다.
 */
object ErrorCode {

    // ─────────────────────────────────────────────────────────────────────────
    // aut — 인증 (Authentication)
    // ─────────────────────────────────────────────────────────────────────────
    const val AUT_0004_AND = "aut_0004_and"  // 토큰 갱신 실패
    const val AUT_0005_AND = "aut_0005_and"  // 소셜 로그인 실패 (Google Sign-In)
    const val AUT_0006_AND = "aut_0006_and"  // 로그아웃 실패
    const val AUT_0501_AND = "aut_0501_and"  // 인증 필요 (미로그인 상태)

    // ─────────────────────────────────────────────────────────────────────────
    // net — 네트워크 (Network)
    // ─────────────────────────────────────────────────────────────────────────
    const val NET_0001_AND = "net_0001_and"  // 네트워크 연결 없음
    const val NET_0002_AND = "net_0002_and"  // 요청 타임아웃
    const val NET_0200_AND = "net_0200_and"  // HTTP 4xx 오류
    const val NET_0201_AND = "net_0201_and"  // HTTP 5xx 오류
    const val NET_0202_AND = "net_0202_and"  // 응답 파싱 실패

    // ─────────────────────────────────────────────────────────────────────────
    // db — 데이터베이스 (Database)
    // ─────────────────────────────────────────────────────────────────────────
    const val DB_0003_AND = "db_0003_and"    // 데이터 저장 실패
    const val DB_0004_AND = "db_0004_and"    // 데이터 조회 실패
    const val DB_0100_AND = "db_0100_and"    // DB 초기화 실패

    // ─────────────────────────────────────────────────────────────────────────
    // quz — 퀴즈 (Quiz) — historyQuiz 전용, error-code-guide.md §5-14
    // ─────────────────────────────────────────────────────────────────────────
    const val QUZ_0001_AND = "quz_0001_and"  // 문제 로드 실패 (API + 캐시 모두 실패)
    const val QUZ_0002_AND = "quz_0002_and"  // 캐시된 문제 없음 (오프라인 상태)
    const val QUZ_0003_AND = "quz_0003_and"  // 외부 문제 API 호출 실패
    const val QUZ_0600_AND = "quz_0600_and"  // 유효하지 않은 난이도 설정

    // ─────────────────────────────────────────────────────────────────────────
    // ui — UI/화면
    // ─────────────────────────────────────────────────────────────────────────
    const val UI_0001_AND = "ui_0001_and"    // 화면 렌더링 오류
}
