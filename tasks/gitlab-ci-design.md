# GitLab CI/CD 설계 — historyQuiz

> 작성 세션: session-0-design
> 작성일: 2026-03-07
> 대상 세션: session-7-deploy

---

## 1. 파이프라인 개요

```
MR(feature → develop)     develop 브랜치     release 브랜치     main 브랜치
┌────────────────┐         ┌──────────┐       ┌──────────────┐   ┌──────────┐
│  lint          │         │  lint    │       │  lint        │   │          │
│  test          │─ Merge─▶│  test    │─ MR ─▶│  test        │─▶ │  (tag)   │
│  build:debug   │         │  build   │       │  build:rel   │   │  deploy  │
└────────────────┘         └──────────┘       │  deploy:stag │   │  :prod   │
                                              └──────────────┘   └──────────┘
```

---

## 2. 스테이지 정의

```yaml
stages:
  - lint       # 정적 분석 (ktlint, detekt)
  - test       # 단위 테스트 + 커버리지
  - build      # APK/AAB 빌드
  - deploy     # 배포 (Firebase App Distribution, Play Store)
```

---

## 3. 트리거 규칙

| 브랜치/이벤트 | lint | test | build | deploy |
|--------------|------|------|-------|--------|
| MR (→ develop) | ✅ | ✅ | debug only | ❌ |
| develop push | ✅ | ✅ | debug | ❌ |
| release/* push | ✅ | ✅ | release | staging (수동) |
| main push (tag) | ✅ | ✅ | release | prod (수동) |

---

## 4. 전체 `.gitlab-ci.yml` 설계

```yaml
# .gitlab-ci.yml — historyQuiz Android CI/CD
image: reactnativecommunity/react-native-android:latest
# 또는 커스텀 Docker 이미지: gradle:8-jdk17

variables:
  GRADLE_OPTS: "-Dorg.gradle.daemon=false -Dorg.gradle.parallel=false"
  ANDROID_SDK_ROOT: "/opt/android-sdk"

# Gradle 캐시로 빌드 시간 단축
cache:
  key: "$CI_COMMIT_REF_SLUG"
  paths:
    - .gradle/
    - app/.gradle/
    - ~/.gradle/

# ─────────────────────────────────────────
# STAGE 1: lint
# ─────────────────────────────────────────
ktlint:
  stage: lint
  script:
    - ./gradlew ktlintCheck
  artifacts:
    when: on_failure
    paths:
      - app/build/reports/ktlint/
    expire_in: 1 week
  rules:
    - if: '$CI_PIPELINE_SOURCE == "merge_request_event"'
    - if: '$CI_COMMIT_BRANCH =~ /^(develop|release\/.*)$/'

detekt:
  stage: lint
  script:
    - ./gradlew detekt
  artifacts:
    when: on_failure
    reports:
      codequality: app/build/reports/detekt/detekt.json
    paths:
      - app/build/reports/detekt/
    expire_in: 1 week
  rules:
    - if: '$CI_PIPELINE_SOURCE == "merge_request_event"'
    - if: '$CI_COMMIT_BRANCH =~ /^(develop|release\/.*)$/'

# ─────────────────────────────────────────
# STAGE 2: test
# ─────────────────────────────────────────
unit-test:
  stage: test
  script:
    - ./gradlew testDevDebugUnitTest --stacktrace
    - ./gradlew jacocoTestReport
  coverage: '/Total.*?([0-9]{1,3})%/'
  artifacts:
    when: always
    reports:
      junit: app/build/test-results/**/*.xml
    paths:
      - app/build/reports/tests/
      - app/build/reports/jacoco/
    expire_in: 1 week
  rules:
    - if: '$CI_PIPELINE_SOURCE == "merge_request_event"'
    - if: '$CI_COMMIT_BRANCH =~ /^(develop|release\/.*)$/'
    - if: '$CI_COMMIT_TAG'

# ─────────────────────────────────────────
# STAGE 3: build
# ─────────────────────────────────────────
build-debug:
  stage: build
  script:
    - ./gradlew assembleDevDebug --stacktrace
  artifacts:
    paths:
      - app/build/outputs/apk/dev/debug/*.apk
    expire_in: 3 days
  rules:
    - if: '$CI_PIPELINE_SOURCE == "merge_request_event"'
    - if: '$CI_COMMIT_BRANCH == "develop"'

build-release:
  stage: build
  script:
    # 서명 키스토어를 CI 환경 변수에서 복원
    - echo "$RELEASE_KEYSTORE_BASE64" | base64 -d > release.jks
    - ./gradlew assembleProdRelease
        -Pandroid.injected.signing.store.file=release.jks
        -Pandroid.injected.signing.store.password=$KEYSTORE_PASSWORD
        -Pandroid.injected.signing.key.alias=$KEY_ALIAS
        -Pandroid.injected.signing.key.password=$KEY_PASSWORD
    - ./gradlew bundleProdRelease  # AAB for Play Store
  artifacts:
    paths:
      - app/build/outputs/apk/prod/release/*.apk
      - app/build/outputs/bundle/prodRelease/*.aab
    expire_in: 30 days
  rules:
    - if: '$CI_COMMIT_BRANCH =~ /^release\/.*$/'
    - if: '$CI_COMMIT_TAG'

# ─────────────────────────────────────────
# STAGE 4: deploy
# ─────────────────────────────────────────
deploy-staging:
  stage: deploy
  needs: ["build-release"]
  script:
    # Firebase App Distribution (staging)
    - npm install -g firebase-tools
    - firebase appdistribution:distribute
        app/build/outputs/apk/staging/release/*.apk
        --app $FIREBASE_STAGING_APP_ID
        --token $FIREBASE_TOKEN
        --groups "internal-testers"
        --release-notes "Branch: $CI_COMMIT_BRANCH | Commit: $CI_COMMIT_SHORT_SHA"
  environment:
    name: staging
  rules:
    - if: '$CI_COMMIT_BRANCH =~ /^release\/.*$/'
      when: manual
  allow_failure: false

deploy-production:
  stage: deploy
  needs: ["build-release"]
  script:
    # Google Play Internal Track (AAB)
    - pip install google-play-upload
    - python scripts/play_store_upload.py
        --aab app/build/outputs/bundle/prodRelease/*.aab
        --track internal
        --service-account-json "$PLAY_STORE_SERVICE_ACCOUNT_JSON"
  environment:
    name: production
  rules:
    - if: '$CI_COMMIT_TAG =~ /^v[0-9]+\.[0-9]+\.[0-9]+$/'
      when: manual
  allow_failure: false
```

---

## 5. CI 환경 변수 목록 (GitLab Settings → CI/CD → Variables)

| 변수 이름 | 설명 | Masked | Protected |
|----------|------|--------|-----------|
| `RELEASE_KEYSTORE_BASE64` | 릴리즈 키스토어 파일 (Base64 인코딩) | ✅ | ✅ |
| `KEYSTORE_PASSWORD` | 키스토어 비밀번호 | ✅ | ✅ |
| `KEY_ALIAS` | 키 별칭 | ✅ | ✅ |
| `KEY_PASSWORD` | 키 비밀번호 | ✅ | ✅ |
| `FIREBASE_TOKEN` | Firebase CLI 인증 토큰 | ✅ | ✅ |
| `FIREBASE_STAGING_APP_ID` | Firebase Staging App ID | ✅ | ✅ |
| `PLAY_STORE_SERVICE_ACCOUNT_JSON` | Play Store 서비스 계정 JSON | ✅ | ✅ |
| `GOOGLE_SERVICES_JSON_DEV` | google-services.json (dev) | ✅ | ❌ |
| `GOOGLE_SERVICES_JSON_PROD` | google-services.json (prod) | ✅ | ✅ |

> ⚠️ 키스토어/서비스 계정 파일은 절대 저장소에 커밋하지 않는다.

---

## 6. google-services.json CI 처리

```yaml
# build-release 스테이지 before_script에 추가
before_script:
  - echo "$GOOGLE_SERVICES_JSON_PROD" > app/src/prod/google-services.json
  - echo "$GOOGLE_SERVICES_JSON_DEV"  > app/src/dev/google-services.json
```

---

## 7. Merge Request 요구사항

MR을 `develop`으로 병합하려면 다음 조건을 모두 충족해야 한다:

- [ ] `ktlint` 통과
- [ ] `detekt` 통과
- [ ] `unit-test` 통과 (커버리지 ≥ 60%)
- [ ] `build-debug` 성공
- [ ] 세션 2(코드 리뷰) 승인

---

## 8. 배포 파이프라인 흐름

```
개발자 → feature/TASK-XXX 브랜치 push
  → MR 생성 (→ develop)
  → CI: lint + test + build:debug
  → 세션 2 코드 리뷰
  → develop 병합

QA 준비 →  release/vX.X.X 브랜치 생성
  → CI: lint + test + build:release
  → deploy:staging (수동 트리거)
  → 세션 3 테스트 → 세션 6 성능 → 세션 4 문서화

출시 →  main 병합 + vX.X.X 태그
  → CI: lint + test + build:release
  → deploy:production (수동 트리거)
```

---

## 9. 빌드 타임 최적화

- Gradle 캐시: `.gradle/` 공유 캐시로 반복 빌드 시간 단축
- 병렬 실행: `lint` 스테이지의 `ktlint`와 `detekt`는 동시 실행
- `--build-cache` 플래그 활용
- Docker 이미지: JDK 17 + Android SDK 포함 커스텀 이미지 (최초 빌드 후 재사용)

---

## 10. 알림 설정

```yaml
# 파이프라인 실패 시 Telegram 알림 (선택)
notify-failure:
  stage: .post
  script:
    - bash scripts/notify-telegram.sh "⚠️ CI 실패 — $CI_COMMIT_BRANCH / $CI_JOB_NAME"
  rules:
    - when: on_failure
```
