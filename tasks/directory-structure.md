# 디렉토리 구조 — historyQuiz

> 작성 세션: session-0-design
> 작성일: 2026-03-07
> 대상 세션: session-1-dev

---

## 전체 프로젝트 루트

```
TEST-HISTORY_QUIZofKorea_AOS/           ← Android 저장소 루트
├── app/
│   └── src/
│       ├── main/
│       ├── test/
│       └── androidTest/
├── buildSrc/                            ← (선택) 빌드 스크립트 공유
├── gradle/
│   └── libs.versions.toml              ← Version Catalog
├── tasks/                               ← 설계 산출물 (세션 0)
├── guides/                              ← 가이드 문서
├── docs/                                ← 배포 문서
├── reports/                             ← 세션 간 리포트
├── memos/
├── skills/
├── scripts/
│   └── notify-telegram.sh
├── deploy/
├── .session-status.json
├── CLAUDE.md
├── build.gradle.kts                     ← 루트 빌드 스크립트
├── settings.gradle.kts
├── .gitignore
└── .gitlab-ci.yml
```

---

## app/ 소스 구조

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/historyquiz/app/
│   │   │   │
│   │   │   ├── core/                              ← 공통 인프라
│   │   │   │   ├── di/
│   │   │   │   │   ├── NetworkModule.kt
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   ├── RepositoryModule.kt
│   │   │   │   │   ├── UseCaseModule.kt
│   │   │   │   │   └── ViewModelModule.kt
│   │   │   │   ├── error/
│   │   │   │   │   ├── AppError.kt                ← data class (code, errorType, message, cause)
│   │   │   │   │   └── ErrorCode.kt               ← const val 상수 목록 (architecture.md §10)
│   │   │   │   ├── network/
│   │   │   │   │   ├── AuthInterceptor.kt         ← Firebase ID Token 헤더 주입
│   │   │   │   │   └── NetworkStatusChecker.kt    ← 연결 상태 확인 유틸
│   │   │   │   └── util/
│   │   │   │       ├── DateUtils.kt
│   │   │   │       ├── FlowUtils.kt               ← collectWhileStarted 등 extension
│   │   │   │       └── StringUtils.kt
│   │   │   │
│   │   │   ├── data/                              ← Data Layer
│   │   │   │   ├── local/
│   │   │   │   │   ├── db/
│   │   │   │   │   │   └── AppDatabase.kt         ← Room @Database (entities, version, migrations)
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── QuestionDao.kt
│   │   │   │   │   │   └── QuizResultDao.kt       ← QuizResultEntity + WrongAnswerEntity 쿼리
│   │   │   │   │   └── entity/
│   │   │   │   │       ├── QuestionEntity.kt
│   │   │   │   │       ├── QuizResultEntity.kt
│   │   │   │   │       └── WrongAnswerEntity.kt
│   │   │   │   ├── remote/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   └── QuestionApiService.kt  ← Retrofit @GET 인터페이스
│   │   │   │   │   └── dto/
│   │   │   │   │       ├── QuestionDto.kt
│   │   │   │   │       └── QuestionListDto.kt
│   │   │   │   ├── repository/
│   │   │   │   │   ├── AuthRepositoryImpl.kt
│   │   │   │   │   ├── QuestionRepositoryImpl.kt
│   │   │   │   │   └── QuizResultRepositoryImpl.kt
│   │   │   │   └── datastore/
│   │   │   │       └── UserPreferencesDataStore.kt ← is_onboarding_done, notification 등
│   │   │   │
│   │   │   ├── domain/                            ← Domain Layer (순수 Kotlin, Android 의존 없음)
│   │   │   │   ├── model/
│   │   │   │   │   ├── Question.kt                ← id, content, options, answerIndex, level, category
│   │   │   │   │   ├── QuizResult.kt              ← playedAt, level, totalCount, correctCount, duration
│   │   │   │   │   ├── WrongAnswer.kt             ← questionId, selectedIndex, question(참조)
│   │   │   │   │   └── User.kt                    ← uid, displayName, email, photoUrl
│   │   │   │   ├── repository/
│   │   │   │   │   ├── AuthRepository.kt
│   │   │   │   │   ├── QuestionRepository.kt
│   │   │   │   │   └── QuizResultRepository.kt
│   │   │   │   └── usecase/
│   │   │   │       ├── auth/
│   │   │   │       │   ├── SignInWithGoogleUseCase.kt
│   │   │   │       │   ├── SignOutUseCase.kt
│   │   │   │       │   ├── GetCurrentUserUseCase.kt
│   │   │   │       │   └── IsLoggedInUseCase.kt
│   │   │   │       ├── quiz/
│   │   │   │       │   ├── GetQuestionsUseCase.kt
│   │   │   │       │   ├── MixQuestionsUseCase.kt ← 로컬 7 : 신규 3 혼합 로직
│   │   │   │       │   └── SubmitQuizUseCase.kt
│   │   │   │       └── result/
│   │   │   │           ├── GetQuizHistoryUseCase.kt
│   │   │   │           ├── GetStatisticsUseCase.kt
│   │   │   │           └── GetStreakUseCase.kt
│   │   │   │
│   │   │   └── presentation/                      ← UI Layer
│   │   │       ├── MainActivity.kt                ← single activity, NavHostFragment
│   │   │       ├── splash/
│   │   │       │   └── SplashFragment.kt
│   │   │       ├── onboarding/
│   │   │       │   ├── OnboardingFragment.kt
│   │   │       │   └── OnboardingPagerAdapter.kt
│   │   │       ├── auth/
│   │   │       │   ├── LoginFragment.kt
│   │   │       │   └── AuthViewModel.kt
│   │   │       ├── home/
│   │   │       │   ├── HomeFragment.kt
│   │   │       │   └── HomeViewModel.kt
│   │   │       ├── quiz/
│   │   │       │   ├── select/
│   │   │       │   │   └── DifficultySelectFragment.kt
│   │   │       │   └── play/
│   │   │       │       ├── QuizPlayFragment.kt
│   │   │       │       ├── QuizPlayViewModel.kt
│   │   │       │       └── QuizResultFragment.kt
│   │   │       ├── statistics/
│   │   │       │   ├── StatisticsFragment.kt
│   │   │       │   └── StatisticsViewModel.kt
│   │   │       └── settings/
│   │   │           ├── SettingsFragment.kt
│   │   │           └── SettingsViewModel.kt
│   │   │
│   │   └── res/
│   │       ├── drawable/
│   │       │   ├── ic_logo.xml
│   │       │   ├── ic_streak.xml
│   │       │   └── bg_option_*.xml                ← 선택지 배경 (기본/정답/오답 상태)
│   │       ├── layout/
│   │       │   ├── activity_main.xml
│   │       │   ├── fragment_splash.xml
│   │       │   ├── fragment_onboarding.xml
│   │       │   ├── item_onboarding_page.xml
│   │       │   ├── fragment_login.xml
│   │       │   ├── fragment_home.xml
│   │       │   ├── fragment_difficulty_select.xml
│   │       │   ├── fragment_quiz_play.xml
│   │       │   ├── fragment_quiz_result.xml
│   │       │   ├── fragment_statistics.xml
│   │       │   ├── fragment_settings.xml
│   │       │   └── item_quiz_result.xml           ← 결과 목록 아이템
│   │       ├── navigation/
│   │       │   └── nav_graph.xml                  ← 전체 네비게이션 그래프
│   │       ├── values/
│   │       │   ├── colors.xml
│   │       │   ├── strings.xml
│   │       │   ├── themes.xml                     ← Material Design 3 테마
│   │       │   └── dimens.xml
│   │       ├── values-night/
│   │       │   └── themes.xml                     ← 다크 모드 테마 (선택)
│   │       └── xml/
│   │           └── network_security_config.xml    ← HTTPS only
│   │
│   ├── test/                                      ← 단위 테스트 (JVM)
│   │   └── java/com/historyquiz/app/
│   │       ├── usecase/
│   │       │   ├── GetQuestionsUseCaseTest.kt
│   │       │   ├── MixQuestionsUseCaseTest.kt
│   │       │   └── SubmitQuizUseCaseTest.kt
│   │       ├── viewmodel/
│   │       │   ├── QuizPlayViewModelTest.kt
│   │       │   └── HomeViewModelTest.kt
│   │       └── repository/
│   │           └── QuestionRepositoryImplTest.kt
│   │
│   └── androidTest/                               ← 통합/UI 테스트
│       └── java/com/historyquiz/app/
│           ├── db/
│           │   ├── QuestionDaoTest.kt
│           │   └── QuizResultDaoTest.kt
│           └── ui/
│               ├── LoginFlowTest.kt
│               └── QuizFlowTest.kt
│
├── google-services.json                           ← (gitignore) dev 환경용
└── build.gradle.kts
```

---

## 빌드 플레이버별 소스셋

```
src/
├── main/        ← 공통 소스
├── dev/         ← dev 플레이버 전용 (google-services.json dev 버전)
├── staging/     ← staging 플레이버 전용
└── prod/        ← prod 플레이버 전용 (google-services.json prod 버전)
```

---

## 주요 설정 파일

| 파일 | 위치 | 설명 |
|------|------|------|
| `libs.versions.toml` | `gradle/` | 전체 의존성 버전 관리 |
| `build.gradle.kts` | `app/` | 앱 빌드 설정, 플레이버, 의존성 |
| `settings.gradle.kts` | 루트 | 모듈 포함 설정 |
| `nav_graph.xml` | `res/navigation/` | 전체 화면 흐름 정의 |
| `network_security_config.xml` | `res/xml/` | HTTPS 강제 설정 |
| `google-services.json` | `app/` | Firebase 설정 (gitignore) |
| `local.properties` | 루트 | 외부 API Key 등 로컬 비밀값 (gitignore) |

---

## .gitignore 항목 (앱 관련)

```
# 민감 파일
app/google-services.json
local.properties
*.jks
*.keystore

# 빌드 산출물
app/build/
.gradle/
build/
```

---

## 네비게이션 흐름

```
SplashFragment
  ├── (온보딩 미완료) → OnboardingFragment → LoginFragment
  ├── (온보딩 완료 + 미로그인) → LoginFragment
  └── (온보딩 완료 + 로그인) → HomeFragment

LoginFragment → HomeFragment (로그인 성공)

HomeFragment (BottomNavigation 또는 Top-level)
  ├── 퀴즈 시작 → DifficultySelectFragment
  │                 → QuizPlayFragment → QuizResultFragment
  │                                        ├── 다시 풀기 → DifficultySelectFragment
  │                                        └── 홈으로 → HomeFragment (popBackStack)
  ├── 통계 탭 → StatisticsFragment
  └── 내 정보 탭 → SettingsFragment
                      └── 로그아웃 → LoginFragment (popBackStack all)
```
