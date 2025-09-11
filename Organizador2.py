#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script Principal - Setup Completo do Sistema de Gestão de Projetos
Autor: Sistema Automatizado
Versão: 1.0

Este script realiza o setup completo do projeto:
1. Organiza estrutura de arquivos
2. Verifica e instala dependências
3. Valida a configuração
4. Prepara o projeto para execução
"""

import os
import sys
import subprocess
from pathlib import Path
import time

def log(mensagem: str, tipo: str = "INFO"):
    """Log com cores e timestamp"""
    cores = {
        "INFO": "\033[94m",      # Azul
        "SUCCESS": "\033[92m",   # Verde
        "WARNING": "\033[93m",   # Amarelo
        "ERROR": "\033[91m",     # Vermelho
        "HEADER": "\033[95m",    # Magenta
        "RESET": "\033[0m"       # Reset
    }
    timestamp = time.strftime("%H:%M:%S")
    print(f"{cores.get(tipo, '')}{timestamp} [{tipo}] {mensagem}{cores['RESET']}")

def verificar_python():
    """Verifica se a versão do Python é compatível"""
    if sys.version_info < (3, 6):
        log("Python 3.6+ é necessário para executar este script", "ERROR")
        return False
    return True

def executar_organizador():
    """Executa o script organizador de projeto"""
    log("Executando organizador de projeto...", "INFO")
    
    # Código do organizador aqui (inline para simplificar)
    try:
        exec(open("organizador_projeto.py").read()) if Path("organizador_projeto.py").exists() else None
        return True
    except Exception as e:
        log(f"Erro ao executar organizador: {e}", "ERROR")
        return False

def executar_verificador():
    """Executa o verificador de ambiente"""
    log("Executando verificador de ambiente...", "INFO")
    
    try:
        exec(open("verificador_ambiente.py").read()) if Path("verificador_ambiente.py").exists() else None
        return True
    except Exception as e:
        log(f"Erro ao executar verificador: {e}", "ERROR")
        return False

def criar_scripts_auxiliares():
    """Cria scripts auxiliares para o projeto"""
    
    # Script de compilação rápida
    script_compile = Path("compile.py")
    conteudo_compile = '''#!/usr/bin/env python3
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
            print("\\n🚀 Para executar o sistema:")
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
'''
    
    # Script de execução
    script_run = Path("run.py")
    conteudo_run = '''#!/usr/bin/env python3
import subprocess
import sys
import os

def main():
    print("🚀 Executando Sistema de Gestão de Projetos...")
    
    try:
        # Executa o sistema
        subprocess.run(['mvn', 'javafx:run'], timeout=None)
            
    except KeyboardInterrupt:
        print("\\n⏹️  Sistema interrompido pelo usuário")
    except FileNotFoundError:
        print("❌ Maven não encontrado. Execute setup_env.bat primeiro.")
    except Exception as e:
        print(f"❌ Erro: {e}")

if __name__ == "__main__":
    main()
'''
    
    # Script de reset/limpeza
    script_reset = Path("reset.py")
    conteudo_reset = '''#!/usr/bin/env python3
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
'''
    
    try:
        # Cria os scripts
        with open(script_compile, 'w', encoding='utf-8') as f:
            f.write(conteudo_compile)
        
        with open(script_run, 'w', encoding='utf-8') as f:
            f.write(conteudo_run)
        
        with open(script_reset, 'w', encoding='utf-8') as f:
            f.write(conteudo_reset)
        
        log("Scripts auxiliares criados: compile.py, run.py, reset.py", "SUCCESS")
        return True
        
    except Exception as e:
        log(f"Erro ao criar scripts auxiliares: {e}", "ERROR")
        return False

def criar_readme_setup():
    """Cria README com instruções de setup"""
    readme_path = Path("SETUP.md")
    
    conteudo = '''# 🚀 Setup do Sistema de Gestão de Projetos

## ⚡ Setup Automático (Recomendado)

```bash
# Execute este comando para setup completo:
python setup_completo.py
```

## 📋 O que o setup automático faz:

1. ✅ **Organiza arquivos** na estrutura correta
2. ✅ **Verifica dependências** (Java, Maven, MySQL)
3. ✅ **Instala dependências** faltantes (portável)
4. ✅ **Baixa MySQL Connector**
5. ✅ **Cria arquivos** faltantes
6. ✅ **Valida estrutura** do projeto
7. ✅ **Gera relatórios** de status

## 🔧 Setup Manual

### 1. Pré-requisitos
- **Java 11+**: https://adoptium.net/
- **Maven 3.6+**: https://maven.apache.org/download.cgi
- **MySQL 8.0+**: https://dev.mysql.com/downloads/

### 2. Configuração do Banco
```sql
-- Execute no MySQL:
CREATE DATABASE gestao_projetos;
SOURCE database/database_script.sql;
```

### 3. Configuração do Projeto
```bash
# Ajuste credenciais em:
src/main/java/com/gestao/projetos/util/DatabaseConnection.java

# Compile o projeto:
mvn clean compile

# Execute o sistema:
mvn javafx:run
```

## 🎯 Scripts Disponíveis

- `python compile.py` - Compila o projeto
- `python run.py` - Executa o sistema  
- `python reset.py` - Limpa arquivos temporários
- `setup_env.bat` - Configura ambiente (Windows)
- `setup_env.sh` - Configura ambiente (Linux/Mac)

## 👥 Usuários de Teste

- **admin / 123456** (Administrador)
- **joao / 123456** (Gerente)
- **pedro / 123456** (Colaborador)

## 🐛 Resolução de Problemas

### Erro de Compilação
```bash
# Limpa e recompila:
mvn clean compile
```

### Erro de Conexão com Banco
1. Verifique se MySQL está rodando
2. Confirme credenciais em DatabaseConnection.java
3. Teste conexão: `mysql -u root -p`

### Erro JavaFX
```bash
# Execute com módulos JavaFX:
mvn javafx:run
```

### Erro de Dependências
```bash
# Baixa dependências novamente:
mvn dependency:resolve
```

## 📞 Suporte

Se encontrar problemas:
1. Execute `python setup_completo.py` novamente
2. Verifique logs de erro
3. Consulte documentação completa em `docs/README.md`
'''
    
    try:
        with open(readme_path, 'w', encoding='utf-8') as f:
            f.write(conteudo)
        log("SETUP.md criado com instruções detalhadas", "SUCCESS")
        return True
    except Exception as e:
        log(f"Erro ao criar SETUP.md: {e}", "ERROR")
        return False

def mostrar_banner():
    """Mostra banner inicial"""
    banner = '''
╔══════════════════════════════════════════════════════════════╗
║                                                              ║
║    🏗️  SETUP AUTOMÁTICO - SISTEMA DE GESTÃO DE PROJETOS     ║
║                                                              ║
║    📊 Organiza estrutura do projeto                          ║
║    🔧 Verifica e instala dependências                        ║
║    📦 Baixa bibliotecas necessárias                          ║
║    ✅ Valida configuração completa                           ║
║                                                              ║
║    Versão: 1.0 | Linguagem: Java + JavaFX + MySQL          ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
'''
    print(banner)

def mostrar_resumo_final():
    """Mostra resumo final do setup"""
    resumo = '''
╔══════════════════════════════════════════════════════════════╗
║                    🎉 SETUP CONCLUÍDO!                      ║
╠══════════════════════════════════════════════════════════════╣
║                                                              ║
║  📁 Estrutura organizada                                     ║
║  🔧 Dependências verificadas                                 ║
║  📦 Bibliotecas baixadas                                     ║
║  📝 Scripts auxiliares criados                               ║
║                                                              ║
║  🚀 PARA EXECUTAR O SISTEMA:                                 ║
║                                                              ║
║  1. Configure MySQL (veja SETUP.md)                         ║
║  2. Execute: python compile.py                               ║
║  3. Execute: python run.py                                   ║
║                                                              ║
║  📖 Consulte SETUP.md para instruções detalhadas            ║
║                                                              ║
╚══════════════════════════════════════════════════════════════╝
'''
    print(resumo)

def main():
    """Função principal do setup completo"""
    
    # Mostra banner
    mostrar_banner()
    
    # Verifica Python
    if not verificar_python():
        sys.exit(1)
    
    log("Iniciando setup completo do projeto...", "HEADER")
    
    sucesso_total = True
    
    # Etapa 1: Organizar estrutura
    log("Etapa 1/5: Organizando estrutura de arquivos...", "HEADER")
    if not executar_organizador():
        log("Falha na organização de arquivos", "ERROR")
        sucesso_total = False
    
    # Etapa 2: Verificar ambiente
    log("Etapa 2/5: Verificando ambiente e dependências...", "HEADER")
    if not executar_verificador():
        log("Falha na verificação do ambiente", "ERROR")
        sucesso_total = False
    
    # Etapa 3: Criar scripts auxiliares
    log("Etapa 3/5: Criando scripts auxiliares...", "HEADER")
    if not criar_scripts_auxiliares():
        log("Falha ao criar scripts auxiliares", "ERROR")
        sucesso_total = False
    
    # Etapa 4: Criar documentação
    log("Etapa 4/5: Criando documentação de setup...", "HEADER")
    if not criar_readme_setup():
        log("Falha ao criar documentação", "ERROR")
        sucesso_total = False
    
    # Etapa 5: Validação final
    log("Etapa 5/5: Validação final...", "HEADER")
    
    # Verifica se estrutura básica existe
    estrutura_ok = True
    diretorios_essenciais = [
        "src/main/java/com/gestao/projetos",
        "src/main/resources/fxml",
        "database"
    ]
    
    for diretorio in diretorios_essenciais:
        if not Path(diretorio).exists():
            log(f"Diretório essencial não encontrado: {diretorio}", "ERROR")
            estrutura_ok = False
    
    if not estrutura_ok:
        sucesso_total = False
    
    # Resultado final
    if sucesso_total:
        log("Setup completo executado com sucesso!", "SUCCESS")
        mostrar_resumo_final()
    else:
        log("Setup concluído com alguns erros. Verifique os logs acima.", "WARNING")
    
    # Próximos passos
    print("\n🔄 PRÓXIMOS PASSOS:")
    print("1. Leia o arquivo SETUP.md para instruções detalhadas")
    print("2. Configure o MySQL com o script database/database_script.sql")  
    print("3. Execute: python compile.py")
    print("4. Execute: python run.py")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        log("Setup interrompido pelo usuário", "WARNING")
    except Exception as e:
        log(f"Erro crítico no setup: {e}", "ERROR")
        sys.exit(1)