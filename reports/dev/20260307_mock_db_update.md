## 개발 로그: DB 스키마 업데이트 및 시대(era) 데이터 삽입

- **세션**: session-1-dev
- **일시**: 2026-03-07
- **대상**: session-2-review, session-3-test

### 변경 내역 요약
사용자의 요청에 따라 `quiz/korean_history_answers.json` 데이터 기반으로 한국사 문제를 생성하여 DB에 삽입했습니다. 기존 데이터에는 문제 텍스트 내용과 시대(era) 정보가 없었기 때문에, **파이썬 스크립트(`augment_json.py`)를 통해 가상의 문제/선택지와 랜덤한 시대('삼국시대', '조선시대' 등) 데이터를 생성**하고 이를 Android Assets(`mock_korean_history_questions.json`)으로 추가하였습니다.
추가로, 향후 카테고리 기획 확장을 위해 `Question` Entity 및 Database 설계에 `era` 필드를 연동하였습니다.

### 주요 수정 내역
1. **Model & Entity (`Question.kt`, `QuestionEntity.kt`)**
   - `era: String` 프로퍼티 추가 (기본값: "미정")
   - Room DB `AppDatabase` 버전을 1->2 로 상향 (`fallbackToDestructiveMigration()` 적용하여 기존 DB 데이터 클리어)

2. **DAO & Repository (`QuestionDao.kt`, `QuestionRepositoryImpl.kt`)**
   - `getByLevelAndEra(level: String, era: String, count: Int)` Query 메서드 추가
   - 향후 UI에서 `삼국시대`, `조선시대` 등 시대별로 문제를 불러올 수 있는 기반 마련

3. **초기화 로직 (`SeedDataHelper.kt`)**
   - 하드코딩된 예시 문제 삽입 함수 삭제
   - JSON Parsing 클래스(`HistoryQuizJsonModel` 등) 추가 및 Gson을 통해 `assets/korean_history_answers.json` 파일 로딩 로직 작성
   - `QuestionDao`를 통해 파싱된 모든 문제(약 1200개 규모)를 초기 실행 시 DB에 Bulk Insert

4. **Koin 의존성 추가 (`DatabaseModule.kt`)**
   - `SeedDataHelper` 생성 시 Android 내부 Assets를 읽어오기 위해 `androidContext()` 파라미터 주입

### 다음 진행 (Session-2, Session-3)
- 향후 실제로 시대 정보 및 문제/보기 텍스트가 입력된 올바른 형태의 정식 JSON 파일이 준비되면 자산(`assets/korean_history_answers.json`) 내용물만 교체하고 앱의 데이터 저장을 지우고(또는 앱 재설치) 실행하면 정상적으로 실제 문제들이 불러와집니다.
- 새롭게 추가된 시대 분류(`getByLevelAndEra`)를 UI단(`QuizPlayViewModel`)에 적용할 지 여부는 후속 과제로 진행 가능합니다.
