#!/usr/bin/env python3
import shutil
import os
from pathlib import Path

def main():
    print("🧹 Limpando arquivos temporários...")
    
    diretorios_limpar = ["target", "temp_downloads", ".mvn"]
    arquivos_limpar = ["*.log", "*.tmp"]
    
    for dir_name in diretorios_limpar:
        dir_path = Path(dir_name)
        if dir_path.exists():
            shutil.rmtree(dir_path)
            print(f"🗑️  Removido: {dir_name}")
    
    print("✅ Limpeza concluída!")

if __name__ == "__main__":
    main()
