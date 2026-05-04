import os
import re

def process_file(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception as e:
        print(f"Skipping {filepath}: {e}")
        return False

    original = content

    content = re.sub(r'PocketGateway', 'CrypsealGateway', content)
    content = re.sub(r'Pocket Gateway', 'Crypseal Gateway', content, flags=re.IGNORECASE)
    content = re.sub(r'\.pocket', '.crypseal', content)
    
    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    root_dir = r"c:\CHANGERS\Crypseal\Crypseal_full_handoff_v3"
    updated_files = []
    for dirpath, dirnames, filenames in os.walk(root_dir):
        if "rename.py" in filenames:
            filenames.remove("rename.py")
        
        for name in filenames:
            if name.startswith('.'):
                continue
            if name.endswith(('.md', '.json', '.kt', '.sh', '.txt')):
                filepath = os.path.join(dirpath, name)
                if process_file(filepath):
                    updated_files.append(filepath)

    print("Updated files:")
    for f in updated_files:
        print(f)

if __name__ == '__main__':
    main()
