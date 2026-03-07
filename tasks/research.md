# 리서치 노트 — TASK-001: 프로젝트 기반 설정

> 작성 세션: session-1-dev
> 작성일: 2026-03-07
> 작성자: Claude Agent

---

## 1. 조사 내용

### 1-1. 빌드 환경

| 항목 | 결정 | 근거 |
|------|------|------|
| Gradle | 8.7 (gradle-wrapper.properties) | AGP 8.3.2 요구사항 충족 |
| AGP | 8.3.2 | Kotlin 2.0 + KSP 호환 안정 버전 |
| Kotlin | 2.0.0 | K2 컴파일러 안정화, compose 없으므로 적합 |
| KSP | 2.0.0-1.0.21 | Kotlin 2.0.0과 1:1 매핑 버전 |
| JVM Target | 17 | AGP 8.x 권장값 |

### 1-2. DI (Koin)

- Koin 3.5.6: `koin-android` + `koin-android-viewmodel` 분리 아티팩트 사용
- `startKoin {}` 블록에 `androidLogger`, `androidContext`, `modules` 설정
- 5개 모듈: network, database, repository, usecase, viewmodel

### 1-3. 네트워크

- `AuthInterceptor`: `runBlocking + Tasks.await` 방식으로 Firebase ID Token 동기 취득
  - 단점: 네트워크 스레드 차단 가능 → 토큰 캐싱 고려 (TASK-003에서 개선 여지)
- `HttpLoggingInterceptor`: DEBUG 빌드에서만 BODY 레벨 활성화 (민감 정보 노출 방지)

### 1-4. Room

- `entities = []` (TASK-001): 엔티티 클래스가 아직 없어 빈 배열로 선언
- `fallbackToDestructiveMigration()`: 개발 초기 스키마 변경 잦음, P0 출시 전 마이그레이션 전략 재검토 필요
- `exportSchema = true` + `ksp room.schemaLocation` → 스키마 파일 버전 관리 가능

### 1-5. DataStore

- `preferencesDataStore` 위임 프로퍼티 방식 (권장)
- 4개 키: `is_onboarding_done`, `last_signed_in_email`, `notification_enabled`, `weekly_goal`
- `clearAll()` 메서드 제공 → 로그아웃 시 호출 (TASK-003에서 연결)

### 1-6. 빌드 플레이버

- `dev`/`staging`/`prod` 3단계, 각각 `BASE_URL` + `ENV` BuildConfig 필드
- `dev` / `staging`은 `applicationId` 접미사 추가 → 동일 기기 3개 앱 동시 설치 가능

---

## 2. 결정 사항

1. `google-services.json` 플레이스홀더 커밋 — 실제 Firebase 프로젝트 생성 후 교체 필요
2. `gradle-wrapper.jar` 미포함 — Android Studio 첫 임포트 시 자동 다운로드
3. `AppDatabase.kt` entities=[] 컴파일 경고 가능 — Room 어노테이션 프로세서가 경고 발생할 수 있으나 빌드 통과
4. `StringUtils.kt`는 현재 불필요하여 생성하지 않음 — 필요 시 TASK에서 추가

---

## 3. 기술 부채 / 주의사항

| 항목 | 내용 | 대상 TASK |
|------|------|-----------|
| AuthInterceptor runBlocking | IO 스레드 차단 가능 | TASK-003 |
| AppDatabase 마이그레이션 | fallbackToDestructive 사용 중 | 출시 전 |
| google-services.json | 플레이스홀더, 실제 Firebase 연결 필요 | TASK-003 |
| 외부 문제 API URL | 플레이스홀더 (dev/staging/prod) | TASK-004 |
