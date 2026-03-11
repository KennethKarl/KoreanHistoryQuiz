# fix_answer_index.py
# korean_history_answers.json의 정답 데이터로 quiz.db answer_index를 정정한다.
# JSON은 1-based (1~5), DB는 0-based (0~4) 형식.
# 커버리지: 59~73회 (basic+advanced 일부) = 1200건
# 미커버: 74~77회 일부 = 300건 (변경 없음)

import sqlite3
import json
import os

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
PROJECT_ROOT = os.path.dirname(SCRIPT_DIR)

JSON_PATH = os.path.join(PROJECT_ROOT, 'app', 'src', 'main', 'assets', 'korean_history_answers.json')
DB_PATH   = os.path.join(PROJECT_ROOT, 'app', 'src', 'main', 'assets', 'quiz.db')


def main():
    print(f"JSON: {JSON_PATH}")
    print(f"DB  : {DB_PATH}")

    with open(JSON_PATH, 'r', encoding='utf-8') as f:
        data = json.load(f)

    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()

    updated = 0
    not_found = 0

    for exam in data['exams']:
        session = exam['session']
        level   = exam['level']
        for ans in exam['answers']:
            q_no      = ans['questionNo']
            answer_0b = ans['answer'] - 1   # 1-based → 0-based
            q_id      = f"{session}_{level}_{q_no}"

            result = c.execute(
                'UPDATE questions SET answer_index=? WHERE id=?',
                (answer_0b, q_id)
            )
            if result.rowcount:
                updated += 1
            else:
                not_found += 1

    conn.commit()
    conn.close()

    print(f"\nDone.")
    print(f"  Updated  : {updated} records")
    print(f"  Not found: {not_found} (ID mismatch or not in DB)")


if __name__ == '__main__':
    main()
