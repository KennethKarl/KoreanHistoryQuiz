# 오류 코드 정의 가이드 (Error Code Guide) v1.3

> **대상 프로젝트**: Android / iOS 모바일 앱 + 서버 + 웹
> **목적**: 플랫폼·상황별 오류 코드를 통일된 체계로 정의하여 디버깅·로깅·이슈 추적의 일관성을 확보한다.
> **위치**: `docs/error-code-guide.md` (각 플랫폼 저장소 공통 참조)

---

## 1. 오류 코드 형식

```
{카테고리 코드}_{순번}_{플랫폼 suffix}
```

| 요소 | 자릿수 | 예시 |
|------|--------|------|
| 플랫폼 suffix | 3자 | `and`, `ios`, `ser`, `web` |
| 카테고리 코드 | 3자 | `cam`, `aut`, `net` … |
| 순번 | **4자리 숫자 (0001~9999)** | `0001`, `0042` |

**전체 예시**

```
cam_0001_and   Android 카메라 초기화 실패
aut_0003_ios   iOS 토큰 갱신 실패
db_0007_ser    서버 DB 연결 타임아웃
ui_0012_web    웹 페이지 렌더링 오류
```

---

## 2. 플랫폼 Suffix

| Suffix | 발생 위치 | 설명 |
|--------|-----------|------|
| `and` | Android 앱 | Android 네이티브 코드에서 발생한 오류 |
| `ios` | iOS 앱 | iOS 네이티브 코드에서 발생한 오류 |
| `ser` | 서버 (Backend) | API 서버·백엔드 로직에서 발생한 오류 |
| `web` | 웹 프론트엔드 | 웹 브라우저·WebView에서 발생한 오류 |

> **Android/iOS 공통 로직에서 발생하는 오류**는 각 플랫폼 suffix(`and`, `ios`)를 개별 사용한다. 카테고리+순번은 공유하고, suffix만 달리한다. 예) `cam_0001_and` / `cam_0001_ios`

---

## 3. 카테고리 코드

### 3-1. 공통 카테고리 (플랫폼 무관)

| 카테고리 코드 | 분류명 | 설명 |
|---------------|--------|------|
| `aut` | 인증 (Authentication) | 로그인, 토큰, 세션, OAuth |
| `net` | 네트워크 (Network) | HTTP 통신, 타임아웃, 연결 오류 |
| `perm` | 권한 (Permission) | OS 권한 요청/거부 |
| `db` | 데이터베이스/저장소 (Database/Storage) | DB 쿼리, 파일 I/O, 캐시 |
| `val` | 유효성 검사 (Validation) | 입력값 형식·범위 오류 |
| `pay` | 결제 (Payment) | 결제 요청, 취소, 영수증 검증 |
| `push` | 푸시 알림 (Push Notification) | FCM/APNs 토큰, 알림 발송 |
| `file` | 파일 처리 (File) | 업로드, 다운로드, 파일 파싱 |
| `loc` | 위치 (Location) | GPS, 지오코딩, 위치 권한 |
| `sys` | 시스템 (System) | OS 레벨, 메모리, 스레드 |
| `cfg` | 설정/환경 (Config) | 앱 설정, 환경변수, Feature Flag |
| `unk` | 알 수 없음 (Unknown) | 분류 불가 예외 |

### 3-2. 모바일(Android/iOS) 전용 카테고리

| 카테고리 코드 | 분류명 | 설명 |
|---------------|--------|------|
| `cam` | 카메라 (Camera) | 카메라 초기화, 촬영, AI 카메라 모듈 |
| `mic` | 마이크 (Microphone) | 오디오 녹음, 스트리밍 |
| `bio` | 생체인증 (Biometric) | 지문, Face ID, 생체정보 |
| `nfc` | NFC | NFC 태그 읽기/쓰기 |
| `ble` | 블루투스 (Bluetooth LE) | BLE 스캔, 연결, 데이터 송수신 |
| `ui` | UI/화면 (UI) | 렌더링 오류, 화면 전환, 뷰 바인딩 |
| `nav` | 내비게이션 (Navigation) | 딥링크, 라우팅, 화면 스택 |
| `wv` | 웹뷰 (WebView) | WebView 로드, JS Bridge |
| `quz` | 퀴즈 (Quiz) | 퀴즈 문제 로드, 캐시, 난이도 처리 — historyQuiz 전용 |

### 3-3. 서버 전용 카테고리

| 카테고리 코드 | 분류명 | 설명 |
|---------------|--------|------|
| `api` | API 처리 (API) | 요청 파싱, 응답 직렬화 |
| `sec` | 보안 (Security) | JWT 검증, CSRF, 권한 부족 |
| `que` | 메시지 큐 (Queue) | 큐 발행·소비 실패 |
| `ext` | 외부 서비스 (External) | 3rd Party API 호출 실패 |
| `mig` | 마이그레이션 (Migration) | DB 스키마 변경 오류 |

### 3-4. 웹 전용 카테고리

| 카테고리 코드 | 분류명 | 설명 |
|---------------|--------|------|
| `ui` | UI/화면 (UI) | 렌더링 오류, 컴포넌트 |
| `sto` | 웹 스토리지 (Storage) | localStorage, sessionStorage, Cookie |
| `seo` | SEO/메타 (SEO) | 메타태그, 구조화 데이터 |

---

## 4. 순번 체계 (4자리)

```
0001 ~ 0099   일반적인 오류 (정상 처리 흐름 내 오류)
0100 ~ 0199   시스템/환경 오류 (초기화, 설정 오류)
0200 ~ 0299   네트워크·외부 의존 오류
0300 ~ 0399   사용자 입력·권한 오류
0400 ~ 0499   데이터 오류 (파싱, 저장, 조회 실패)
0500 ~ 0599   보안·인증 오류
0600 ~ 0699   비즈니스 로직 오류
1000 ~ 1999   확장 예약 (대규모 기능 추가 시 활용)
9000 ~ 9999   알 수 없음·미분류 오류
```

> 순번은 카테고리 내에서 독립적으로 관리한다. `cam_0001_and`와 `aut_0001_and`는 서로 다른 오류다.

---

## 5. 카테고리별 오류 코드 정의

### 5-1. `cam` — 카메라

| 코드 | 메시지 (한) | 메시지 (영) | 비고 |
|------|-------------|-------------|------|
| `cam_0001_{p}` | 카메라 초기화 실패 | Camera initialization failed | 기기 미지원·권한 없음 제외 |
| `cam_0002_{p}` | 카메라 권한 거부 | Camera permission denied | → `perm` 카테고리와 연계 |
| `cam_0003_{p}` | 카메라 이미 사용 중 | Camera already in use | 다른 앱/세션 점유 |
| `cam_0004_{p}` | 촬영 실패 | Capture failed | 셔터 오류 |
| `cam_0005_{p}` | 이미지 저장 실패 | Image save failed | 저장소 부족·권한 |
| `cam_0006_{p}` | AI 모델 로드 실패 | AI model load failed | AI 카메라 모듈 전용 |
| `cam_0007_{p}` | AI 추론 실패 | AI inference failed | AI 카메라 모듈 전용 |
| `cam_0008_{p}` | 전면/후면 전환 실패 | Camera switch failed | |
| `cam_0009_{p}` | 지원하지 않는 해상도 | Unsupported resolution | |
| `cam_0010_{p}` | 플래시 제어 실패 | Flash control failed | |
| `cam_0100_{p}` | 카메라 하드웨어 미지원 | Camera hardware not supported | |

> `{p}` = `and` 또는 `ios`

---

### 5-2. `aut` — 인증

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `aut_0001_{p}` | 로그인 실패 (아이디/비밀번호) | Login failed — invalid credentials |
| `aut_0002_{p}` | 액세스 토큰 만료 | Access token expired |
| `aut_0003_{p}` | 리프레시 토큰 만료 | Refresh token expired |
| `aut_0004_{p}` | 토큰 갱신 실패 | Token refresh failed |
| `aut_0005_{p}` | 소셜 로그인 실패 | Social login failed |
| `aut_0006_{p}` | 로그아웃 실패 | Logout failed |
| `aut_0007_{p}` | 계정 잠김 | Account locked |
| `aut_0008_{p}` | 이메일 인증 미완료 | Email not verified |
| `aut_0500_{p}` | 유효하지 않은 토큰 | Invalid token |
| `aut_0501_{p}` | 인증 필요 (미로그인 상태) | Authentication required |
| `aut_0502_{p}` | 권한 부족 | Insufficient permissions |

---

### 5-3. `net` — 네트워크

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `net_0001_{p}` | 네트워크 연결 없음 | No network connection |
| `net_0002_{p}` | 요청 타임아웃 | Request timeout |
| `net_0003_{p}` | 서버 연결 실패 | Server connection failed |
| `net_0004_{p}` | SSL/TLS 오류 | SSL/TLS error |
| `net_0005_{p}` | DNS 조회 실패 | DNS resolution failed |
| `net_0200_{p}` | HTTP 4xx 오류 | HTTP client error |
| `net_0201_{p}` | HTTP 5xx 오류 | HTTP server error |
| `net_0202_{p}` | 응답 파싱 실패 | Response parsing failed |

---

### 5-4. `perm` — 권한

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `perm_0001_{p}` | 카메라 권한 없음 | Camera permission not granted |
| `perm_0002_{p}` | 마이크 권한 없음 | Microphone permission not granted |
| `perm_0003_{p}` | 위치 권한 없음 | Location permission not granted |
| `perm_0004_{p}` | 저장소 권한 없음 | Storage permission not granted |
| `perm_0005_{p}` | 연락처 권한 없음 | Contacts permission not granted |
| `perm_0006_{p}` | 알림 권한 없음 | Notification permission not granted |
| `perm_0007_{p}` | 블루투스 권한 없음 | Bluetooth permission not granted |

---

### 5-5. `pay` — 결제

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `pay_0001_{p}` | 결제 초기화 실패 | Payment initialization failed |
| `pay_0002_{p}` | 결제 요청 실패 | Payment request failed |
| `pay_0003_{p}` | 결제 취소 | Payment cancelled by user |
| `pay_0004_{p}` | 영수증 검증 실패 | Receipt verification failed |
| `pay_0005_{p}` | 결제 수단 없음 | No payment method available |
| `pay_0400_{p}` | 잘못된 결제 금액 | Invalid payment amount |
| `pay_0600_{p}` | 잔액 부족 | Insufficient balance |
| `pay_0601_{p}` | 결제 한도 초과 | Payment limit exceeded |

---

### 5-6. `db` — 데이터베이스/저장소

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `db_0001_{p}` | DB 연결 실패 | Database connection failed |
| `db_0002_{p}` | 쿼리 실행 실패 | Query execution failed |
| `db_0003_{p}` | 데이터 저장 실패 | Data write failed |
| `db_0004_{p}` | 데이터 조회 실패 | Data read failed |
| `db_0005_{p}` | 데이터 삭제 실패 | Data delete failed |
| `db_0006_{p}` | 트랜잭션 롤백 | Transaction rolled back |
| `db_0100_{p}` | DB 초기화 실패 | Database initialization failed |
| `db_0200_{p}` | DB 연결 타임아웃 | Database connection timeout |
| `db_0400_{p}` | 데이터 무결성 위반 | Data integrity violation |

---

### 5-7. `push` — 푸시 알림

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `push_0001_{p}` | FCM/APNs 토큰 등록 실패 | Push token registration failed |
| `push_0002_{p}` | 푸시 토큰 갱신 실패 | Push token refresh failed |
| `push_0001_ser` | 알림 발송 실패 | Notification delivery failed |
| `push_0002_ser` | 잘못된 디바이스 토큰 | Invalid device token |

---

### 5-8. `bio` — 생체인증 (모바일 전용)

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `bio_0001_{p}` | 생체인증 미지원 기기 | Biometric not supported |
| `bio_0002_{p}` | 생체정보 미등록 | No biometric enrolled |
| `bio_0003_{p}` | 생체인증 실패 | Biometric authentication failed |
| `bio_0004_{p}` | 생체인증 잠김 (시도 횟수 초과) | Biometric locked out |
| `bio_0005_{p}` | 생체인증 취소 | Biometric cancelled by user |

---

### 5-9. `wv` — 웹뷰 (모바일 전용)

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `wv_0001_{p}` | 웹뷰 페이지 로드 실패 | WebView page load failed |
| `wv_0002_{p}` | JS Bridge 호출 실패 | JS Bridge call failed |
| `wv_0003_{p}` | 웹뷰 초기화 실패 | WebView initialization failed |
| `wv_0004_{p}` | 잘못된 URL 스킴 | Invalid URL scheme |

---

### 5-10. `loc` — 위치

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `loc_0001_{p}` | 위치 서비스 비활성화 | Location service disabled |
| `loc_0002_{p}` | 위치 정보 취득 실패 | Location acquisition failed |
| `loc_0003_{p}` | 위치 정보 타임아웃 | Location request timeout |
| `loc_0004_{p}` | 지오코딩 실패 | Geocoding failed |

---

### 5-11. `sys` — 시스템

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `sys_0001_{p}` | 메모리 부족 | Out of memory |
| `sys_0002_{p}` | 저장 공간 부족 | Insufficient storage |
| `sys_0003_{p}` | 앱 크래시 (예기치 않은 종료) | App crashed unexpectedly |
| `sys_0100_{p}` | 앱 초기화 실패 | App initialization failed |
| `sys_0101_{p}` | 환경 설정 로드 실패 | Config load failed |
| `sys_9000_{p}` | 알 수 없는 시스템 오류 | Unknown system error |

---

### 5-12. `sec` — 보안 (서버 전용)

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `sec_0001_ser` | JWT 서명 검증 실패 | JWT signature verification failed |
| `sec_0002_ser` | CSRF 토큰 불일치 | CSRF token mismatch |
| `sec_0003_ser` | API 키 유효하지 않음 | Invalid API key |
| `sec_0004_ser` | IP 차단 | IP address blocked |
| `sec_0005_ser` | 요청 속도 제한 초과 | Rate limit exceeded |

---

### 5-13. `ext` — 외부 서비스 (서버 전용)

| 코드 | 메시지 (한) | 메시지 (영) |
|------|-------------|-------------|
| `ext_0001_ser` | 외부 API 호출 실패 | External API call failed |
| `ext_0002_ser` | 외부 서비스 타임아웃 | External service timeout |
| `ext_0003_ser` | 외부 서비스 응답 파싱 실패 | External service response parse failed |
| `ext_0004_ser` | SMS 발송 실패 | SMS delivery failed |
| `ext_0005_ser` | 이메일 발송 실패 | Email delivery failed |

---

## 6. 오류 코드 명명 규칙 요약

```
규칙 1. suffix는 반드시 소문자 3자 (and / ios / ser / web)
규칙 2. 카테고리 코드는 반드시 소문자 2~4자
규칙 3. 순번은 반드시 4자리 숫자 (앞에 0 패딩)  ← 0001 ~ 9999
규칙 4. 구분자는 언더스코어(_) 사용
규칙 5. 플랫폼 공통 오류는 suffix만 달리하고 카테고리+순번은 동일하게 유지
규칙 6. 새 오류 코드 추가 시 반드시 이 문서를 업데이트하고 커밋
규칙 7. 삭제된 오류 코드는 실제 삭제 대신 [Deprecated] 표시 후 유지
규칙 8. 순번 9000~9999는 미분류/임시 코드 — 출시 전 반드시 정식 코드로 교체
```

---

## 7. 코드 구현 가이드

### Android (Kotlin)

```kotlin
// skills/kotlin-conventions.md 참조
// ErrorCode.kt — 단일 파일로 관리

object ErrorCode {
    // 카메라
    const val CAM_0001_AND = "cam_0001_and"  // 카메라 초기화 실패
    const val CAM_0002_AND = "cam_0002_and"  // 카메라 권한 거부
    const val CAM_0006_AND = "cam_0006_and"  // AI 모델 로드 실패
    const val CAM_0007_AND = "cam_0007_and"  // AI 추론 실패

    // 인증
    const val AUT_0001_AND = "aut_0001_and"  // 로그인 실패
    const val AUT_0002_AND = "aut_0002_and"  // 액세스 토큰 만료
    const val AUT_0003_AND = "aut_0003_and"  // 리프레시 토큰 만료

    // 네트워크
    const val NET_0001_AND = "net_0001_and"  // 네트워크 연결 없음
    const val NET_0002_AND = "net_0002_and"  // 요청 타임아웃

    // ... 이하 동일한 패턴으로 추가
}

// 사용 예
data class AppError(
    val code: String,
    val message: String,
    val cause: Throwable? = null
)

fun handleCameraError(e: Exception): AppError {
    return when (e) {
        is CameraAccessException -> AppError(ErrorCode.CAM_0001_AND, "카메라 초기화 실패", e)
        is SecurityException     -> AppError(ErrorCode.CAM_0002_AND, "카메라 권한 거부", e)
        else                     -> AppError("cam_9000_and", "알 수 없는 카메라 오류", e)
    }
}
```

### iOS (Swift)

```swift
// skills/swift-conventions.md 참조
// ErrorCode.swift — 단일 파일로 관리

enum ErrorCode: String {
    // 카메라
    case cam0001Ios = "cam_0001_ios"  // 카메라 초기화 실패
    case cam0002Ios = "cam_0002_ios"  // 카메라 권한 거부
    case cam0006Ios = "cam_0006_ios"  // AI 모델 로드 실패
    case cam0007Ios = "cam_0007_ios"  // AI 추론 실패

    // 인증
    case aut0001Ios = "aut_0001_ios"  // 로그인 실패
    case aut0002Ios = "aut_0002_ios"  // 액세스 토큰 만료
    case aut0003Ios = "aut_0003_ios"  // 리프레시 토큰 만료

    // ... 이하 동일한 패턴으로 추가
}

struct AppError: Error {
    let code: ErrorCode
    let message: String
    let underlying: Error?
}
```

### 서버 (예: Kotlin Spring / Node.js)

```kotlin
// Kotlin Spring 예
enum class ErrorCode(val code: String, val message: String) {
    AUT_0001_SER("aut_0001_ser", "로그인 실패"),
    AUT_0500_SER("aut_0500_ser", "유효하지 않은 토큰"),
    DB_0001_SER("db_0001_ser",  "DB 연결 실패"),
    SEC_0005_SER("sec_0005_ser", "요청 속도 제한 초과"),
}

// API 응답 예
data class ErrorResponse(
    val code: String,      // "aut_0001_ser"
    val message: String,   // "로그인 실패"
    val detail: String? = null
)
```

---

## 8. 로깅 및 모니터링 연계

오류 발생 시 아래 필드를 함께 기록한다.

```json
{
  "errorCode": "cam_0006_and",
  "platform": "android",
  "appVersion": "1.2.0",
  "osVersion": "14",
  "deviceModel": "Galaxy S24",
  "userId": "u_xxxxxxxx",
  "sessionId": "sess_xxxxxxxx",
  "timestamp": "2026-03-05T09:00:00Z",
  "message": "AI 모델 로드 실패",
  "stackTrace": "..."
}
```

- **Firebase Crashlytics / Sentry**: `errorCode` 필드를 커스텀 키로 등록하여 집계
- **Slack/Telegram 알림**: `*_ser` suffix 오류 중 0500번대는 즉시 알림 설정 권고
- **노션 이슈 트래커**: 이슈 등록 시 `errorCode` 필드 필수 포함

---

## 9. 오류 코드 추가 절차

1. 이 문서에서 해당 카테고리 테이블에 신규 행 추가
2. 순번이 기존 코드와 중복되지 않는지 확인
3. Android/iOS/Server/Web 각 플랫폼의 `ErrorCode` 상수 파일 업데이트
4. 변경 내용을 `CHANGELOG.md`에 기록
5. `git commit -m "docs: add error code {코드} — {설명}"` 으로 커밋

---

## 10. 타사 오류 코드 체계 비교

앱-웹-서버 환경을 운영하는 주요 회사들의 방식은 크게 세 가지 유형으로 나뉜다.

---

### 유형 1 — 숫자 범위 분리형 (카카오, 네이버 등 국내)

숫자 범위로 도메인을 구분하는 방식. HTTP 상태코드 체계에서 착안.

```
-1       : 공통 실패 (기타)
10000대  : 인증/회원
20000대  : 콘텐츠/검색
30000대  : 결제/쇼핑
40000대  : 메시지/알림
```

**카카오 API 응답 예시**
```json
{
  "code": -402,
  "msg": "해당 앱에 유효한 플랫폼이 없습니다"
}
```

- 음수를 에러, 양수를 성공/경고로 구분
- 플랫폼 prefix 없이 서버 단일 코드로 통합 관리 (앱/웹 공통 수신)

---

### 유형 2 — 문자열 열거형 (Stripe, Twilio 등 글로벌 핀테크)

코드 자체를 사람이 읽을 수 있는 문자열로 정의.

**Stripe 방식**
```json
{
  "error": {
    "type": "card_error",
    "code": "card_declined",
    "decline_code": "insufficient_funds",
    "message": "Your card has insufficient funds."
  }
}
```

- `type`(대분류) + `code`(소분류) + `decline_code`(세부 사유)의 계층 구조
- 클라이언트(앱/웹) 동일 코드 수신, 플랫폼 구분 없음

**Twilio 방식**
```json
{
  "code": 21211,
  "message": "The 'To' number is not a valid phone number",
  "more_info": "https://www.twilio.com/docs/errors/21211"
}
```

- 5자리 숫자 + 문서 URL을 함께 제공하여 자가 진단 유도

---

### 유형 3 — 도메인+예외명 조합형 (AWS) ← 우리 체계에서 참고한 방식

AWS는 서비스 도메인을 prefix처럼 사용하고 예외 클래스명 자체를 에러 식별자로 활용.

**AWS 원형**
```
InvalidParameterException
ThrottlingException
AccessDeniedException
ResourceNotFoundException
```
- `X-Amzn-ErrorType` HTTP 헤더로 전달
- 플랫폼 구분은 별도로 하지 않음

**우리 프로젝트에 적용한 변형 — prefix 명확 구분 추가**

AWS의 예외명 방식에 우리의 플랫폼 prefix를 결합하면 다음과 같다.

```json
{
  "errorCode": "aut_0500_and",
  "errorType": "InvalidTokenException",
  "platform": "android",
  "message": "유효하지 않은 토큰"
}
```

```json
{
  "errorCode": "sec_0005_ser",
  "errorType": "RateLimitExceededException",
  "platform": "server",
  "message": "요청 속도 제한 초과"
}
```

- `errorCode` : 우리 체계의 `{prefix}_{category}_{4자리}` — 로그 필터링·집계용
- `errorType` : AWS식 예외 클래스명 — 개발자 가독성·IDE 자동완성용
- `platform` : 발생 플랫폼 명시 — 모니터링 대시보드 분류용

이 조합을 통해 **코드(기계 친화) + 예외명(사람 친화) + prefix(플랫폼 식별)** 세 가지를 모두 확보할 수 있다.

**Android (Kotlin) 구현 예**
```kotlin
data class AppError(
    val code: String,           // "cam_0006_and"
    val errorType: String,      // "AiModelLoadException"
    val message: String,
    val cause: Throwable? = null
)

sealed class CameraException(message: String) : Exception(message) {
    class InitializationException(msg: String) : CameraException(msg)
    class AiModelLoadException(msg: String)    : CameraException(msg)
    class AiInferenceException(msg: String)    : CameraException(msg)
}

fun handleCameraError(e: Exception): AppError = when (e) {
    is CameraException.InitializationException ->
        AppError(ErrorCode.CAM_0001_AND, "InitializationException", "카메라 초기화 실패", e)
    is CameraException.AiModelLoadException ->
        AppError(ErrorCode.CAM_0006_AND, "AiModelLoadException", "AI 모델 로드 실패", e)
    is CameraException.AiInferenceException ->
        AppError(ErrorCode.CAM_0007_AND, "AiInferenceException", "AI 추론 실패", e)
    else ->
        AppError("cam_9000_and", "UnknownCameraException", "알 수 없는 카메라 오류", e)
}
```

**서버 (Kotlin Spring) 구현 예**
```kotlin
enum class ErrorCode(val code: String, val errorType: String, val message: String) {
    AUT_0001_SER("aut_0001_ser", "InvalidCredentialsException",   "로그인 실패"),
    AUT_0500_SER("aut_0500_ser", "InvalidTokenException",         "유효하지 않은 토큰"),
    SEC_0005_SER("sec_0005_ser", "RateLimitExceededException",    "요청 속도 제한 초과"),
    SER_DB_0001 ("db_0001_ser",  "DatabaseConnectionException",   "DB 연결 실패"),
}

data class ErrorResponse(
    val errorCode: String,    // "aut_0001_ser"
    val errorType: String,    // "InvalidTokenException"
    val message: String,
    val detail: String? = null
)
```

---

### 체계별 비교

| 항목 | **우리 체계** | 카카오식 | Stripe식 | **AWS식+prefix (권장 참고)** |
|------|:-----------:|:-------:|:-------:|:---------------------------:|
| 플랫폼 구분 | ✅ prefix | ❌ | ❌ | ✅ suffix + errorType |
| 가독성 | 중 | 하 | 상 | 상 |
| 확장성 | 높음 | 중 | 중 | 높음 |
| 로그 검색 | 쉬움 | 보통 | 쉬움 | 매우 쉬움 |
| IDE 자동완성 | 보통 | 어려움 | 보통 | ✅ 예외 클래스 활용 |
| 앱 디버깅 | ✅ 즉시 식별 | 불편 | 불편 | ✅ 즉시 식별 |

> **결론**: 우리 체계의 `errorCode` + AWS식의 `errorType`(예외 클래스명)을 함께 사용하면
> 로그 집계(기계)와 코드 디버깅(사람) 양쪽을 모두 커버할 수 있다.

---

### 5-14. `quz` — 퀴즈 (historyQuiz Android 전용)

| 코드 | 메시지 (한) | 메시지 (영) | 비고 |
|------|-------------|-------------|------|
| `quz_0001_and` | 문제 로드 실패 | Question load failed | 외부 API + 로컬 캐시 모두 실패 |
| `quz_0002_and` | 캐시된 문제 없음 | No cached questions available | 오프라인 + 캐시 비어있을 때 |
| `quz_0003_and` | 외부 문제 API 호출 실패 | External question API call failed | Retrofit 호출 오류 |
| `quz_0600_and` | 유효하지 않은 난이도 설정 | Invalid difficulty setting | 난이도 값 범위 벗어남 |

---

## 11. 개정 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|-----------|
| v1.0 | 2026-03-05 | 최초 작성 |
| v1.1 | 2026-03-05 | 순번 3자리 → 4자리 변경, 미분류 대역 9000~9999로 조정 |
| v1.2 | 2026-03-05 | 타사 오류 코드 체계 비교 섹션 추가, AWS식+prefix 조합 구현 가이드 추가 |
| v1.3 | 2026-03-05 | 포맷 변경: `{prefix}_{category}_{num}` → `{category}_{num}_{suffix}` (크로스플랫폼 식별성 개선) |
| v1.4 | 2026-03-07 | `quz` 카테고리 추가 (historyQuiz Android 퀴즈 전용, session-0-design) |
