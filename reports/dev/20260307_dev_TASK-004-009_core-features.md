## 개발 로그: TASK-004 ~ TASK-007 (Core Features)

- **세션**: session-1-dev
- **일시**: 2026-03-07 12:35
- **대상**: session-2-review, session-3-test

### 변경 파일
- `app/src/main/java/.../domain/usecase/quiz/*`: GetQuestionsUseCase, MixQuestionsUseCase, GetStreakUseCase
- `app/src/main/java/.../data/...`: QuestionRepositoryImpl, QuestionEntity, QuestionDao, QuestionApiService, 등
- `app/src/main/java/.../presentation/home/*`: HomeFragment, HomeViewModel
- `app/src/main/java/.../presentation/quiz/*`: DifficultySelectFragment, QuizPlayFragment, QuizPlayViewModel, QuizResultFragment
- `app/src/main/res/layout/*`: fragment_home.xml, fragment_difficulty_select.xml, fragment_quiz_play.xml, fragment_quiz_result.xml
- `app/src/main/java/.../core/di/*`: RepositoryModule, UseCaseModule, ViewModelModule

### 구현 요약
- **TASK-004**: 문제 데이터를 Room LocalDB와 Retrofit Remote API로 관리하는 Repository 패턴 구현
- **TASK-005**: 홈 화면 대시보드 (풀은 문제 수, 정답률, 연속 스트릭 표시 + 뷰모델 플로우 리액티브 UI 연결)
- **TASK-006 & TASK-007**: 난이도 선택 → 퀴즈 풀이(4지, 5지선다 동적 구성) → 퀴즈 완료 결과 표시의 전체 Flow 기본 뼈대 작성 완료

### 자기 검증 결과
- Koin Module 구성 시 `get()` 의존성이 빠짐없이 연결되었음을 확인
- `./gradlew assembleDebug` 빌드 시 컴파일 에러가 발생하지 않음을 확인.
- 다만, TASK-003(Google 로그인)이 스킵되어 있는 상태이므로, 온보딩 후 HomeFragment로 라우팅되도록 임시 수정이 선행되었습니다.

### 리뷰 요청 사항 (session-2-review)
- `HomeViewModel`과 `QuizPlayViewModel`의 Flow State 관리 패턴이 `architecture.md` 규격에 부합하는지 코드리뷰를 부탁드립니다.
- `QuestionRepositoryImpl`의 네트워크 대응 로직 (오프라인 시 Room Fallback)이 요구사항 목적에 맞게 작성되었는지 설계 리뷰 바랍니다.

### 테스트 가이드 (session-3-test)
- 아직 Firebase 백엔드나 실제 문제 API 서버가 연동되지 않아, 에뮬레이터에서 뷰 흐름(Home → 퀴즈 선택 → 퀴즈 풀이 → 결과) 렌더링 정상 여부만 테스트할 수 있습니다.
- UI 터치 동작과 네비게이션이 `nav_graph.xml` 의도대로 화면 전환되는지 확인해주세요.
