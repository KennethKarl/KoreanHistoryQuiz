# Dev 리포트 — TASK-001: 프로젝트 기반 설정

> 세션: session-1-dev
> 날짜: 2026-03-07
> 브랜치: feature/TASK-001-project-base-setup
> 커밋: cc24c58

---

## 완료 항목

### 빌드 시스템 (6개)
- `settings.gradle.kts` — 앱 이름, 플러그인 저장소
- `build.gradle.kts` (root) — AGP 8.3.2, Kotlin 2.0.0 플러그인 선언
- `gradle/libs.versions.toml` — 전체 의존성 버전 카탈로그
- `gradle/wrapper/gradle-wrapper.properties` — Gradle 8.7
- `gradlew` — Unix 래퍼 스크립트
- `app/build.gradle.kts` — dev/staging/prod 플레이버, 의존성 32개

### 앱 설정 (3개)
- `app/src/main/AndroidManifest.xml` — 권한(INTERNET, ACCESS_NETWORK_STATE), HistoryQuizApplication, MainActivity
- `app/proguard-rules.pro` — Retrofit, Koin, Room, Firebase, Gson, Coroutines, DataStore, Coil 보존
- `app/google-services.json` — 플레이스홀더 (실제 Firebase 연결 시 교체)

### 리소스 (8개)
- `themes.xml` / `themes.xml(night)` — Material Design 3, Splash Theme
- `colors.xml` — Brown-toned 팔레트, 퀴즈 전용 색상(정답/오답/스트릭)
- `strings.xml` — 전체 UI 문자열 (한국어)
- `dimens.xml` — 초등학생 대상 큰 텍스트 (body=18sp, title=22sp, headline=28sp)
- `network_security_config.xml` — HTTPS only
- `nav_graph.xml` — startDestination=splashFragment 플레이스홀더
- `activity_main.xml` — FragmentContainerView + NavHostFragment

### Kotlin 소스 (15개)
| 파일 | 역할 |
|------|------|
| `HistoryQuizApplication.kt` | Koin startKoin (5개 모듈) |
| `MainActivity.kt` | SplashScreen + ViewBinding |
| `core/di/NetworkModule.kt` | OkHttp + Retrofit + AuthInterceptor |
| `core/di/DatabaseModule.kt` | AppDatabase + UserPreferencesDataStore |
| `core/di/RepositoryModule.kt` | placeholder |
| `core/di/UseCaseModule.kt` | placeholder |
| `core/di/ViewModelModule.kt` | placeholder |
| `core/error/AppError.kt` | data class (code, errorType, message, cause) |
| `core/error/ErrorCode.kt` | architecture.md §10 전체 상수 |
| `core/network/AuthInterceptor.kt` | Firebase ID Token 헤더 주입 |
| `core/network/NetworkStatusChecker.kt` | ConnectivityManager 기반 |
| `core/util/DateUtils.kt` | timestamp ↔ LocalDate, calculateStreak |
| `core/util/FlowUtils.kt` | collectWhenStarted (repeatOnLifecycle) |
| `data/local/db/AppDatabase.kt` | Room @Database(entities=[]) |
| `data/datastore/UserPreferencesDataStore.kt` | 4-key DataStore 래퍼 |

---

## 자기 검증 결과

| 항목 | 결과 | 비고 |
|------|------|------|
| 패키지 경로 일관성 | ✅ | 전체 `com.historyquiz.app` |
| ErrorCode ↔ architecture §10 | ✅ | 11개 상수 1:1 대응 |
| DataStore 키 4개 ↔ architecture §7 | ✅ | is_onboarding_done, last_signed_in_email, notification_enabled, weekly_goal |
| 빌드 플레이버 ↔ architecture §9 | ✅ | dev/staging/prod 각 BASE_URL 설정 |
| Koin 5개 모듈 등록 | ✅ | HistoryQuizApplication.kt |
| ProGuard 필수 라이브러리 보존 | ✅ | 6개 카테고리 |

---

## 알려진 제약사항

| 항목 | 내용 |
|------|------|
| gradle-wrapper.jar | 미포함 → Android Studio 첫 임포트 시 자동 다운로드 |
| google-services.json | 플레이스홀더 → TASK-003 시 실제 Firebase 파일 교체 |
| AppDatabase entities=[] | Room 어노테이션 경고 가능 → TASK-004/007에서 엔티티 추가 |
| AuthInterceptor runBlocking | IO 스레드 블로킹 가능 → TASK-003에서 개선 검토 |

---

## 다음 TASK

**TASK-002**: Splash + Onboarding + Navigation 골격 구현
- SplashFragment: DataStore 확인 → 라우팅
- OnboardingFragment + OnboardingPagerAdapter
- nav_graph.xml 완성

