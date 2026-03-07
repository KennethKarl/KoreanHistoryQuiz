# 구현 계획 — TASK-001: 프로젝트 기반 설정

> 작성 세션: session-1-dev
> 작성일: 2026-03-07
> 상태: ✅ 완료

---

## 계획 요약

`tasks/architecture.md` 설계 기반으로 컴파일 가능한 Android 프로젝트 골격을 생성한다.
실제 기능 구현은 TASK-002~009에서 수행하며, 본 태스크는 기반 인프라에 집중한다.

---

## 생성 파일 목록 (총 32개)

### 빌드 설정 ✅

| 파일 | 상태 |
|------|------|
| `settings.gradle.kts` | ✅ |
| `build.gradle.kts` (root) | ✅ |
| `gradle/libs.versions.toml` | ✅ |
| `gradle/wrapper/gradle-wrapper.properties` | ✅ |
| `gradlew` | ✅ |
| `app/build.gradle.kts` | ✅ |

### 앱 설정 ✅

| 파일 | 상태 |
|------|------|
| `app/src/main/AndroidManifest.xml` | ✅ |
| `app/proguard-rules.pro` | ✅ |
| `app/google-services.json` (플레이스홀더) | ✅ |

### 리소스 ✅

| 파일 | 상태 |
|------|------|
| `res/values/themes.xml` | ✅ |
| `res/values-night/themes.xml` | ✅ |
| `res/values/colors.xml` | ✅ |
| `res/values/strings.xml` | ✅ |
| `res/values/dimens.xml` | ✅ |
| `res/xml/network_security_config.xml` | ✅ |
| `res/navigation/nav_graph.xml` | ✅ |
| `res/layout/activity_main.xml` | ✅ |

### Kotlin 소스 ✅

| 파일 | 상태 |
|------|------|
| `HistoryQuizApplication.kt` | ✅ |
| `MainActivity.kt` | ✅ |
| `core/di/NetworkModule.kt` | ✅ |
| `core/di/DatabaseModule.kt` | ✅ |
| `core/di/RepositoryModule.kt` | ✅ (placeholder) |
| `core/di/UseCaseModule.kt` | ✅ (placeholder) |
| `core/di/ViewModelModule.kt` | ✅ (placeholder) |
| `core/error/AppError.kt` | ✅ |
| `core/error/ErrorCode.kt` | ✅ |
| `core/network/AuthInterceptor.kt` | ✅ |
| `core/network/NetworkStatusChecker.kt` | ✅ |
| `core/util/DateUtils.kt` | ✅ |
| `core/util/FlowUtils.kt` | ✅ |
| `data/local/db/AppDatabase.kt` | ✅ (entities=[]) |
| `data/datastore/UserPreferencesDataStore.kt` | ✅ |

---

## 자기 검증 체크리스트

- [x] 패키지 경로 `com.historyquiz.app` 일관성
- [x] ErrorCode.kt 상수 ↔ architecture.md §10 대응
- [x] DataStore 키 4개 ↔ architecture.md §7 대응
- [x] 빌드 플레이버 3개 (dev/staging/prod) ↔ architecture.md §9 대응
- [x] Koin 모듈 5개 모두 HistoryQuizApplication.kt에 등록
- [x] proguard-rules.pro: Retrofit, Koin, Room, Firebase, Gson 보존 규칙 포함

---

## 다음 단계

| TASK | 내용 |
|------|------|
| TASK-002 | Splash + Onboarding + Navigation 골격 |
| TASK-003 | Google 로그인 + Firebase Auth |
| TASK-004 | 문제 데이터 (Room + Retrofit) |
