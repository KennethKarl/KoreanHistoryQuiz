# 태스크 분해 — historyQuiz

> 작성 세션: session-0-design
> 작성일: 2026-03-07
> 대상 세션: session-1-dev (개발), session-3-test (테스트)

---

## 태스크 의존성 맵

```
TASK-001 (기반 설정)
  ├── TASK-002 (스플래시 & 온보딩)
  ├── TASK-003 (Google 로그인) ──→ TASK-005 (홈)
  └── TASK-004 (문제 데이터) ─────→ TASK-005 (홈)
                               ├──→ TASK-006 (퀴즈 플레이) ──→ TASK-007 (결과)
                               └──→ TASK-008 (내 정보)

TASK-007 (결과) ──→ TASK-009 (통계) [P1]
```

---

## TASK-001: 프로젝트 기반 설정

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | 없음 |
| 예상 소요 | 1일 |
| 담당 | session-1-dev |

**설명**: 개발에 앞서 프로젝트 전체에서 공유하는 기반 코드를 설정한다.

**수용 기준 (AC)**:
- [ ] Gradle KTS 설정 + `libs.versions.toml` (version catalog) 작성
- [ ] 빌드 플레이버 3종 설정: `dev` / `staging` / `prod` (applicationId suffix, BaseUrl)
- [ ] Koin DI 모듈 구성: `networkModule`, `databaseModule`, `repositoryModule`, `useCaseModule`, `viewModelModule`
- [ ] Retrofit2 + OkHttp3 네트워크 모듈 설정 (AuthInterceptor, HttpLoggingInterceptor)
- [ ] Room AppDatabase 초기화 (Migration 전략 포함)
- [ ] DataStore 초기화 (UserPreferencesDataStore)
- [ ] Firebase 프로젝트 연동 (google-services.json dev/prod 분리)
- [ ] Firebase Analytics + Crashlytics 초기화
- [ ] Material Design 3 테마 설정 (`Theme.HistoryQuiz`)
- [ ] `ErrorCode.kt` 상수 파일 생성 (architecture.md §10 기준)
- [ ] `AppError.kt` data class 정의
- [ ] AndroidX Navigation Component 설정 (`nav_graph.xml`)
- [ ] `NetworkSecurityConfig.xml` (HTTPS only)
- [ ] ProGuard rules 기본 설정 (release)

---

## TASK-002: 스플래시 & 온보딩

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-001 |
| 예상 소요 | 0.5일 |
| 담당 | session-1-dev |

**설명**: 앱 첫 실행 시 보여주는 스플래시와 최초 1회 온보딩을 구현한다.

**수용 기준 (AC)**:
- [ ] SplashFragment: 앱 로고 표시, cold start ≤ 2초 이내 다음 화면 전환
- [ ] SplashFragment: 온보딩 완료 여부(`is_onboarding_done`) DataStore 확인 → 분기 (온보딩 or 로그인)
- [ ] SplashFragment: 로그인 상태 확인 → 이미 로그인 시 HomeFragment로 직행
- [ ] OnboardingFragment: ViewPager2 기반 3장 슬라이드
- [ ] 온보딩 각 페이지: 앱 소개 이미지 + 타이틀 + 설명 텍스트
- [ ] "시작하기" 버튼 → OnboardingFragment 종료 후 `is_onboarding_done = true` 저장 → LoginFragment 이동
- [ ] 건너뛰기(Skip) 버튼 지원

---

## TASK-003: Google 로그인 & Firebase Auth

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-001 |
| 예상 소요 | 1일 |
| 담당 | session-1-dev |

**설명**: Firebase Authentication을 이용한 Google 소셜 로그인을 구현한다.

**수용 기준 (AC)**:
- [ ] `AuthRepository` 인터페이스 구현 (`AuthRepositoryImpl`)
- [ ] `SignInWithGoogleUseCase`: Google ID Token → Firebase Auth signInWithCredential
- [ ] `SignOutUseCase`: Firebase Auth signOut + DataStore 초기화
- [ ] `GetCurrentUserUseCase`: 현재 로그인 사용자 반환
- [ ] `IsLoggedInUseCase`: 로그인 상태 Boolean 반환
- [ ] `AuthViewModel`: `signIn()`, `signOut()`, `AuthUiState` (StateFlow)
- [ ] `LoginFragment`: "Google로 로그인" 버튼 UI (Material Design 3)
- [ ] 로그인 성공 → HomeFragment 이동
- [ ] 로그인 실패 → `aut_0005_and` 에러 스낵바 표시
- [ ] 토큰 갱신 실패 처리 → `aut_0004_and` → 재로그인 유도
- [ ] Firebase Auth 상태 리스너: 토큰 만료 시 자동 갱신

---

## TASK-004: 문제 데이터 관리

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-001 |
| 예상 소요 | 2일 |
| 담당 | session-1-dev |

**설명**: 외부 공개 API에서 한국사 문제를 가져와 Room에 캐싱하는 데이터 레이어를 구현한다.

**수용 기준 (AC)**:
- [ ] `QuestionEntity` Room Entity 정의 (schema §6-1 기준)
- [ ] `QuestionDao` CRUD 구현 (insert, queryByLevel, getAll, deleteOld)
- [ ] `QuestionDto` → `Question` 도메인 모델 매핑
- [ ] `QuestionApiService` Retrofit 인터페이스 정의
- [ ] `QuestionRemoteDataSource`: API 호출 + `net_*` 에러 처리
- [ ] `QuestionLocalDataSource`: Room 조회/삽입
- [ ] `QuestionRepositoryImpl`:
  - 네트워크 가능 시: API → Room 캐시 → 반환
  - 네트워크 불가 시: Room 캐시 → 반환 (없으면 `quz_0002_and`)
- [ ] `GetQuestionsUseCase`: 난이도 + 개수 파라미터로 문제 목록 반환
- [ ] `MixQuestionsUseCase`: 로컬 7 : 신규 3 혼합, 네트워크 없으면 로컬 100%
- [ ] 기본(4지선다) / 심화(4지/5지선다 혼합) 필터링
- [ ] 캐시 만료 정책: 30일 이상 된 문제 자동 삭제 (`WorkManager` 또는 앱 시작 시)

---

## TASK-005: 홈 화면

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-003, TASK-004 |
| 예상 소요 | 1일 |
| 담당 | session-1-dev |

**설명**: 학습 현황을 보여주는 홈 대시보드를 구현한다.

**수용 기준 (AC)**:
- [ ] `HomeViewModel`: `HomeUiState` (오늘 풀은 문제 수, 정답률, 스트릭)
- [ ] `GetStreakUseCase`: quiz_results 테이블에서 연속 학습일 계산
- [ ] 홈 UI: 오늘의 학습 요약 카드 (문제 수 / 정답률)
- [ ] 홈 UI: 연속 학습 스트릭 표시 (일수 + 불꽃 아이콘)
- [ ] 홈 UI: 기본 퀴즈 시작 버튼
- [ ] 홈 UI: 심화 퀴즈 시작 버튼
- [ ] 홈 UI: 최근 결과 미니 카드 (마지막 퀴즈 점수)
- [ ] 버튼 탭 → DifficultySelectFragment (또는 직접 QuizPlayFragment) 이동

---

## TASK-006: 퀴즈 플레이

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-004 |
| 예상 소요 | 2일 |
| 담당 | session-1-dev |

**설명**: 실제 퀴즈를 풀 수 있는 핵심 기능을 구현한다.

**수용 기준 (AC)**:
- [ ] `DifficultySelectFragment`: 기본 / 심화 카드 UI, 난이도 선택 → QuizPlayFragment 이동
- [ ] `QuizPlayViewModel`: `QuizUiState` StateFlow 기반 상태 관리
  - `Loading` → `Ready` → `Answered` → `Finished`
- [ ] `QuizPlayFragment`:
  - [ ] 문제 본문 텍스트 표시
  - [ ] 4지선다 레이아웃 (기본)
  - [ ] 5지선다 레이아웃 (심화 일부 문제)
  - [ ] 선택지 선택 시 즉시 정답(초록) / 오답(빨강) 피드백
  - [ ] 진행률 표시: ProgressBar + "n/10" 텍스트
  - [ ] 다음 문제 자동 전환 (1.5초 딜레이)
- [ ] 문제 랜덤 셔플 (`MixQuestionsUseCase` 결과 shuffle)
- [ ] 퀴즈 10문제 완료 → QuizResultFragment 이동
- [ ] 문제 없음 오류 → `quz_0001_and` → 에러 화면 + 재시도 버튼

---

## TASK-007: 퀴즈 결과

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-006 |
| 예상 소요 | 1일 |
| 담당 | session-1-dev |

**설명**: 퀴즈 완료 후 결과를 표시하고 DB에 저장한다.

**수용 기준 (AC)**:
- [ ] `QuizResultEntity` + `WrongAnswerEntity` Room Entity 정의 (schema §6-2, §6-3)
- [ ] `QuizResultDao` 구현 (insert, queryAll, queryById, queryByDateRange)
- [ ] `QuizResultRepositoryImpl` 구현
- [ ] `SubmitQuizUseCase`: 결과 객체 생성 + Room 저장
- [ ] `QuizResultFragment`:
  - [ ] 점수 / 정답률 (n/10, n%) 표시
  - [ ] 애니메이션 (숫자 카운트업)
  - [ ] 틀린 문제 목록 (문제 본문 + 정답 표시)
  - [ ] "다시 풀기" 버튼 → 동일 난이도 새 퀴즈 시작
  - [ ] "홈으로" 버튼 → HomeFragment 이동 (back stack 클리어)
- [ ] 결과 저장 실패 시 `db_0003_and` 로그 기록 (UI 오류 미표시, silent fail)

---

## TASK-008: 내 정보 화면

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-003 |
| 예상 소요 | 0.5일 |
| 담당 | session-1-dev |

**설명**: 프로필 확인 및 설정 화면을 구현한다.

**수용 기준 (AC)**:
- [ ] `SettingsViewModel`: `SettingsUiState` (유저 프로필, 알림 설정)
- [ ] `SettingsFragment`:
  - [ ] Google 계정 프로필 사진 (Coil 로드)
  - [ ] 이름, 이메일 표시
  - [ ] 알림 설정 토글 (DataStore `notification_enabled`)
  - [ ] "로그아웃" 버튼 → 확인 다이얼로그 → `SignOutUseCase` 호출 → LoginFragment 이동

---

## TASK-009: 통계 화면 [P1]

| 항목 | 내용 |
|------|------|
| 우선순위 | P1 |
| 의존성 | TASK-007 |
| 예상 소요 | 1.5일 |
| 담당 | session-1-dev |

**설명**: 날짜별 학습 기록과 정답률 통계를 시각화한다.

**수용 기준 (AC)**:
- [ ] `GetQuizHistoryUseCase`: 날짜 범위 기반 결과 목록 조회
- [ ] `GetStatisticsUseCase`: 정답률 집계, 레벨별 분리
- [ ] `StatisticsViewModel`: `StatisticsUiState` StateFlow
- [ ] `StatisticsFragment`:
  - [ ] 날짜별 학습 기록 RecyclerView 목록
  - [ ] 주간/월간 정답률 막대 차트 (MPAndroidChart)
  - [ ] 기본 / 심화 레벨별 탭 분리
  - [ ] 빈 상태 UI (아직 기록 없음 안내)

---

## 전체 일정 (추정)

| TASK | 내용 | 소요(일) | 누적 |
|------|------|----------|------|
| TASK-001 | 기반 설정 | 1.0 | 1.0 |
| TASK-002 | 스플래시&온보딩 | 0.5 | 1.5 |
| TASK-003 | Google 로그인 | 1.0 | 2.5 |
| TASK-004 | 문제 데이터 | 2.0 | 4.5 |
| TASK-005 | 홈 화면 | 1.0 | 5.5 |
| TASK-006 | 퀴즈 플레이 | 2.0 | 7.5 |
| TASK-007 | 퀴즈 결과 | 1.0 | 8.5 |
| TASK-008 | 내 정보 | 0.5 | 9.0 |
| **P0 합계** | | **9.0일** | |
| TASK-009 | 통계 화면 | 1.5 | 10.5 |
| **전체 합계** | | **10.5일** | |

> 리뷰(세션2), 테스트(세션3), 성능(세션6), 문서화(세션4), 배포(세션7) 소요 별도.

---

## [신규] QUIZ DB 구축 태스크 (TASK-010~015)

> 추가 세션: session-0-design (보완)
> 추가일: 2026-03-08

### 태스크 의존성

```
TASK-010 (PDF 파이프라인)
  ├── TASK-011 (시대 구분 고도화) [P1]
  ├── TASK-012 (이미지 크롭) [P0]
  └── → TASK-013 (DB 생성·검증) [P0]
              └── → TASK-014 (Android Room 통합) [P0]
                          └── → TASK-015 (JSON 시드 제거·E2E 테스트) [P1]
```

---

## TASK-010: PDF 파이프라인 스크립트 구축

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | 없음 (독립 Python 스크립트) |
| 예상 소요 | 2.0일 |
| 담당 | session-1-dev |
| 입력 | `C:\Users\pc\Downloads\QUIZ\` (69개 PDF) |
| 출력 | 문제 텍스트·선택지·정답 dict |

**설명**: 문제지 PDF와 정답표 PDF를 파싱하여 구조화된 데이터를 추출한다.

**수용 기준 (AC)**:
- [ ] `scripts/build_quiz_db.py` 작성
- [ ] 정답표 PDF(`*_정답표.pdf`)에서 문제번호→정답 매핑 추출
- [ ] 문제지 PDF(`*_문제지.pdf`)에서 문제 텍스트·선택지 추출
- [ ] `quiz/parse_real_pdfs.py`의 `ERA_KEYWORDS`, `determine_era()` 재사용
- [ ] 57회~77회 기본+심화 전체 파싱 성공 (오류율 ≤ 1%)
- [ ] 파싱 결과를 중간 JSON으로 덤프 (`scripts/output/parsed_questions.json`)

---

## TASK-011: 시대 구분 분류 고도화 [P1]

| 항목 | 내용 |
|------|------|
| 우선순위 | P1 |
| 의존성 | TASK-010 |
| 예상 소요 | 0.5일 |
| 담당 | session-1-dev |

**설명**: 자동 분류 정확도를 높이고 미분류 문제를 수동으로 태깅한다.

**수용 기준 (AC)**:
- [ ] `ERA_KEYWORDS` 키워드 보완 (미분류 비율 ≤ 10% 목표)
- [ ] `scripts/output/unclassified.json` 에 미분류 문제 목록 출력
- [ ] 수동 태깅용 CSV(`scripts/output/manual_era_tags.csv`) 지원

---

## TASK-012: 문제 이미지 일괄 크롭

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-010 |
| 예상 소요 | 1.0일 |
| 담당 | session-1-dev |
| 출력 | `app/src/main/assets/images/*.png` |

**설명**: 문제지 PDF 각 페이지에서 개별 문제 영역을 PNG로 크롭한다.

**수용 기준 (AC)**:
- [ ] `scripts/crop_all.py` 작성 (`quiz/crop_questions.py` 래핑)
- [ ] 2열 레이아웃 자동 감지 및 크롭
- [ ] 파일명 규칙: `{회차}_{level}_{문제번호}.png` (예: `57_basic_1.png`)
- [ ] 저장 위치: `app/src/main/assets/images/`
- [ ] 이미지당 크기 ≤ 200KB

---

## TASK-013: SQLite DB 생성 및 검증

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-010, TASK-012 |
| 예상 소요 | 0.5일 |
| 담당 | session-1-dev |
| 출력 | `app/src/main/assets/quiz.db` |

**설명**: 파싱 데이터와 이미지 경로를 취합하여 SQLite DB를 생성하고 검증한다.

**수용 기준 (AC)**:
- [ ] `build_quiz_db.py`가 `quiz.db` 생성
- [ ] DB 스키마: `questions` 테이블 (architecture.md §12-2 참조)
- [ ] 레코드 수 검증: 회차별 50문제 × 기본+심화 총 건수 확인
- [ ] 정답 샘플 검증: 임의 10문제 정답 정확성 수동 확인
- [ ] DB 파일 크기 ≤ 50MB

---

## TASK-014: Android Room 통합

| 항목 | 내용 |
|------|------|
| 우선순위 | P0 |
| 의존성 | TASK-013 |
| 예상 소요 | 1.0일 |
| 담당 | session-1-dev |
| 수정 파일 | `AppDatabase.kt`, `DatabaseModule.kt` |

**설명**: 앱이 `quiz.db`를 assets에서 복사하여 Room DB로 사용하도록 변경한다.

**수용 기준 (AC)**:
- [ ] `AppDatabase.kt`에 `.createFromAsset("quiz.db")` 적용
- [ ] DB 버전 +1, `fallbackToDestructiveMigration()` 설정 (개발 단계)
- [ ] 기존 `QuestionEntity` 스키마와 `quiz.db` 스키마 호환 확인
- [ ] 앱 콜드 스타트 시 DB 복사 시간 ≤ 3초
- [ ] `QuizPlayViewModel.loadQuestions()` 정상 동작 확인

---

## TASK-015: 기존 JSON 시드 제거 및 E2E 테스트 [P1]

| 항목 | 내용 |
|------|------|
| 우선순위 | P1 |
| 의존성 | TASK-014 |
| 예상 소요 | 0.5일 |
| 담당 | session-1-dev |

**설명**: JSON 기반 시드 코드를 제거하고 전체 퀴즈 플로우를 검증한다.

**수용 기준 (AC)**:
- [ ] `SeedDataHelper` 비활성화 (또는 삭제)
- [ ] `korean_history_answers.json` 역할 제거 (이미 quiz.db로 대체)
- [ ] 홈 → 퀴즈 플레이(기본) → 퀴즈 결과 E2E 동작 확인
- [ ] 홈 → 퀴즈 플레이(심화) → 퀴즈 결과 E2E 동작 확인
- [ ] 오프라인 상태에서 문제 로딩 정상 동작 확인

---

## 신규 태스크 일정 추가

| TASK | 내용 | 소요(일) | 우선순위 |
|------|------|----------|----------|
| TASK-010 | PDF 파이프라인 | 2.0 | P0 |
| TASK-012 | 이미지 크롭 | 1.0 | P0 |
| TASK-013 | DB 생성·검증 | 0.5 | P0 |
| TASK-014 | Android Room 통합 | 1.0 | P0 |
| **P0 추가 합계** | | **4.5일** | |
| TASK-011 | 시대 구분 고도화 | 0.5 | P1 |
| TASK-015 | JSON 시드 제거·E2E | 0.5 | P1 |
| **P1 추가 합계** | | **1.0일** | |
