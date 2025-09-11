#!/usr/bin/env python3
import subprocess
import sys
import os

def main():
    print("🚀 Executando Sistema de Gestão de Projetos...")
    
    try:
        # Executa o sistema
        subprocess.run(['mvn', 'javafx:run'], timeout=None)
            
    except KeyboardInterrupt:
        print("\n⏹️  Sistema interrompido pelo usuário")
    except FileNotFoundError:
        print("❌ Maven não encontrado. Execute setup_env.bat primeiro.")
    except Exception as e:
        print(f"❌ Erro: {e}")

if __name__ == "__main__":
    main()
