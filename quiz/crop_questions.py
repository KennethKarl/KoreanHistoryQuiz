# crop_questions.py
# TASK-012: 문제 이미지 크롭 -- EasyOCR 기반 문제번호 감지 방식
# 스캔 PDF에서 문제 번호(1~50)의 Y 좌표를 OCR로 찾아 문제별로 크롭
import cv2
import numpy as np
import os
import re
import fitz  # PyMuPDF

try:
    import easyocr as _easyocr_module
    EASYOCR_AVAILABLE = True
except ImportError:
    EASYOCR_AVAILABLE = False

_reader = None

def _get_reader():
    global _reader
    if _reader is None:
        if not EASYOCR_AVAILABLE:
            raise RuntimeError("easyocr is not installed. Run: pip install easyocr")
        _reader = _easyocr_module.Reader(['ko', 'en'], gpu=False, verbose=False)
    return _reader


# 문제번호 패턴: "1." "12." "50." 또는 "10 " (마침표 없는 경우도 포함)
# - "N." or "N·" or "N," or "N " (N=1~50, 텍스트 시작부분)
_Q_PAT = re.compile(r'^([1-9]|[1-4][0-9]|50)[\s.·,]')


def _find_q_nums_in_strip(strip_img):
    """좁은 컬럼 마진 이미지에서 문제번호를 OCR로 찾아 반환.
    Returns: {q_num: y_center_in_strip}
    """
    reader = _get_reader()
    results = reader.readtext(strip_img, paragraph=False)
    q_positions = {}

    strip_w = strip_img.shape[1] if strip_img.ndim >= 2 else 200

    for bbox, text, conf in results:
        text = text.strip()
        m = _Q_PAT.match(text)
        if not m:
            continue
        if conf < 0.3:
            continue

        # 문제번호는 스트립의 왼쪽 절반에 위치해야 함 (x0 < strip_w * 0.6)
        x0_in_strip = bbox[0][0]
        if x0_in_strip > strip_w * 0.6:
            continue

        q_num = int(m.group(1))
        if 1 <= q_num <= 50 and q_num not in q_positions:
            y_mid = int((bbox[0][1] + bbox[2][1]) / 2)
            q_positions[q_num] = y_mid
    return q_positions


def process_exam(pdf_path, output_dir, session, level):
    """PDF에서 문제별 이미지를 크롭하여 output_dir에 저장.

    Args:
        pdf_path: 시험지 PDF 경로
        output_dir: 출력 디렉토리 (assets/images)
        session: 회차 번호 (int, e.g. 69)
        level: 레벨 문자열 ("basic" or "advanced")
    """
    if not os.path.exists(output_dir):
        os.makedirs(output_dir)

    print(f"Opening {pdf_path}")
    doc = fitz.open(pdf_path)
    DPI = 150

    # ---- Step 1: 모든 페이지 렌더링 + 문제번호 위치 수집 ----
    # all_q_info: {q_num: (page_num, is_left_col, y_in_page)}
    all_q_info = {}
    rendered_pages = {}

    for page_num in range(len(doc)):
        page = doc[page_num]
        pix = page.get_pixmap(dpi=DPI)
        img_bytes = pix.tobytes("png")
        arr = np.frombuffer(img_bytes, np.uint8)
        img = cv2.imdecode(arr, cv2.IMREAD_COLOR)
        rendered_pages[page_num] = img

        h, w = img.shape[:2]
        mid_x = w // 2

        # 좌측 컬럼 마진 스트립 (x: 10 ~ min(280, mid-5) px)
        left_x0 = 10
        left_x1 = min(280, mid_x - 5)
        left_strip = img[0:h, left_x0:left_x1]
        left_q = _find_q_nums_in_strip(left_strip)
        for q_num, y in left_q.items():
            if q_num not in all_q_info:
                all_q_info[q_num] = (page_num, True, y)

        # 우측 컬럼 마진 스트립 (x: mid_x-5 ~ mid_x+270 px)
        # mid_x-5 부터 시작해 컬럼 경계 근처의 문제번호도 포착
        right_x0 = max(0, mid_x - 5)
        right_x1 = min(mid_x + 270, w - 10)
        right_strip = img[0:h, right_x0:right_x1]
        right_q = _find_q_nums_in_strip(right_strip)
        for q_num, y in right_q.items():
            if q_num not in all_q_info:
                all_q_info[q_num] = (page_num, False, y)

        found = sorted(list(left_q.keys()) + list(right_q.keys()))
        print(f"  Page {page_num}: found Q{found}")

    # ---- Step 2: 문제번호 순으로 정렬 ----
    q_list = sorted(all_q_info.items())  # [(q_num, (page_num, is_left, y)), ...]

    if not q_list:
        print(f"WARNING: No questions found in {pdf_path}")
        return

    print(f"Total detected: {len(q_list)} questions: {[q for q, _ in q_list]}")

    # ---- Step 3: 각 문제 크롭 ----
    saved = 0
    for idx, (q_num, (page_num, is_left, y_start)) in enumerate(q_list):
        img = rendered_pages[page_num]
        h, w = img.shape[:2]
        mid_x = w // 2

        # X 범위
        x_start = 0 if is_left else mid_x
        x_end   = mid_x if is_left else w

        # Y 시작: 문제번호 약간 위
        y1 = max(0, y_start - 12)

        # Y 끝: 같은 페이지 + 같은 컬럼의 다음 문제 바로 위, 없으면 페이지 하단
        y2 = h
        for j in range(idx + 1, len(q_list)):
            next_q_num, (next_page, next_is_left, next_y) = q_list[j]
            if next_page != page_num:
                # 다음 문제가 다음 페이지에 있으면 현재 페이지 하단까지
                break
            if next_is_left == is_left:
                # 같은 컬럼의 다음 문제 시작 직전
                y2 = max(y1 + 80, next_y - 8)
                break
            # 다른 컬럼은 건너뜀 (계속 탐색)

        crop = img[y1:y2, x_start:x_end]
        if crop.size == 0:
            print(f"  SKIP Q{q_num}: empty crop")
            continue

        out_path = os.path.join(output_dir, f"{session}_{level}_{q_num}.png")

        # Windows 예약 장치명 보호: nul, con, prn, aux, com1~9, lpt1~9
        _WIN_RESERVED = {
            'nul', 'con', 'prn', 'aux',
            'com1', 'com2', 'com3', 'com4', 'com5',
            'com6', 'com7', 'com8', 'com9',
            'lpt1', 'lpt2', 'lpt3', 'lpt4', 'lpt5',
            'lpt6', 'lpt7', 'lpt8', 'lpt9',
        }
        stem = os.path.splitext(os.path.basename(out_path))[0].lower()
        if stem in _WIN_RESERVED:
            print(f"  SKIP reserved name: {out_path}")
            continue

        cv2.imwrite(out_path, crop)
        print(f"Saved {out_path}")
        saved += 1

    print(f"Finished: {saved} images saved for {session}_{level}.")


if __name__ == "__main__":
    # 단일 PDF 테스트
    pdf = r"C:\Users\pc\Downloads\QUIZ\69_기본_문제지.pdf"
    out = r"C:\Users\pc\Documents\project\TEST-HISTORY_QUIZofKorea_AOS\app\src\main\assets\images"
    process_exam(pdf, out, 69, "basic")
