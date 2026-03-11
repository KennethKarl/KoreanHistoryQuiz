import json
import random

ERAS = ["고조선", "삼국시대", "통일신라", "고려시대", "조선시대", "근대", "일제강점기", "현대"]

def generate_mock_question(session, level, q_no, answer_index, num_choices):
    era = random.choice(ERAS)
    content = f"[{era}] {session}회 {level} 난이도 {q_no}번 문제입니다. 이 문제의 정답은 {answer_index}번이어야 합니다."
    options = []
    for i in range(1, num_choices + 1):
        if i == answer_index:
            options.append(f"이것은 {answer_index}번 정답 선택지입니다.")
        else:
            options.append(f"이것은 {i}번 오답 선택지입니다.")
            
    return content, options, era

with open('korean_history_answers.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

for exam in data['exams']:
    session = exam['session']
    level = exam['level']
    num_choices = exam['numChoices']
    
    for ans_dict in exam['answers']:
        q_no = ans_dict['questionNo']
        answer_idx = ans_dict['answer']
        
        gen_ans_idx = answer_idx if answer_idx > 0 else 1
        
        content, options, era = generate_mock_question(session, level, q_no, gen_ans_idx, num_choices)
        
        ans_dict['content'] = content
        ans_dict['options'] = options
        ans_dict['era'] = era

output_path = 'mock_korean_history_questions.json'
with open(output_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False, indent=2)

print(f"Generated complete mock questions with eras to {output_path}")
