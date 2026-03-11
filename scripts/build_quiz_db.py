"""
TASK-010 & TASK-013: QUIZ DB 빌드 파이프라인
==============================================
한국사능력검정시험 PDF(문제지 + 정답표)를 파싱하여 SQLite DB(quiz.db)를 생성한다.

사용법:
    python scripts/build_quiz_db.py [--quiz-dir PATH] [--output PATH]

기본값:
    --quiz-dir  C:\\Users\\pc\\Downloads\\QUIZ
    --output    app/src/main/assets/quiz.db

의존 라이브러리:
    pip install PyMuPDF pdfplumber

스키마 (v2, TASK-014 기준):
    Android Room QuestionEntity와 완전 호환.
    - answer_index : 0-based 정수 (PDF 1-based값에서 -1)
    - category     : "{round_num}회" (예: "69회")
    - cached_at    : 생성 시각 Unix timestamp
    - source       : "local" 고정
"""

import argparse
import glob
import json
import os
import re
import sqlite3
import sys
import time

import fitz  # PyMuPDF

# ── 설정 ──────────────────────────────────────────────────────────────────────

QUIZ_DIR_DEFAULT = r"C:\Users\pc\Downloads\QUIZ"
OUTPUT_DB_DEFAULT = os.path.join(
    os.path.dirname(__file__), "..",
    "app", "src", "main", "assets", "quiz.db"
)
ANSWERS_JSON_FALLBACK = os.path.join(
    os.path.dirname(__file__), "..",
    "app", "src", "main", "assets", "korean_history_answers.json"
)
OUTPUT_JSON_DEBUG = os.path.join(
    os.path.dirname(__file__), "output", "parsed_questions.json"
)

# ── 시대 구분 키워드 (quiz/parse_real_pdfs.py 재사용) ──────────────────────────

ERA_KEYWORDS = {
    "선사/고조선": ["구석기", "신석기", "청동기", "단군", "팔조법", "고조선", "위만", "홍익인간", "빗살무늬", "고인돌"],
    "삼국": ["고구려", "백제", "신라", "가야", "광개토", "장수왕", "근초고왕", "진흥왕", "무열왕",
             "살수대첩", "안시성", "삼국 통일", "을지문덕", "계백", "관창"],
    "통일신라/발해": ["통일신라", "발해", "문무왕", "신문왕", "장보고", "대조영", "선덕여왕", "성덕대왕",
                    "불국사", "석굴암", "해동성국", "원효", "의상", "독서삼품과"],
    "고려": ["고려", "왕건", "광종", "성종", "무신", "몽골", "공민왕", "거란", "여진",
             "팔만대장경", "벽란도", "최충헌", "삼별초", "과거제", "최승로"],
    "조선": ["조선", "이성계", "태종", "세종", "세조", "성종", "연산군", "중종", "명종",
             "선조", "광해군", "인조", "효종", "현종", "숙종", "영조", "정조",
             "임진왜란", "병자호란", "실학", "세도정치", "흥선대원군", "훈민정음", "집현전"],
    "근대": ["강화도 조약", "개항", "갑신정변", "동학", "갑오개혁", "독립협회", "대한제국",
             "을사늑약", "의병", "개화기", "위정척사", "홍범도", "신민회", "헤이그"],
    "일제강점기": ["일제", "조선총독부", "3.1운동", "임시정부", "독립군", "광복군",
                  "무단통치", "문화통치", "민족말살", "김구", "안중근", "윤봉길", "신채호",
                  "형평사", "소작쟁의", "민립대학"],
    "현대": ["광복", "대한민국", "이승만", "박정희", "전두환", "노태우", "김영삼", "김대중",
             "노무현", "6.25", "민주화운동", "IMF", "남북정상회담", "제헌헌법", "6월항쟁"],
}


def determine_era(text: str) -> str:
    """키워드 스코어링으로 시대 구분 반환. 최고점 0이면 '미정'."""
    text_nospace = text.replace(" ", "")
    scores = {era: 0 for era in ERA_KEYWORDS}
    for era, keywords in ERA_KEYWORDS.items():
        for kw in keywords:
            scores[era] += text_nospace.count(kw.replace(" ", ""))
    best = max(scores, key=scores.get)
    return best if scores[best] > 0 else "미정"


# ── 정답표 파싱 ────────────────────────────────────────────────────────────────

def parse_answer_pdf(pdf_path: str) -> dict[int, int]:
    """
    정답표 PDF에서 {문제번호: 정답} dict 반환.
    Format 1: q_no\\n[한글]\\nanswer  (일반 형식)
    Format 2: q_no\\nanswer\\npoints  (모두 숫자, 블록 15개)
    """
    doc = fitz.open(pdf_path)
    page = doc[0]
    blocks = page.get_text("blocks")
    answers: dict[int, int] = {}

    # Format 1
    for b in blocks:
        text = b[4]
        pairs = re.findall(r'(\d+)\n[^\d\n]+\n(\d+)', text)
        for q_str, a_str in pairs:
            q_no, ans = int(q_str), int(a_str)
            if 1 <= q_no <= 50 and 1 <= ans <= 5:
                answers[q_no] = ans

    if len(answers) == 50:
        return answers

    # Format 2 (각 블록이 정확히 15개 숫자)
    answers2: dict[int, int] = {}
    for b in blocks:
        nums = re.findall(r'\d+', b[4])
        if len(nums) == 15:
            for i in range(0, 15, 3):
                q_no, ans = int(nums[i]), int(nums[i + 1])
                if 1 <= q_no <= 50 and 1 <= ans <= 5:
                    answers2[q_no] = ans

    return answers2 if len(answers2) >= len(answers) else answers


def load_fallback_answers(json_path: str) -> dict[tuple, dict[int, int]]:
    """
    korean_history_answers.json에서 {(session, level): {q_no: answer}} 반환.
    HWP 파일(57, 58회) 정답 fallback용.
    """
    fallback: dict[tuple, dict[int, int]] = {}
    if not os.path.exists(json_path):
        return fallback
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)
    for exam in data.get("exams", []):
        session = exam["session"]
        level = exam["level"]
        answers = {a["questionNo"]: a["answer"] for a in exam["answers"]}
        fallback[(session, level)] = answers
    return fallback


# ── 문제지 파싱 ────────────────────────────────────────────────────────────────

def extract_text_from_pdf(pdf_path: str) -> str:
    """PDF 전체 텍스트 추출 (블록 순서 정렬)."""
    doc = fitz.open(pdf_path)
    full_text = ""
    for page in doc:
        blocks = page.get_text("blocks")
        blocks.sort(key=lambda b: (b[1], b[0]))
        for b in blocks:
            full_text += b[4] + "\n"
    return full_text


def parse_questions_from_text(text: str, num_choices: int = 5) -> dict[int, dict]:
    """
    문제지 텍스트에서 {q_no: {content, options}} 파싱.
    선택지 기호: ①②③④⑤
    """
    questions: dict[int, dict] = {}
    pattern = r"(\d+)\.\s*(.*?)(?=\n\d+\.\s|$)"
    matches = list(re.finditer(pattern, text, re.DOTALL))

    for match in matches:
        q_no = int(match.group(1))
        if not (1 <= q_no <= 50):
            continue

        content_raw = match.group(2).strip()
        opt_pattern = r"([①②③④⑤])(.*?)(?=[①②③④⑤]|$)"
        opt_matches = list(re.finditer(opt_pattern, content_raw, re.DOTALL))

        if opt_matches:
            options = [om.group(2).strip().replace("\n", " ") for om in opt_matches]
            content = content_raw[: opt_matches[0].start()].strip()
            content = re.sub(r'\n+', '\n', content)
        else:
            content = content_raw
            options = [f"(파싱 실패 - 보기 {i + 1})" for i in range(num_choices)]

        # 선택지 수 맞춤
        if len(options) > num_choices:
            options = options[:num_choices]
        while len(options) < num_choices:
            options.append(f"(파싱 실패 - 보기 {len(options) + 1})")

        questions[q_no] = {"content": content, "options": options}

    return questions


# ── DB 스키마 생성 ─────────────────────────────────────────────────────────────

SCHEMA_SQL = """
CREATE TABLE IF NOT EXISTS questions (
    id           TEXT NOT NULL PRIMARY KEY,
    content      TEXT NOT NULL,
    options      TEXT NOT NULL,
    answer_index INTEGER NOT NULL,
    level        TEXT NOT NULL,
    category     TEXT NOT NULL,
    era          TEXT NOT NULL,
    cached_at    INTEGER NOT NULL,
    source       TEXT NOT NULL
);
"""
# Room QuestionEntity 호환 규칙:
#  - @ColumnInfo(defaultValue) 미사용 → SQL DEFAULT 절 사용 금지 (defaultValue='undefined' 기대)
#  - @Index 미사용 → CREATE INDEX 금지 (indices=[] 기대)
#  - TEXT PRIMARY KEY는 자동 NOT NULL이 아님 → 명시적 NOT NULL 필요


# ── 메인 파이프라인 ────────────────────────────────────────────────────────────

def build(quiz_dir: str, output_db: str, verbose: bool = True):
    def log(msg):
        if verbose:
            print(msg)

    os.makedirs(os.path.dirname(os.path.abspath(output_db)), exist_ok=True)

    # ─ 1. 정답표 수집 ───────────────────────────────────────────────────────────
    log("\n[Step 1] 정답표 PDF 파싱 중...")
    answer_map: dict[tuple, dict[int, int]] = {}

    for pdf_path in sorted(glob.glob(os.path.join(quiz_dir, "*_정답표.pdf"))):
        fname = os.path.basename(pdf_path)
        # 63_심화_정답표_2.pdf 같은 중복 파일 건너뜀
        if re.search(r'_\d+\.pdf$', fname):
            log(f"  [SKIP] {fname} (중복 파일)")
            continue

        m = re.match(r'^(\d+)_(기본|심화)_정답표\.pdf$', fname)
        if not m:
            continue
        round_num, level_kr = int(m.group(1)), m.group(2)
        level = "basic" if level_kr == "기본" else "advanced"

        try:
            answers = parse_answer_pdf(pdf_path)
            cnt = len(answers)
            status = "OK" if cnt == 50 else f"WARN {cnt}/50"
            log(f"  [{status}]  {fname}")
            answer_map[(round_num, level)] = answers
        except Exception as e:
            log(f"  [ERR]  {fname}: {e}")

    # ─ 1b. HWP 정답 fallback (57, 58회) ────────────────────────────────────────
    log("\n[Step 1b] JSON fallback 정답 로드 (57, 58회 HWP)...")
    fallback = load_fallback_answers(ANSWERS_JSON_FALLBACK)
    for key, answers in fallback.items():
        if key not in answer_map:
            log(f"  [OK]  {key[0]}회 {key[1]} (JSON fallback)")
            answer_map[key] = answers

    # ─ 2. 문제지 파싱 ────────────────────────────────────────────────────────────
    log("\n[Step 2] 문제지 PDF 파싱 중...")
    question_map: dict[tuple, dict[int, dict]] = {}

    for pdf_path in sorted(glob.glob(os.path.join(quiz_dir, "*_문제지.pdf"))):
        fname = os.path.basename(pdf_path)
        m = re.match(r'^(\d+)_(기본|심화)_문제지\.pdf$', fname)
        if not m:
            continue
        round_num, level_kr = int(m.group(1)), m.group(2)
        level = "basic" if level_kr == "기본" else "advanced"
        num_choices = 4 if level == "basic" else 5

        try:
            text = extract_text_from_pdf(pdf_path)
            questions = parse_questions_from_text(text, num_choices)
            cnt = len(questions)
            status = "OK" if cnt == 50 else f"WARN {cnt}/50"
            log(f"  [{status}]  {fname}")
            question_map[(round_num, level)] = questions
        except Exception as e:
            log(f"  [ERR]  {fname}: {e}")

    # ─ 3. SQLite DB 생성 ─────────────────────────────────────────────────────────
    log(f"\n[Step 3] DB 생성: {output_db}")
    if os.path.exists(output_db):
        os.remove(output_db)

    conn = sqlite3.connect(output_db)
    conn.executescript(SCHEMA_SQL)

    cached_at = int(time.time())
    inserted = 0
    errors = []
    debug_rows = []

    all_keys = sorted(set(answer_map.keys()) | set(question_map.keys()))
    for round_num, level in all_keys:
        level_kr = "기본" if level == "basic" else "심화"
        num_choices = 4 if level == "basic" else 5
        answers = answer_map.get((round_num, level), {})
        questions = question_map.get((round_num, level), {})
        category = f"{round_num}회"

        for q_no in range(1, 51):
            row_id = f"{round_num}_{level}_{q_no}"
            answer_raw = answers.get(q_no)
            if answer_raw is None:
                errors.append(f"정답 없음: {row_id}")
                continue

            # PDF 정답은 1-based("3"), QuestionEntity.answerIndex는 0-based(2)
            answer_index = int(answer_raw) - 1

            q_data = questions.get(q_no)
            if q_data:
                content = q_data["content"]
                options_json = json.dumps(q_data["options"], ensure_ascii=False)
            else:
                content = f"(문제 파싱 실패) {round_num}회 {level_kr} {q_no}번"
                options_json = json.dumps(
                    [f"보기 {i}" for i in range(1, num_choices + 1)],
                    ensure_ascii=False
                )

            era = determine_era(content)

            conn.execute(
                """INSERT OR REPLACE INTO questions
                   (id, content, options, answer_index, level, category,
                    era, cached_at, source)
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                (row_id, content, options_json, answer_index, level, category,
                 era, cached_at, "local")
            )
            inserted += 1
            debug_rows.append({
                "id": row_id, "round": round_num, "level": level,
                "question_no": q_no, "era": era,
                "answer_index": answer_index,
                "content_preview": content[:60]
            })

    conn.commit()
    conn.close()

    # ─ 디버그 JSON 저장 ──────────────────────────────────────────────────────────
    os.makedirs(os.path.dirname(os.path.abspath(OUTPUT_JSON_DEBUG)), exist_ok=True)
    with open(OUTPUT_JSON_DEBUG, "w", encoding="utf-8") as f:
        json.dump(debug_rows, f, ensure_ascii=False, indent=2)

    # ─ 요약 ──────────────────────────────────────────────────────────────────────
    log(f"\n{'='*50}")
    log(f"완료: {inserted}개 레코드 삽입 → {output_db}")
    log(f"디버그 JSON: {OUTPUT_JSON_DEBUG}")
    if errors:
        log(f"오류 {len(errors)}건:")
        for e in errors[:10]:
            log(f"  - {e}")
    else:
        log("오류 없음 (OK)")
    log(f"{'='*50}")

    return inserted, errors


# ── 진입점 ─────────────────────────────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(description="QUIZ DB 빌드 파이프라인")
    parser.add_argument("--quiz-dir", default=QUIZ_DIR_DEFAULT,
                        help="QUIZ PDF 디렉토리 경로")
    parser.add_argument("--output", default=OUTPUT_DB_DEFAULT,
                        help="출력 SQLite DB 경로")
    parser.add_argument("--quiet", action="store_true", help="출력 최소화")
    args = parser.parse_args()

    build(args.quiz_dir, args.output, verbose=not args.quiet)


if __name__ == "__main__":
    main()
