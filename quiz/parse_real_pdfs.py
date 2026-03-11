import glob
import re
import json
import fitz  # PyMuPDF
import os

QUIZ_DIR = r"C:\Users\pc\Downloads\QUIZ"
ANSWERS_JSON_PATH = "korean_history_answers.json"
OUTPUT_JSON_PATH = "parsed_korean_history_questions.json"

ERA_KEYWORDS = {
    "고조선": ["단군", "팔조법", "고조선", "위만", "홍익인간"],
    "삼국시대": ["고구려", "백제", "신라", "가야", "광개토", "장수왕", "근초고왕", "진흥왕", "무열왕", "살수대첩", "안시성", "삼국 통일"],
    "통일신라": ["통일신라", "발해", "문무왕", "신문왕", "장보고", "대조영", "선덕여왕", "성덕대왕", "불국사", "석굴암", "해동성국"],
    "고려시대": ["고려", "왕건", "광종", "성종", "무신", "몽골", "공민왕", "거란", "여진", "팔만대장경", "벽란도", "최충헌"],
    "조선시대": ["조선", "이성계", "태종", "세종", "세조", "성종", "연산군", "중종", "명종", "선조", "광해군", "인조", "효종", "현종", "숙종", "영조", "정조", "임진왜란", "병자호란", "실학", "세도정치", "흥선대원군"],
    "근대": ["강화도 조약", "개항", "갑신정변", "동학", "갑오개혁", "독립협회", "대한제국", "을사늑약", "의병", "개화기", "위정척사"],
    "일제강점기": ["일제", "조선총독부", "3.1운동", "임시정부", "독립군", "광복군", "무단통치", "문화통치", "민족말살", "김구", "안중근", "윤봉길", "신채호"],
    "현대": ["광복", "대한민국", "이승만", "박정희", "전두환", "노태우", "김영삼", "김대중", "노무현", "6.25", "민주화운동", "IMF", "남북정상회담"]
}

def determine_era(text):
    text = text.replace(" ", "")
    scores = {era: 0 for era in ERA_KEYWORDS}
    for era, keywords in ERA_KEYWORDS.items():
        for kw in keywords:
            kw_nospace = kw.replace(" ", "")
            scores[era] += text.count(kw_nospace)
    
    max_era = max(scores, key=scores.get)
    if scores[max_era] > 0:
        return max_era
    return "미정"

def extract_text_from_pdf(pdf_path):
    doc = fitz.open(pdf_path)
    full_text = ""
    for page in doc:
        blocks = page.get_text("blocks")
        blocks.sort(key=lambda b: (b[1], b[0]))
        for b in blocks:
            full_text += b[4] + "\n"
    return full_text

def parse_questions_from_text(text):
    questions_data = {}
    pattern = r"(\d+)\.\s*(.*?)(?=\n\d+\.\s|$)"
    matches = list(re.finditer(pattern, text, re.DOTALL))
    
    for i, match in enumerate(matches):
        q_no = int(match.group(1))
        # Skip weird large numbers that might have been matched
        if q_no > 50 or q_no < 1:
            continue
            
        content_raw = match.group(2).strip()
        
        opt_pattern = r"([①②③④⑤])(.*?)(?=[①②③④⑤]|$)"
        opt_matches = list(re.finditer(opt_pattern, content_raw, re.DOTALL))
        
        options = []
        if opt_matches:
            for om in opt_matches:
                options.append(om.group(2).strip().replace("\n", " "))
            
            # Remove options from the main content
            content = content_raw[:opt_matches[0].start()].strip()
            # Try to clean up newlines in content for better readability, but preserve some structure
            content = re.sub(r'\n+', '\n', content)
        else:
            content = content_raw
            # If options aren't found well, just keep them empty, we'll fallback
            options = ["(파싱 실패 - 선택지 1)", "(파싱 실패 - 선택지 2)", "(파싱 실패 - 선택지 3)", "(파싱 실패 - 선택지 4)", "(파싱 실패 - 선택지 5)"]
            
        questions_data[q_no] = {
            "content": content,
            "options": options
        }
    return questions_data

def process_pdfs():
    print("Loading answers JSON...")
    with open(ANSWERS_JSON_PATH, "r", encoding="utf-8") as f:
        master_data = json.load(f)
        
    pdf_files = glob.glob(os.path.join(QUIZ_DIR, "*.pdf"))
    
    # Store parsed text by (session, level)
    parsed_pdfs = {}
    
    for pdf_path in pdf_files:
        filename = os.path.basename(pdf_path)
        if "문제지" not in filename:
            continue
            
        # Extract session and level
        sess_match = re.search(r"(\d+)회", filename)
        if not sess_match: continue
        session = int(sess_match.group(1))
        
        level = "advanced" if ("심화" in filename) else "basic"
        if "기본" not in filename and "심화" not in filename:
            pass # fallback if needed
            
        print(f"Parsing PDF: {filename} -> Session {session}, Level {level}")
        text = extract_text_from_pdf(pdf_path)
        q_data = parse_questions_from_text(text)
        
        parsed_pdfs[(session, level)] = q_data

    # Merge into the master_data
    for exam in master_data["exams"]:
        session = exam["session"]
        level = exam["level"]
        key = (session, level)
        
        q_data = parsed_pdfs.get(key, {})
        
        for ans_dict in exam["answers"]:
            q_no = ans_dict["questionNo"]
            parsed_q = q_data.get(q_no)
            
            num_choices = exam["numChoices"]
            
            if parsed_q:
                content = parsed_q["content"]
                options = parsed_q["options"]
                
                # Trim or pad options to match num_choices
                if len(options) > num_choices:
                    options = options[:num_choices]
                elif len(options) < num_choices:
                    for i in range(len(options), num_choices):
                        options.append(f"(파싱 실패 - 보기 {i+1})")
                
                era = determine_era(content)
                
                ans_dict["content"] = f"[{era}] " + content
                ans_dict["options"] = options
                ans_dict["era"] = era
            else:
                # Fallback if PDF was missing or question not parsed
                era_fallback = "미정"
                ans_dict["content"] = f"[{era_fallback}] (문제 파싱 실패) {session}회 {level} {q_no}번"
                ans_dict["options"] = [f"보기 {i}" for i in range(1, num_choices + 1)]
                ans_dict["era"] = era_fallback

    with open(OUTPUT_JSON_PATH, "w", encoding="utf-8") as f:
        json.dump(master_data, f, ensure_ascii=False, indent=2)
        
    print(f"Done. Saved to {OUTPUT_JSON_PATH}")

if __name__ == "__main__":
    process_pdfs()
