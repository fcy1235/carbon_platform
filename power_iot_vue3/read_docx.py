import zipfile
import xml.etree.ElementTree as ET
import sys

def read_docx(file_path):
    with zipfile.ZipFile(file_path, 'r') as z:
        xml_content = z.read('word/document.xml')
    root = ET.fromstring(xml_content)
    paragraphs = []
    for para in root.iter('{http://schemas.openxmlformats.org/wordprocessingml/2006/main}p'):
        texts = []
        for node in para.iter('{http://schemas.openxmlformats.org/wordprocessingml/2006/main}t'):
            if node.text:
                texts.append(node.text)
        if texts:
            paragraphs.append(''.join(texts))
    return '\n'.join(paragraphs)

if __name__ == '__main__':
    content = read_docx(sys.argv[1])
    with open(sys.argv[2], 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"Written to {sys.argv[2]}")
