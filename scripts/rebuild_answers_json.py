# rebuild_answers_json.py
# quiz/ori/ 의 모든 *_정답표.pdf 를 올바르게 파싱하여
# korean_history_answers.json 을 전체 재구성한다.
#
# PDF 블록 구조 A (대부분): 문항번호\n정답(①②③④⑤)\n배점\n
# PDF 블록 구조 B (일부):   문항번호\n정답(1~5)\n배점(1~3)\n
# 기존 파서 버그: 배점(plain digit)을 정답으로 잘못 추출
# 수정: ①②③④⑤ (U+2460~U+2464) 문자를 직접 매핑, fallback으로 plain digit 처리

import re
import fitz  # PyMuPDF
import json
import os
import glob

SCRIPT_DIR   = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
ORI_DIR      = os.path.join(PROJECT_ROOT, 'quiz', 'ori')
JSON_PATH    = os.path.join(PROJECT_ROOT, 'app', 'src', 'main', 'assets', 'korean_history_answers.json')

# ①=1, ②=2, ③=3, ④=4, ⑤=5  (U+2460 ~ U+2464)
CIRCLE_CHARS = {chr(0x2460 + i): i + 1 for i in range(5)}
CIRCLE_PAT   = re.compile(r'(\d+)\n([' + ''.join(CIRCLE_CHARS.keys()) + r'])\n(\d+)')

LEVEL_MAP = {'기본': 'basic', '심화': 'advanced'}

# 최소 추출 문제 수: 이 이상이면 포맷 A 결과 채택
_MIN_CIRCLE = 10


def _parse_circle(text):
    """포맷 A: 문항번호\n[①②③④⑤]\n배점 → {q_no: 정답(1~5)}"""
    answers = {}
    for m in CIRCLE_PAT.finditer(text):
        q_no   = int(m.group(1))
        answer = CIRCLE_CHARS[m.group(2)]
        if 1 <= q_no <= 50:
            answers[q_no] = answer
    return answers


def _parse_plain(text):
    """포맷 B: 문항번호\n정답(1~5)\n배점(1~3) (plain digit, 각 숫자 독립 라인)
    → {q_no: 정답(1~5)}
    라인 단위로 스캔하여 오직 '순수 숫자 라인' 3연속만 처리.
    헤더·한글 등 잡음 라인은 자동 무시됨.
    """
    lines = text.split('\n')
    answers = {}
    i = 0
    while i + 2 < len(lines):
        l0 = lines[i].strip()
        l1 = lines[i + 1].strip()
        l2 = lines[i + 2].strip()

        # l0 = q_no(1~50), l1 = answer(1~5), l2 = score(1~3)
        if (re.fullmatch(r'\d{1,2}', l0)
                and re.fullmatch(r'[1-5]', l1)
                and re.fullmatch(r'[1-3]', l2)):
            q_no = int(l0)
            ans  = int(l1)
            if 1 <= q_no <= 50 and q_no not in answers:
                answers[q_no] = ans
            i += 3
        else:
            i += 1
    return answers


def parse_answer_pdf(pdf_path):
    """
    정답표 PDF에서 {문항번호(int): 정답(1~5, int)} 딕셔너리와 포맷 문자('A'/'B')를 반환한다.
    - 전체 페이지 텍스트를 합산하여 파싱
    - 포맷 A(circled chars) 먼저 시도, 결과 부족 시 포맷 B(plain digit) fallback
    """
    doc = fitz.open(pdf_path)
    # 전체 페이지 텍스트 합산
    text = '\n'.join(page.get_text() for page in doc)

    # 포맷 A 시도
    answers = _parse_circle(text)
    if len(answers) >= _MIN_CIRCLE:
        return answers, 'A'

    # 포맷 B fallback
    answers_b = _parse_plain(text)
    if len(answers_b) > len(answers):
        return answers_b, 'B'

    return answers, 'A'


def build_exam_entry(pdf_path):
    """PDF 한 파일을 파싱하여 exam dict 반환 (실패 시 None)"""
    basename = os.path.basename(pdf_path)  # "75_기본_정답표.pdf"
    parts    = basename.split('_')
    try:
        session  = int(parts[0])
        level_kr = parts[1]                # "기본" or "심화"
    except (IndexError, ValueError):
        print(f'  [SKIP] 파일명 파싱 실패: {basename}')
        return None

    level = LEVEL_MAP.get(level_kr, level_kr)
    answers_dict, fmt = parse_answer_pdf(pdf_path)

    if not answers_dict:
        print(f'  [WARN] 정답 추출 0건: {basename}')
        return None

    # 50문제 전부 추출됐는지 경고
    if len(answers_dict) != 50:
        print(f'  [WARN] {basename}: {len(answers_dict)}/50 문제만 추출됨 (포맷{fmt})')

    answers_list = [
        {"questionNo": qno, "answer": ans}
        for qno, ans in sorted(answers_dict.items())
    ]

    return {
        "session":        session,
        "level":          level,
        "levelKr":        level_kr,
        "numChoices":     5,
        "totalQuestions": len(answers_list),
        "answers":        answers_list,
    }


def main():
    # *_정답표.pdf 파일 전부 수집
    pdfs = sorted(glob.glob(os.path.join(ORI_DIR, '*_정답표.pdf')))
    print(f'정답표 PDF 발견: {len(pdfs)}개')

    exams = []
    warn_count = 0
    for pdf in pdfs:
        entry = build_exam_entry(pdf)
        if entry:
            q_cnt = entry['totalQuestions']
            flag  = '' if q_cnt == 50 else f'  ← {q_cnt}/50 경고'
            print(f'  OK  {os.path.basename(pdf)}: {q_cnt}문제{flag}')
            exams.append(entry)
            if q_cnt != 50:
                warn_count += 1

    # session 오름차순, level 알파벳순 정렬
    exams.sort(key=lambda e: (e['session'], e['level']))

    data = {
        "version":     2,
        "description": "한국사능력검정시험 정답 데이터 (PDF 직접 파싱 v2)",
        "examCount":   len(exams),
        "exams":       exams,
    }

    with open(JSON_PATH, 'w', encoding='utf-8') as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print()
    print(f'저장 완료: {JSON_PATH}')
    print(f'총 시험 수: {len(exams)}, 경고: {warn_count}건')
    if warn_count:
        print('  → 경고 항목은 PDF 구조를 직접 확인하세요.')


if __name__ == '__main__':
    main()
