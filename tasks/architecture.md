# 아키텍처 설계 — historyQuiz

> 작성 세션: session-0-design
> 작성일: 2026-03-07
> Source of Truth: 이 파일은 아키텍처의 유일한 원본이다.

---

## 1. 기술 스택

| 분류 | 기술 | 버전(목표) |
|------|------|------------|
| 언어 | Kotlin | 2.x |
| 아키텍처 패턴 | Clean Architecture (Data → Domain → UI) | — |
| UI 패턴 | MVVM + StateFlow | — |
| DI | Koin | 3.x |
| 네트워크 | Retrofit2 + OkHttp3 | Retrofit 2.11 / OkHttp 4.x |
| 비동기 | Coroutines + Flow | 1.9.x |
| 로컬 DB | Room | 2.6.x |
| 설정 저장 | DataStore (Proto) | 1.1.x |
| 네비게이션 | AndroidX Navigation Component | 2.8.x |
| 이미지 | Coil | 2.x |
| 인증 | Google Sign-In + Firebase Auth | 최신 |
| 분석 | Firebase Analytics | 최신 |
| 크래시 | Firebase Crashlytics | 최신 |
| 차트 (P1) | MPAndroidChart | 3.1.x |
| 빌드 | Gradle (KTS) | 8.x |
| 최소 SDK | API 26 (Android 8.0) | — |
| 타겟 SDK | API 35 | — |
| 디자인 시스템 | Material Design 3 | — |

---

## 2. 아키텍처 레이어

```
┌─────────────────────────────────────────────────────┐
│                   Presentation Layer                 │
│  Fragment / Activity  ←→  ViewModel (StateFlow)     │
│  Navigation Component                                │
└────────────────────┬────────────────────────────────┘
                     │ (UseCase 호출)
┌────────────────────▼────────────────────────────────┐
│                    Domain Layer                      │
│  UseCase  ←→  Repository Interface  ←→  Model       │
└────────────────────┬────────────────────────────────┘
                     │ (Repository 구현체)
┌────────────────────▼────────────────────────────────┐
│                     Data Layer                       │
│  RepositoryImpl                                      │
│   ├── RemoteDataSource (Retrofit → 외부 문제 API)    │
│   ├── LocalDataSource  (Room → 문제 캐시, 결과)      │
│   └── DataStore (사용자 설정, 온보딩 상태)            │
└─────────────────────────────────────────────────────┘
```

### 의존성 방향

```
Presentation → Domain ← Data
(단방향, Domain은 Presentation/Data를 알지 못한다)
```

---

## 3. 모듈 구조

단일 모듈 (`:app`) 패키지 분리 방식으로 운영한다.
규모 확대 시 멀티 모듈로 전환 가능하도록 패키지 경계를 명확히 한다.

```
com.historyquiz.app
├── core/
│   ├── di/             Koin 모듈 정의
│   ├── error/          AppError.kt, ErrorCode.kt
│   ├── network/        OkHttpClient, RetrofitBuilder, AuthInterceptor
│   └── util/           Extension functions, DateUtils, etc.
├── data/
│   ├── local/
│   │   ├── db/         AppDatabase.kt
│   │   ├── dao/        QuestionDao.kt, QuizResultDao.kt
│   │   └── entity/     QuestionEntity.kt, QuizResultEntity.kt, WrongAnswerEntity.kt
│   ├── remote/
│   │   ├── api/        QuestionApiService.kt
│   │   └── dto/        QuestionDto.kt, QuestionListDto.kt
│   ├── repository/     QuestionRepositoryImpl.kt, AuthRepositoryImpl.kt,
│   │                   QuizResultRepositoryImpl.kt
│   └── datastore/      UserPreferencesDataStore.kt
├── domain/
│   ├── model/          Question.kt, QuizResult.kt, User.kt, WrongAnswer.kt
│   ├── repository/     QuestionRepository.kt, AuthRepository.kt,
│   │                   QuizResultRepository.kt
│   └── usecase/
│       ├── auth/       SignInWithGoogleUseCase.kt, SignOutUseCase.kt,
│       │               GetCurrentUserUseCase.kt, IsLoggedInUseCase.kt
│       ├── quiz/       GetQuestionsUseCase.kt, MixQuestionsUseCase.kt,
│       │               SubmitQuizUseCase.kt
│       └── result/     GetQuizHistoryUseCase.kt, GetStatisticsUseCase.kt,
│                       GetStreakUseCase.kt
└── presentation/
    ├── MainActivity.kt
    ├── splash/         SplashFragment.kt
    ├── onboarding/     OnboardingFragment.kt, OnboardingPagerAdapter.kt
    ├── auth/           LoginFragment.kt, AuthViewModel.kt
    ├── home/           HomeFragment.kt, HomeViewModel.kt
    ├── quiz/
    │   ├── select/     DifficultySelectFragment.kt
    │   └── play/       QuizPlayFragment.kt, QuizPlayViewModel.kt,
    │                   QuizResultFragment.kt
    ├── statistics/     StatisticsFragment.kt, StatisticsViewModel.kt
    └── settings/       SettingsFragment.kt, SettingsViewModel.kt
```

---

## 4. 핵심 데이터 흐름

### 4-1. 인증 흐름

```
LoginFragment
  → AuthViewModel.signInWithGoogle()
  → SignInWithGoogleUseCase
  → AuthRepository.signInWithGoogle(idToken)
  → Firebase Auth SDK (Google ID Token 교환)
  → DataStore (로그인 상태 저장)
  → [성공] HomeFragment 이동
  → [실패] ErrorCode.AUT_0005_AND → 에러 UI 표시
```

### 4-2. 문제 로드 흐름

```
QuizPlayViewModel.loadQuestions(difficulty)
  → GetQuestionsUseCase → MixQuestionsUseCase
  → QuestionRepository.getQuestions(difficulty, count)
      ├── 로컬(Room): 캐시된 문제 조회
      └── 원격(Retrofit): 외부 API에서 신규 문제 조회
           → Room에 캐시 저장
  → 혼합 (로컬 7 : 신규 3 비율, 네트워크 없으면 로컬 100%)
  → QuizUiState.Ready(questions) → QuizPlayFragment 렌더링
```

### 4-3. 퀴즈 결과 저장 흐름

```
QuizPlayViewModel.submitAnswer(questionId, selectedIndex)
  → 정답 비교 → WrongAnswer 목록 누적
QuizPlayViewModel.finishQuiz()
  → SubmitQuizUseCase(quizResult)
  → QuizResultRepository.saveResult(quizResult)
  → Room 저장 (QuizResultEntity + WrongAnswerEntity)
  → QuizResultFragment 이동 (결과 표시)
```

---

## 5. 상태 관리

각 ViewModel은 `StateFlow<UiState>`를 노출한다.

```kotlin
// 공통 패턴
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val error: AppError) : UiState<Nothing>()
}
```

**QuizUiState 예시:**
```kotlin
sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Ready(val questions: List<Question>, val current: Int = 0) : QuizUiState()
    data class Answered(val isCorrect: Boolean, val correctIndex: Int) : QuizUiState()
    data class Finished(val result: QuizResult) : QuizUiState()
    data class Error(val appError: AppError) : QuizUiState()
}
```

---

## 6. Room DB 스키마

### 6-1. questions 테이블

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | TEXT PK | 외부 API 문제 고유 ID |
| content | TEXT | 문제 본문 |
| options | TEXT | JSON 배열 (선택지 목록) |
| answer_index | INTEGER | 정답 인덱스 (0-based) |
| level | TEXT | "basic" / "advanced" |
| category | TEXT | 시대 분류 (고조선, 삼국 등) |
| cached_at | INTEGER | 캐시 시각 (Unix timestamp) |
| source | TEXT | "local" / "remote" |

### 6-2. quiz_results 테이블

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | INTEGER PK AUTOINCREMENT | 결과 ID |
| played_at | INTEGER | 플레이 시각 (Unix timestamp) |
| level | TEXT | "basic" / "advanced" |
| total_count | INTEGER | 총 문제 수 |
| correct_count | INTEGER | 정답 수 |
| duration_sec | INTEGER | 소요 시간 (초) |

### 6-3. wrong_answers 테이블

| 컬럼 | 타입 | 설명 |
|------|------|------|
| id | INTEGER PK AUTOINCREMENT | |
| result_id | INTEGER FK → quiz_results.id | |
| question_id | TEXT | 문제 ID |
| selected_index | INTEGER | 사용자가 선택한 인덱스 |

---

## 7. DataStore 키 목록

| 키 | 타입 | 설명 |
|----|------|------|
| `is_onboarding_done` | Boolean | 온보딩 완료 여부 |
| `last_signed_in_email` | String | 마지막 로그인 이메일 (UI 표시용) |
| `notification_enabled` | Boolean | 알림 활성화 여부 |
| `weekly_goal` | Int | 주간 목표 문제 수 (P1) |

---

## 8. 네트워크 설정

```
OkHttpClient
├── 타임아웃: connect 10s / read 30s / write 30s
├── AuthInterceptor: Firebase ID Token → "Authorization: Bearer {token}" 헤더 추가
└── HttpLoggingInterceptor: DEBUG 빌드에만 활성화

Retrofit
└── Gson Converter (또는 Kotlinx Serialization)

환경별 BaseUrl (BuildConfig.BASE_URL)
├── dev:      https://dev-api.historyquiz.com/v1 (또는 외부 공개 API)
├── staging:  https://staging-api.historyquiz.com/v1
└── prod:     https://api.historyquiz.com/v1
```

> ⚠️ 외부 공개 API URL은 계약/확인 후 업데이트 필요. 현재 플레이스홀더 사용.

---

## 9. 빌드 플레이버

```kotlin
// build.gradle.kts (app)
android {
    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            applicationIdSuffix = ".dev"
            buildConfigField("String", "BASE_URL", "\"https://dev-api.historyquiz.com/v1\"")
        }
        create("staging") {
            applicationIdSuffix = ".staging"
            buildConfigField("String", "BASE_URL", "\"https://staging-api.historyquiz.com/v1\"")
        }
        create("prod") {
            buildConfigField("String", "BASE_URL", "\"https://api.historyquiz.com/v1\"")
        }
    }
}
```

---

## 10. 오류 코드 정의

> 기준 문서: `guides/error-code-guide.md` v1.3

### 10-1. 인증 (aut) — Android

| 코드 | 메시지 (한) | 발생 지점 |
|------|------------|-----------|
| `aut_0004_and` | 토큰 갱신 실패 | Firebase Auth 토큰 갱신 시 |
| `aut_0005_and` | 소셜 로그인 실패 | Google Sign-In 취소/오류 시 |
| `aut_0006_and` | 로그아웃 실패 | Firebase Auth signOut 오류 시 |
| `aut_0501_and` | 인증 필요 (미로그인 상태) | 비로그인 상태에서 보호된 화면 진입 시 |

### 10-2. 네트워크 (net) — Android

| 코드 | 메시지 (한) | 발생 지점 |
|------|------------|-----------|
| `net_0001_and` | 네트워크 연결 없음 | 외부 문제 API 호출 시 |
| `net_0002_and` | 요청 타임아웃 | Retrofit call timeout |
| `net_0200_and` | HTTP 4xx 오류 | 외부 API 클라이언트 오류 |
| `net_0201_and` | HTTP 5xx 오류 | 외부 API 서버 오류 |
| `net_0202_and` | 응답 파싱 실패 | Gson/Serialization 오류 |

### 10-3. 데이터베이스 (db) — Android

| 코드 | 메시지 (한) | 발생 지점 |
|------|------------|-----------|
| `db_0003_and` | 데이터 저장 실패 | 퀴즈 결과 Room 저장 시 |
| `db_0004_and` | 데이터 조회 실패 | 문제/결과 Room 조회 시 |
| `db_0100_and` | DB 초기화 실패 | AppDatabase 초기화 시 |

### 10-4. 퀴즈 (quz) — Android [신규 카테고리]

> 이 카테고리는 `guides/error-code-guide.md`에도 추가되었다.

| 코드 | 메시지 (한) | 메시지 (영) | 발생 지점 |
|------|------------|------------|-----------|
| `quz_0001_and` | 문제 로드 실패 | Question load failed | 외부 API + 로컬 모두 실패 시 |
| `quz_0002_and` | 캐시된 문제 없음 | No cached questions available | 오프라인 + 캐시 비어있을 때 |
| `quz_0003_and` | 외부 문제 API 호출 실패 | External question API call failed | Retrofit 호출 오류 |
| `quz_0600_and` | 유효하지 않은 난이도 설정 | Invalid difficulty setting | 난이도 값 범위 벗어남 |

### 10-5. UI (ui) — Android

| 코드 | 메시지 (한) | 발생 지점 |
|------|------------|-----------|
| `ui_0001_and` | 화면 렌더링 오류 | Fragment 초기화 오류 |

---

## 11. 보안 고려사항

- Firebase Auth ID Token은 앱 메모리에만 보관 (SharedPreferences/파일 저장 금지)
- google-services.json은 `.gitignore`에 포함 (CI에서는 환경 변수로 주입)
- 외부 문제 API Key가 있을 경우 `local.properties`에 저장하고 BuildConfig로 주입
- ProGuard/R8 난독화 활성화 (release 빌드)
- Network Security Config: HTTPS only (cleartext 비허용)

---

## 12. 개정 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|-----------|
| v1.0 | 2026-03-07 | 최초 작성 (session-0-design) |
