# Dev 리포트 — TASK-002: Splash & Onboarding

> 세션: session-1-dev
> 날짜: 2026-03-07
> 브랜치: feature/TASK-002-splash-onboarding-login
> 커밋: 5a73d85

---

## 완료 항목

### Layout (5개)
| 파일 | 내용 |
|------|------|
| `fragment_splash.xml` | 로고 + 앱이름 페이드인, ConstraintLayout 중앙 배치 |
| `fragment_onboarding.xml` | ViewPager2 + TabLayout(도트) + Skip/다음/시작하기 버튼 |
| `item_onboarding_page.xml` | 이미지 + 타이틀 + 설명 레이아웃 |
| `fragment_login.xml` | Google 로그인 버튼 + 로딩 인디케이터 스텁 |
| `fragment_home.xml` | 스텁 (TASK-005에서 실제 구현) |

### Drawable (8개)
| 파일 | 내용 |
|------|------|
| `ic_logo.xml` | 책 모양 벡터 아이콘 |
| `ic_streak.xml` | 불꽃 아이콘 (스트릭) |
| `ic_onboarding_quiz.xml` | 퀴즈 (물음표 + 책) |
| `ic_onboarding_study.xml` | 학습 (달력 + 체크) |
| `ic_onboarding_stats.xml` | 통계 (막대 차트) |
| `selector_onboarding_dot.xml` | 도트 인디케이터 selector |
| `shape_dot_selected.xml` | 선택된 도트 (colorPrimary) |
| `shape_dot_unselected.xml` | 비선택 도트 (tertiary_container) |

### Navigation (1개 업데이트)
- `nav_graph.xml`: 전체 8개 Fragment destinations + actions 완성
  - Splash(3 actions) → Onboarding(1) → Login(1) → Home(3)
  - DifficultySelect → QuizPlay → QuizResult(2)
  - Statistics, Settings(1)

### Kotlin (5개)
| 파일 | 역할 |
|------|------|
| `SplashFragment.kt` | 1.5초 페이드인 → DataStore+Firebase 라우팅 |
| `OnboardingFragment.kt` | ViewPager2 관리, Skip/Next/Start 로직, DataStore 저장 |
| `OnboardingPagerAdapter.kt` | `OnboardingPage` 데이터 클래스 + RecyclerView.Adapter |
| `LoginFragment.kt` | 스텁 (TASK-003에서 AuthViewModel 연결) |
| `HomeFragment.kt` | 스텁 (TASK-005에서 HomeViewModel 연결) |

---

## 자기 검증 결과

| 항목 | 결과 | 비고 |
|------|------|------|
| SplashFragment 라우팅 3분기 | ✅ | onboarding미완/미로그인/로그인 |
| DataStore `is_onboarding_done` 저장 | ✅ | `setOnboardingDone(true)` |
| ViewPager2 + TabLayoutMediator | ✅ | 3페이지 도트 인디케이터 |
| 마지막 페이지에서 btnAction → 시작하기 | ✅ | `updateActionButton()` |
| nav_graph 전체 화면 destinations | ✅ | 8개 fragment, 11개 action |
| back stack 클리어 (로그인 후 뒤로가기 방지) | ✅ | `popUpTo nav_graph inclusive=true` |
| shape drawable에 theme attr 제거 | ✅ | `@color/md_theme_*` 로 교체 |

---

## AC 달성 현황

| AC 항목 | 상태 |
|---------|------|
| SplashFragment 로고 표시, ≤2초 전환 | ✅ (1.5초) |
| DataStore `is_onboarding_done` 확인 → 분기 | ✅ |
| 로그인 상태 확인 → HomeFragment 직행 | ✅ |
| OnboardingFragment ViewPager2 3장 | ✅ |
| 페이지별 이미지+타이틀+설명 | ✅ |
| 시작하기 버튼 → `is_onboarding_done=true` → LoginFragment | ✅ |
| 건너뛰기(Skip) 버튼 | ✅ |

---

## 다음 TASK

**TASK-003**: Google 로그인 + Firebase Auth
- `AuthRepositoryImpl`, `SignInWithGoogleUseCase`, `AuthViewModel`
- `LoginFragment`에 Google Sign-In 로직 연결
- 성공 시 `action_login_to_home` 네비게이션
- 실패 시 `aut_0005_and` 에러 스낵바
