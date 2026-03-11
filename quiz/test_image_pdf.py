import fitz
import sys

def analyze_pdf(path):
    doc = fitz.open(path)
    page = doc[0]
    
    # Check text
    text = page.get_text()
    print(f"Total Text length on page 1: {len(text)}")
    
    # Check images embedded
    images = page.get_images(full=True)
    print(f"Total Images strictly embedded on page 1: {len(images)}")

if __name__ == "__main__":
    analyze_pdf(sys.argv[1])
