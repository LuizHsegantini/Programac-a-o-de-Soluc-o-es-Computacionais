#!/usr/bin/env python3
import subprocess
import sys
import os

def main():
    print("🔨 Compilando projeto...")
    
    try:
        # Limpa e compila
        result = subprocess.run(['mvn', 'clean', 'compile'], 
                              capture_output=True, text=True, timeout=300)
        
        if result.returncode == 0:
            print("✅ Compilação bem-sucedida!")
            print("\n🚀 Para executar o sistema:")
            print("   mvn javafx:run")
        else:
            print("❌ Erros de compilação:")
            print(result.stderr)
            
    except subprocess.TimeoutExpired:
        print("❌ Timeout na compilação")
    except FileNotFoundError:
        print("❌ Maven não encontrado. Execute setup_env.bat primeiro.")
    except Exception as e:
        print(f"❌ Erro: {e}")

if __name__ == "__main__":
    main()
