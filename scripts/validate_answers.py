# validate_answers.py
# korean_history_answers.json과 quiz.db의 answer_index가 일치하는지 검증한다.
#
# JSON: 1-based (1~5)
# DB:   0-based (0~4), 즉 answer_index = JSON.answer - 1
#
# 사용법:
#   python scripts/validate_answers.py          # 전체 검증 (불일치만 출력)
#   python scripts/validate_answers.py --all    # 전체 결과 상세 출력
#   python scripts/validate_answers.py --spot 75 basic 1  # 특정 문제 확인

import sqlite3
import json
import os
import sys
import argparse

SCRIPT_DIR   = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)
JSON_PATH    = os.path.join(PROJECT_ROOT, 'app', 'src', 'main', 'assets', 'korean_history_answers.json')
DB_PATH      = os.path.join(PROJECT_ROOT, 'app', 'src', 'main', 'assets', 'quiz.db')


def load_json_answers():
    """JSON에서 {q_id: answer_1based} 딕셔너리 반환"""
    with open(JSON_PATH, 'r', encoding='utf-8') as f:
        data = json.load(f)

    result = {}
    for exam in data['exams']:
        session = exam['session']
        level   = exam['level']
        for ans in exam['answers']:
            q_id  = f"{session}_{level}_{ans['questionNo']}"
            result[q_id] = ans['answer']   # 1-based
    return result


def load_db_answers(conn):
    """DB에서 {q_id: answer_index_0based} 딕셔너리 반환"""
    c = conn.cursor()
    c.execute('SELECT id, answer_index FROM questions')
    return {row[0]: row[1] for row in c.fetchall()}


def validate(show_all=False, spot=None):
    print(f'JSON : {JSON_PATH}')
    print(f'DB   : {DB_PATH}')
    print()

    json_ans = load_json_answers()
    conn     = sqlite3.connect(DB_PATH)
    db_ans   = load_db_answers(conn)
    conn.close()

    # --spot 단일 문제 확인
    if spot:
        session, level, q_no = spot
        q_id = f'{session}_{level}_{q_no}'
        j_ans = json_ans.get(q_id)
        d_ans = db_ans.get(q_id)
        print(f'=== 단일 검증: {q_id} ===')
        if q_id not in json_ans:
            print(f'  JSON에 없음')
        if q_id not in db_ans:
            print(f'  DB에 없음')
        if j_ans is not None and d_ans is not None:
            expected_0b = j_ans - 1
            match = (expected_0b == d_ans)
            status = '[OK] 일치' if match else '✗ 불일치'
            print(f'  JSON 정답  : {j_ans} (1-based)')
            print(f'  DB answer_index: {d_ans} (0-based)')
            print(f'  기대값     : {expected_0b} (0-based)')
            print(f'  결과       : {status}')
        return

    # 전체 검증
    total       = len(json_ans)
    matched     = 0
    mismatched  = 0
    not_in_db   = 0

    mismatch_list = []

    for q_id, j_ans in sorted(json_ans.items()):
        if q_id not in db_ans:
            not_in_db += 1
            if show_all:
                print(f'  [NOT_IN_DB] {q_id}')
            continue

        expected_0b = j_ans - 1
        d_ans       = db_ans[q_id]

        if expected_0b == d_ans:
            matched += 1
            if show_all:
                print(f'  [OK] {q_id}: JSON={j_ans}, DB={d_ans}')
        else:
            mismatched += 1
            mismatch_list.append((q_id, j_ans, d_ans))

    # 불일치 목록 출력
    if mismatch_list:
        print('=== 정답 불일치 목록 ===')
        for q_id, j_ans, d_ans in mismatch_list:
            print(f'  [MISMATCH] {q_id}: JSON={j_ans}(1b) / DB={d_ans}(0b), 기대={j_ans - 1}')
    else:
        print('=== 불일치 없음 [OK] ===')

    # DB에만 있는 문제 (JSON 미커버)
    db_only = set(db_ans.keys()) - set(json_ans.keys())
    print()
    print('=== 검증 결과 요약 ===')
    print(f'  JSON 정답 수      : {total}')
    print(f'  일치 (정답 맞음)  : {matched}')
    print(f'  불일치 (오류)     : {mismatched}')
    print(f'  DB 미존재         : {not_in_db}')
    print(f'  JSON 미커버 (DB전용): {len(db_only)}')
    print()

    if mismatched == 0 and not_in_db == 0:
        print('[PASS] 전체 검증 통과! JSON과 DB가 완전히 일치합니다.')
    else:
        print(f'[WARN]  검증 실패: {mismatched}건 불일치, {not_in_db}건 DB 미존재')

    if db_only:
        print(f'\n  DB 전용 문제 (JSON 미커버, 정답 미업데이트):')
        for q_id in sorted(db_only)[:20]:
            print(f'    {q_id}: DB answer_index={db_ans[q_id]}')
        if len(db_only) > 20:
            print(f'    ... 외 {len(db_only) - 20}건')


def main():
    parser = argparse.ArgumentParser(description='quiz.db 정답 검증 도구')
    parser.add_argument('--all',  action='store_true', help='전체 결과 상세 출력')
    parser.add_argument('--spot', nargs=3, metavar=('SESSION', 'LEVEL', 'Q_NO'),
                        help='특정 문제 확인 예) --spot 75 basic 1')
    args = parser.parse_args()

    spot = None
    if args.spot:
        try:
            session = int(args.spot[0])
            level   = args.spot[1]
            q_no    = int(args.spot[2])
            spot    = (session, level, q_no)
        except ValueError:
            print('--spot 인자 오류: SESSION과 Q_NO는 정수여야 합니다.')
            sys.exit(1)

    validate(show_all=args.all, spot=spot)


if __name__ == '__main__':
    main()
