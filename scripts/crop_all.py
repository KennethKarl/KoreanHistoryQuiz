# TASK-012: 문제 이미지 일괄 크롭 스크립트
# QUIZ DIR 의 모든 문제지 PDF를 순회하여
# quiz/crop_questions.py의 process_exam()으로 문제별 PNG 이미지를 추출한다.
# 출력: app/src/main/assets/images/{round_num}_{level}_{q_num}.png
#       예) 69_basic_1.png, 73_advanced_25.png

import sys
import os
import re

# quiz/crop_questions.py import를 위해 경로 추가
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
sys.path.insert(0, os.path.join(PROJECT_ROOT, "quiz"))

from crop_questions import process_exam  # noqa: E402

# ── 설정 ──────────────────────────────────────────────────────────────────────
QUIZ_DIR = r"C:\Users\pc\Downloads\QUIZ"
OUTPUT_DIR = os.path.join(PROJECT_ROOT, "app", "src", "main", "assets", "images")

# 한글 레벨명 → 영문 매핑
LEVEL_MAP = {"기본": "basic", "심화": "advanced"}

# 파일명 패턴: "69_기본_문제지.pdf", "73_심화_문제지.pdf" 등
PATTERN = re.compile(r"^(\d+)_?(기본|심화)_?문제지\.pdf$", re.IGNORECASE)
# ─────────────────────────────────────────────────────────────────────────────


def already_done(output_dir: str, round_num: int, level: str) -> bool:
    """해당 회차·레벨의 이미지가 38~55개 존재하면 True.
    (한국사 시험 기본 48~50문항 기준; 과분할 이미지 100+는 재크롭)
    """
    prefix = f"{round_num}_{level}_"
    if not os.path.isdir(output_dir):
        return False
    count = sum(1 for f in os.listdir(output_dir)
                if f.startswith(prefix) and f.endswith(".png"))
    return 38 <= count <= 55


def main():
    if not os.path.isdir(QUIZ_DIR):
        print(f"ERR  QUIZ 디렉토리를 찾을 수 없습니다: {QUIZ_DIR}")
        sys.exit(1)

    os.makedirs(OUTPUT_DIR, exist_ok=True)

    pdf_files = sorted(os.listdir(QUIZ_DIR))
    matched = []
    for fname in pdf_files:
        m = PATTERN.match(fname)
        if m:
            matched.append((int(m.group(1)), m.group(2), fname))

    if not matched:
        print("WARN 문제지 PDF 파일을 찾지 못했습니다. 파일명 패턴 확인: {회차}_{기본|심화}_문제지.pdf")
        return

    print(f"총 {len(matched)}개 문제지 PDF 발견\n")

    success, skip, fail = 0, 0, 0

    for round_num, kor_level, fname in matched:
        level = LEVEL_MAP.get(kor_level, kor_level)
        pdf_path = os.path.join(QUIZ_DIR, fname)

        if already_done(OUTPUT_DIR, round_num, level):
            print(f"SKIP {round_num}회 {level} -- 이미지 존재")
            skip += 1
            continue

        print(f"\n[{round_num}회 {level}] {fname}")
        try:
            process_exam(pdf_path, OUTPUT_DIR, round_num, level)
            success += 1
        except Exception as e:
            print(f"ERR  {fname}: {e}")
            fail += 1

    print(f"\n=== crop_all 완료 ===")
    print(f"  성공: {success}개")
    print(f"  스킵: {skip}개 (이미지 기존재)")
    print(f"  실패: {fail}개")
    print(f"  출력 디렉토리: {OUTPUT_DIR}")


if __name__ == "__main__":
    main()
