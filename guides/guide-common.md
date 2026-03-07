# Claude Agent 멀티세션 프로젝트 — Android(AOS) 공통 가이드 (v3.0)

> **대상 프로젝트**: Android 모바일 앱 (GitLab 관리, 독립 저장소)
> 이 파일은 모든 세션이 공유하는 원칙·구조·규칙을 정의한다.
> 각 세션은 `guide-session-X.md`에 이 공통 가이드가 내장되어 있으므로, 별도로 이 파일을 읽을 필요 없이 자기 세션 가이드만 읽으면 된다.

---

## 핵심 원칙

0. **불명확하면 반드시 되물어라**: 작업 지시가 모호하거나 범위·우선순위·기대 결과가 명확하지 않으면, 추측하여 진행하지 말고 사용자에게 구체적으로 질문하라.
1. **파일 기반 소통**: 세션 간 정보 전달은 반드시 파일로 한다. 다른 세션의 컨텍스트를 가정하지 않는다.
2. **자기 검증**: 모든 세션은 작업 완료 후 결과를 스스로 점검한다.
3. **누적 학습**: 실수와 주의사항을 메모장에 기록하여 프로젝트 전체의 품질을 점진적으로 높인다.
4. **과감한 롤백**: 수정을 3회 반복해도 해결되지 않으면, git reset 후 범위를 좁혀 재시도한다.
5. **완료 즉시 알림**: 세션 작업이 완료되면 Telegram으로 사용자에게 즉시 알린다.
6. **설계 원본 단일화**: `tasks/architecture.md`가 아키텍처의 유일한 원본(Source of Truth)이다.

---

## 세션별 권장 모델

| 세션 | 역할 | 모델 | 이유 |
|------|------|------|------|
| 세션 0 | 설계 & 태스크 분해 | `claude-opus-4-5-20251101` | 고난도 설계 추론 |
| 세션 1 | Android 개발 | `claude-opus-4-5-20251101` | 핵심 코딩 |
| 세션 2 | 코드 리뷰 & 정적 분석 | `claude-opus-4-5-20251101` | 보안·성능 판단 |
| 세션 3 | 테스트 | `claude-sonnet-4-5-20250929` | AC 기반 구조적 작업 |
| 세션 4 | 문서화 | `claude-sonnet-4-5-20250929` | 텍스트 정리 중심 |
| 세션 6 | 성능 & 모니터링 | `claude-opus-4-5-20251101` | 병목 분석·최적화 추론 |
| 세션 7 | 배포 & CI/CD | `claude-sonnet-4-5-20250929` | 정형화된 설정 위주 |

---

## 프로젝트 디렉토리 구조

```
project-root/                  # Android GitLab 저장소 루트
├── tasks/                     # 설계 산출물
│   ├── requirements.md
│   ├── architecture.md        # ⭐ 아키텍처 유일한 원본 (Source of Truth)
│   ├── task-breakdown.md
│   ├── directory-structure.md
│   ├── gitlab-ci-design.md
│   ├── research.md
│   ├── plan.md
│   └── archive/               # 완료된 태스크의 설계 스냅샷
│       └── YYYYMMDD_TASK-XXX_기능명/
│           ├── YYYYMMDD_req_TASK-XXX_기능명.md
│           ├── YYYYMMDD_arch_TASK-XXX_기능명.md
│           ├── YYYYMMDD_task_TASK-XXX_기능명.md
│           ├── YYYYMMDD_dir_TASK-XXX_기능명.md
│           └── YYYYMMDD_ci_TASK-XXX_기능명.md
├── docs/                      # 문서 산출물
│   ├── README.md
│   ├── CHANGELOG.md
│   ├── architecture.md        # 아키텍처 발행본 (tasks/architecture.md 기반)
│   ├── api.md
│   ├── setup-guide.md
│   ├── build-guide.md
│   └── release-guide.md
├── reports/                   # 세션 간 소통 리포트
│   ├── dev/
│   ├── review/
│   ├── test/
│   ├── performance/
│   └── deploy/
├── memos/
│   └── claude-mistakes.md
├── skills/                    # Claude Agent 재사용 스킬
│   ├── kotlin-conventions.md
│   ├── test-strategy.md
│   ├── review-checklist.md
│   └── gitlab-ci-template.md
├── scripts/
│   └── notify-telegram.sh
├── deploy/                    # 배포 관련
│   ├── .env.example
│   ├── deploy-checklist.md
│   └── rollback-guide.md
│
├── app/                       # Android 앱 소스
│   └── src/
│       ├── main/
│       ├── test/              # 단위 테스트
│       └── androidTest/       # 통합·UI 테스트
├── fastlane/                  # Fastlane 배포 자동화
├── build.gradle
├── gradlew
├── .gitlab-ci.yml
├── .session-status.json
└── CLAUDE.md
```

---

## 파일 명명 규칙

형식: `기능명_타입_티켓ID_YYYYMM.md` (dev·review) / `기능명_타입_티켓ID_YYYYMMDD.md` (그 외)

| 타입 | 설명 | 저장 위치 |
|------|------|-----------|
| `dev` | 개발 로그 | `reports/dev/` |
| `review` | 코드 리뷰 결과 | `reports/review/` |
| `test` | 테스트 결과 | `reports/test/` |
| `perf` | 성능 분석 결과 | `reports/performance/` |
| `deploy` | 배포 체크리스트 | `reports/deploy/` |
| `req` | 요구사항 스냅샷 | `tasks/archive/기능명_TASK-XXX_YYYYMMDD/` |
| `arch` | 아키텍처 스냅샷 | `tasks/archive/기능명_TASK-XXX_YYYYMMDD/` |
| `task` | 태스크 분해 스냅샷 | `tasks/archive/기능명_TASK-XXX_YYYYMMDD/` |
| `dir` | 디렉토리 구조 스냅샷 | `tasks/archive/기능명_TASK-XXX_YYYYMMDD/` |
| `ci` | CI/CD 설계 스냅샷 | `tasks/archive/기능명_TASK-XXX_YYYYMMDD/` |

**예시**
- `webview-login_dev_TASK-001_202603.md`
- `webview-login_review_TASK-001_202603.md`
- `webview-login_test_TASK-001_20260303.md`
- `webview-login_perf_TASK-001_20260303.md`
- `initial-release_deploy_v1.0.0_20260303.md`
- `initial-setup_req_TASK-001_20260303.md`
- `initial-setup_arch_TASK-001_20260303.md`
- `initial-setup_task_TASK-001_20260303.md`
- `initial-setup_dir_TASK-001_20260303.md`
- `initial-setup_ci_TASK-001_20260303.md`

---

## 설계 산출물 아카이브

**목적**: `tasks/`의 설계 파일(5개)은 기능 추가 시 덮어쓰이므로, 배포 완료 시점의 설계 상태를 스냅샷으로 보존한다.

**담당**: 세션 7 (배포 완료 직후)

**폴더 명명**: `tasks/archive/YYYYMMDD_TASK-XXX_기능명/`
- 예: `tasks/archive/20260303_TASK-001_initial-setup/`
- 예: `tasks/archive/20260304_TASK-002_ai-camera/`

**아카이브 대상 (5개 파일)**

```
tasks/archive/기능명_TASK-XXX_YYYYMMDD/
├── 기능명_req_TASK-XXX_YYYYMMDD.md
├── 기능명_arch_TASK-XXX_YYYYMMDD.md
├── 기능명_task_TASK-XXX_YYYYMMDD.md
├── 기능명_dir_TASK-XXX_YYYYMMDD.md
└── 기능명_ci_TASK-XXX_YYYYMMDD.md
```

> **복사 완료 후**: `tasks/`의 원본 5개 파일 삭제 (research.md·plan.md는 유지). 다음 기능은 세션 0이 새로 생성.

---

## 공통 규칙

```
[공통 규칙]
1. 작업 시작 전 .session-status.json을 읽어 다른 세션의 상태를 확인하라.
2. 작업 시작 전 memos/claude-mistakes.md를 읽어 과거 실수를 반복하지 마라.
3. 다른 세션에 전달할 내용은 반드시 파일(reports/ 등)로 남겨라.
4. 파일 기반으로만 소통하라. 다른 세션의 컨텍스트를 절대 가정하지 마라.
5. 산출물 파일 상단에 메타데이터(작성 세션, 일시, 태스크 ID, 대상 세션)를 포함하라.
6. 작업 완료 후 자기 검증을 수행하라. (실행 결과, 에러, 기대 동작 충족 여부)
7. 실수나 주의사항 발견 시 memos/claude-mistakes.md에 기록하라.
8. 작업 완료 후 .session-status.json의 자기 세션 상태를 업데이트하라.
9. 작업 완료 후 Telegram으로 사용자에게 완료 알림을 보내라.
    형식: "[세션명] ✅ TASK-XXX 완료 — {한 줄 요약}"
10. 아키텍처 참조 시 반드시 tasks/architecture.md(원본)를 사용하라.
11. 작업 파일 수정 후 반드시 의미 있는 커밋 메시지를 작성하라.
    예: git commit -m "TASK-XXX: 로그인 플로우 구현"
    커밋 시점은 아래 [세션별 커밋 시점] 섹션을 참조하라.
```

---

## GitLab 워크플로우

### 브랜치 전략

```
main (보호 브랜치)
├── develop            ← 통합 브랜치
│   ├── feature/TASK-XXX-feature-name   ← 기능 브랜치
│   └── bugfix/TASK-XXX-bug-name        ← 버그 수정 브랜치
├── release/vX.X.X     ← 릴리즈 브랜치
└── hotfix/...
```

### Merge Request 규칙

- MR 제목 형식: `TASK-XXX: {작업 설명}`
- MR 생성 시 세션 2(리뷰)를 리뷰어로 지정
- CI 파이프라인 통과 필수 (lint + test + build)

### 세션별 커밋 시점

| 세션 | 커밋 시점 | 커밋 대상 파일 | 브랜치 |
|------|----------|-------------|--------|
| 세션 0 | 설계 산출물 완료 후 | `tasks/*.md` | `develop` 직접 또는 별도 설계 브랜치 |
| 세션 1 | ① 계획 확정 시<br>② 기능 단위 구현 + 검증 완료 시<br>③ 리뷰 피드백 반영 완료 시 | `tasks/plan.md`<br>`app/src/`, `reports/dev/`<br>`app/src/` | `feature/TASK-XXX-{name}` |
| 세션 2 | 리뷰 결과 파일 작성 후 | `reports/review/기능명_review_TASK-XXX_YYYYMM.md` | `develop` |
| 세션 3 | 테스트 코드 작성 + 실행 완료 후 | `app/src/test/`, `reports/test/` | `develop` |
| 세션 4 | 문서 작성 완료 후 | `docs/*.md` | `develop` |
| 세션 6 | 성능 분석 결과 작성 후 | `reports/performance/` | `develop` |
| 세션 7 | 배포 체크리스트 완료 후 | `reports/deploy/`, `.gitlab-ci.yml`, `tasks/archive/` | `release/vX.X.X` |

> **세션 1만 `feature/` 브랜치를 사용한다.** 나머지 세션은 산출물 파일(리포트·문서)만 커밋하므로 `develop`에 직접 커밋 또는 PR 없이 push한다.

---


### 칸반 컬럼 흐름

```
📥 Todo → 🔨 In Progress → 👀 In Review → 🧪 Testing → ⚡ Performance → 📝 Documenting → 🚀 Deploying → ✅ Done
```

실패·반려 시 `🔨 In Progress`로 복귀.

### 세션별 업데이트 책임

| 세션 | 시점 | 상태 변경 |
|------|------|----------|
| 세션 0 | TASK 분해 완료 | 카드 생성 → `📥 Todo` |
| 세션 1 | 개발 착수 / 완료 | `📥→🔨` / `🔨→👀` 리뷰 요청 |
| 세션 2 | APPROVED | `👀→🧪` / REJECTED → `🔨` 복귀 + 이슈 등록 |
| 세션 3 | ALL PASS | `🧪→⚡` / FAIL → `🔨` 복귀 + 이슈 등록 |
| 세션 6 | GOOD | `⚡→📝` / CRITICAL → `🔨` 복귀 + 이슈 등록 |
| 세션 4 | 문서화 완료 | `📝→🚀` |
| 세션 7 | 배포 완료 | `🚀→✅` |
| 세션 5 | 매일 | 일일 리포트, 이슈 종합·요약, 대시보드 |

### TASK 카드 속성

이름, 상태, 우선순위(P0/P1/P2), 담당 세션, 의존성, 예상 소요, 리뷰 판정, 테스트 판정, 성능 판정, 마지막 업데이트, 관련 파일


```
프로젝트 홈
├── 📋 태스크 보드 (칸반)    ← 각 세션이 즉시 업데이트
├── 📊 일일 진행 리포트       ← 세션 5 담당
├── 🐛 이슈 트래커            ← 발견 세션이 즉시 등록, 세션 5가 종합·요약
├── 📖 기술 문서
│   └── Android 아키텍처
├── 📝 회의록 & 의사결정      ← 세션 0·5 담당
└── 📈 대시보드 (요약 통계)   ← 세션 5 담당
```

---

## 실패 대응 전략 (Rollback Protocol)

```
1. 문제 감지 → 즉시 중단
2. 실패 원인을 memos/claude-mistakes.md에 기록
3. git stash 또는 git reset --hard
4. [알림] Telegram: "⚠️ TASK-XXX 롤백 — 사유: {한 줄 요약}"
5. tasks/plan.md 범위 축소 재작성
```

| 상황 | 행동 |
|------|------|
| 수정 3회 이상 반복 | git reset 후 재설계 |
| 계획과 전혀 다른 방향 | git reset 후 plan 재작성 |
| 파일 변경이 예상의 2배 이상 | 중단, 태스크 재분해 |
| 다른 세션 산출물과 충돌 | 중단, .session-status.json 재확인 |

---

## 알림 시스템 (Telegram)

각 세션이 작업 완료 시 Telegram 스크립트를 직접 호출한다.

```bash
bash scripts/notify-telegram.sh "[세션명] ✅ TASK-XXX 완료 — {한 줄 요약}"
```

---

## 세션 간 워크플로우

```
세션 0 (설계) [Opus]
    │
    └──→ 세션 1 (개발) [Opus]  ⇄  세션 2 (리뷰) [Opus]
                                        │
                                        ▼
                              세션 3 (테스트) [Sonnet]
                                        │
                                        ▼
                              세션 6 (성능) [Opus]
                                        │
                                        ▼
                              세션 4 (문서화) [Sonnet]
                                        │
                                        ▼
                              세션 7 (배포) [Sonnet]

```

실행 순서: 0 → 1 → 2 → 3 → 6 → 4 → 7 / 세션 5만 병렬

---

## SuperClaude 활용 가이드

SuperClaude는 Claude Code에 특화된 커맨드와 페르소나를 추가하는 오픈소스 프레임워크다.
각 세션 가이드에 `[SuperClaude 활용]` 섹션으로 권장 커맨드가 명시되어 있다.

**주요 커맨드 요약**

| 세션 | 커맨드 | 효과 |
|------|--------|------|
| 0 (설계) | `/sc:design --persona-architect` | 아키텍처 설계 최적화 |
| 1 (개발) | `/sc:implement TASK-XXX --validate --safe-mode` | 안전한 구현 |
| 2 (리뷰) | `/sc:analyze --focus security --think-hard` | 심층 보안 분석 |
| 3 (테스트) | `/sc:test --coverage --persona-qa` | 커버리지 포함 테스트 |
| 4 (문서) | `/sc:document --persona-scribe` | 고품질 문서 생성 |
| 6 (성능) | `/sc:analyze --focus performance --ultrathink` | 초심층 성능 분석 |
| 7 (배포) | `/sc:build --dry-run` + `/sc:git` | 안전한 배포 |

**공통 플래그**
- `--uc`: 토큰 압축 (~70% 절약) — 모든 세션에서 사용 권장
- `/sc:index --scope project`: 세션 시작 시 프로젝트 전체 구조 파악

---

## iOS 프로젝트와의 협업

본 프로젝트는 Android 독립 저장소로 운영한다. iOS와 협업이 필요한 경우:

- **API 계약**: 크로스 플랫폼 공유 문서(별도 관리)의 API 명세를 참조한다.
- **기능 동등성**: 크로스 플랫폼 Feature Parity 문서를 참조하여 진행 상황을 동기화한다.
- **배포 조율**: 동시 릴리즈가 필요한 경우 크로스 플랫폼 릴리즈 일정을 참조한다.
