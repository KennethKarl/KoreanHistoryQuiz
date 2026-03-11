import fitz
import cv2
import easyocr
import numpy as np

def test_ocr():
    pdf_path = r"C:\Users\pc\Downloads\QUIZ\69회 한국사 문제지(기본).pdf"
    print(f"Opening {pdf_path}")
    doc = fitz.open(pdf_path)
    page = doc[0]
    
    # Render page to an image
    pix = page.get_pixmap(dpi=150)
    img_data = pix.tobytes("png")
    
    # Convert to OpenCV format
    nparr = np.frombuffer(img_data, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    print("Running EasyOCR (this might take a minute on first run to download models)...")
    reader = easyocr.Reader(['ko', 'en'])
    results = reader.readtext(img)
    
    print("Detected text blocks:")
    for (bbox, text, prob) in results:
        # Check if the text looks like a question number, e.g., "1.", "2."
        text = text.strip()
        if text in ["1.", "2.", "3.", "4.", "5."] or ("1" in text and "." in text):
            print(f"Found possible question number: '{text}' at {bbox} with prob {prob}")

if __name__ == "__main__":
    test_ocr()
