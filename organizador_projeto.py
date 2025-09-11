#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para organizar e validar o projeto Sistema de Gestão de Projetos
Autor: Sistema Automatizado
Versão: 1.0
"""

import os
import sys
import shutil
import subprocess
import urllib.request
import zipfile
import json
from pathlib import Path
from typing import List, Dict, Tuple
import re

class ProjetoOrganizador:
    def __init__(self, diretorio_raiz: str = "."):
        self.diretorio_raiz = Path(diretorio_raiz).resolve()
        self.erros = []
        self.avisos = []
        self.arquivos_criados = []
        self.arquivos_movidos = []
        
        # Estrutura esperada do projeto
        self.estrutura_projeto = {
            "src/main/java/com/gestao/projetos": [
                "MainApp.java"
            ],
            "src/main/java/com/gestao/projetos/model": [
                "Usuario.java",
                "Projeto.java", 
                "Tarefa.java"
            ],
            "src/main/java/com/gestao/projetos/dao": [
                "UsuarioDAO.java",
                "ProjetoDAO.java",
                "TarefaDAO.java"
            ],
            "src/main/java/com/gestao/projetos/controller": [
                "LoginController.java",
                "MainMenuController.java",
                "DashboardController.java",
                "UsuarioListController.java",
                "UsuarioFormController.java",
                "ProjetoListController.java",
                "ProjetoFormController.java",
                "TarefaListController.java",
                "AlterarSenhaController.java",
                "TarefaFormController.java"
            ],
            "src/main/java/com/gestao/projetos/util": [
                "DatabaseConnection.java",
                "SessionManager.java",
                "CryptUtils.java",
                "ValidationUtils.java"
            ],
            "src/main/resources/fxml": [
                "Login.fxml",
                "MainMenu.fxml",
                "Dashboard.fxml",
                "UsuarioList.fxml",
                "UsuarioForm.fxml",
                "ProjetoList.fxml",
                "ProjetoForm.fxml",
                "TarefaList.fxml",
                "TarefaForm.fxml",
                "AlterarSenha.fxml",
                "RelatoriosProjetos.fxml",
                "RelatoriosTarefas.fxml"
            ],
            "src/main/resources/css": [],
            "src/main/resources/images": [],
            "database": [
                "database_script.sql",
                "install.sql"
            ],
            "docs": [
                "README.md",
                "wireframes.md"
            ],
            "scripts": [
                "run.bat",
                "run.sh",
                "build.bat"
            ],
            ".": [
                "pom.xml",
                "module-info.java",
                ".gitignore"
            ]
        }

        # Mapeamento de arquivos conhecidos
        self.mapeamento_arquivos = {
            "MainApp.java": "src/main/java/com/gestao/projetos/",
            "Usuario.java": "src/main/java/com/gestao/projetos/model/",
            "Projeto.java": "src/main/java/com/gestao/projetos/model/",
            "Tarefa.java": "src/main/java/com/gestao/projetos/model/",
            "UsuarioDAO.java": "src/main/java/com/gestao/projetos/dao/",
            "ProjetoDAO.java": "src/main/java/com/gestao/projetos/dao/",
            "TarefaDAO.java": "src/main/java/com/gestao/projetos/dao/",
            "DatabaseConnection.java": "src/main/java/com/gestao/projetos/util/",
            "SessionManager.java": "src/main/java/com/gestao/projetos/util/",
            "CryptUtils.java": "src/main/java/com/gestao/projetos/util/",
            "ValidationUtils.java": "src/main/java/com/gestao/projetos/util/",
            "LoginController.java": "src/main/java/com/gestao/projetos/controller/",
            "MainMenuController.java": "src/main/java/com/gestao/projetos/controller/",
            "DashboardController.java": "src/main/java/com/gestao/projetos/controller/",
            "UsuarioListController.java": "src/main/java/com/gestao/projetos/controller/",
            "UsuarioFormController.java": "src/main/java/com/gestao/projetos/controller/",
            "ProjetoListController.java": "src/main/java/com/gestao/projetos/controller/",
            "ProjetoFormController.java": "src/main/java/com/gestao/projetos/controller/",
            "TarefaListController.java": "src/main/java/com/gestao/projetos/controller/",
            "AlterarSenhaController.java": "src/main/java/com/gestao/projetos/controller/",
            "Login.fxml": "src/main/resources/fxml/",
            "MainMenu.fxml": "src/main/resources/fxml/",
            "Dashboard.fxml": "src/main/resources/fxml/",
            "UsuarioList.fxml": "src/main/resources/fxml/",
            "UsuarioForm.fxml": "src/main/resources/fxml/",
            "ProjetoList.fxml": "src/main/resources/fxml/",
            "ProjetoForm.fxml": "src/main/resources/fxml/",
            "TarefaList.fxml": "src/main/resources/fxml/",
            "database_script.sql": "database/",
            "README.md": "docs/",
            "pom.xml": "./",
            "module-info.java": "src/main/java/",
            ".gitignore": "./"
        }

    def log(self, mensagem: str, tipo: str = "INFO"):
        """Log com cores"""
        cores = {
            "INFO": "\033[94m",    # Azul
            "SUCCESS": "\033[92m", # Verde
            "WARNING": "\033[93m", # Amarelo
            "ERROR": "\033[91m",   # Vermelho
            "RESET": "\033[0m"     # Reset
        }
        print(f"{cores.get(tipo, '')}{tipo}: {mensagem}{cores['RESET']}")

    def criar_estrutura_diretorios(self):
        """Cria a estrutura de diretórios do projeto"""
        self.log("Criando estrutura de diretórios...", "INFO")
        
        for diretorio in self.estrutura_projeto.keys():
            caminho = self.diretorio_raiz / diretorio
            if not caminho.exists():
                caminho.mkdir(parents=True, exist_ok=True)
                self.log(f"Diretório criado: {diretorio}", "SUCCESS")
            else:
                self.log(f"Diretório já existe: {diretorio}", "INFO")

    def detectar_arquivos_java(self) -> List[Path]:
        """Detecta arquivos Java na raiz e subdiretórios"""
        arquivos_java = []
        for arquivo in self.diretorio_raiz.rglob("*.java"):
            if arquivo.is_file():
                arquivos_java.append(arquivo)
        return arquivos_java

    def detectar_arquivos_fxml(self) -> List[Path]:
        """Detecta arquivos FXML na raiz e subdiretórios"""
        arquivos_fxml = []
        for arquivo in self.diretorio_raiz.rglob("*.fxml"):
            if arquivo.is_file():
                arquivos_fxml.append(arquivo)
        return arquivos_fxml

    def detectar_arquivos_sql(self) -> List[Path]:
        """Detecta arquivos SQL na raiz e subdiretórios"""
        arquivos_sql = []
        for arquivo in self.diretorio_raiz.rglob("*.sql"):
            if arquivo.is_file():
                arquivos_sql.append(arquivo)
        return arquivos_sql

    def detectar_package_java(self, arquivo: Path) -> str:
        """Detecta o package de um arquivo Java"""
        try:
            with open(arquivo, 'r', encoding='utf-8') as f:
                conteudo = f.read()
                match = re.search(r'package\s+([\w\.]+);', conteudo)
                if match:
                    return match.group(1)
        except Exception as e:
            self.log(f"Erro ao ler arquivo {arquivo}: {e}", "ERROR")
        return ""

    def organizar_arquivos_java(self):
        """Organiza arquivos Java baseado no package e nome"""
        self.log("Organizando arquivos Java...", "INFO")
        
        arquivos_java = self.detectar_arquivos_java()
        
        for arquivo in arquivos_java:
            # Pula se já estiver na estrutura correta
            if "src/main/java" in str(arquivo):
                continue
                
            nome_arquivo = arquivo.name
            package = self.detectar_package_java(arquivo)
            
            # Determina destino baseado no package ou nome
            destino = None
            
            if package:
                # Converte package para caminho
                package_path = package.replace('.', '/')
                destino = self.diretorio_raiz / "src/main/java" / package_path / nome_arquivo
            elif nome_arquivo in self.mapeamento_arquivos:
                # Usa mapeamento manual
                destino = self.diretorio_raiz / self.mapeamento_arquivos[nome_arquivo] / nome_arquivo
            else:
                # Tenta determinar pela classe
                if "Controller" in nome_arquivo:
                    destino = self.diretorio_raiz / "src/main/java/com/gestao/projetos/controller" / nome_arquivo
                elif "DAO" in nome_arquivo:
                    destino = self.diretorio_raiz / "src/main/java/com/gestao/projetos/dao" / nome_arquivo
                elif nome_arquivo in ["Usuario.java", "Projeto.java", "Tarefa.java"]:
                    destino = self.diretorio_raiz / "src/main/java/com/gestao/projetos/model" / nome_arquivo
                elif nome_arquivo == "MainApp.java":
                    destino = self.diretorio_raiz / "src/main/java/com/gestao/projetos" / nome_arquivo
                else:
                    destino = self.diretorio_raiz / "src/main/java/com/gestao/projetos/util" / nome_arquivo

            if destino:
                try:
                    # Cria diretório se não existir
                    destino.parent.mkdir(parents=True, exist_ok=True)
                    
                    # Move arquivo
                    if arquivo != destino:
                        shutil.move(str(arquivo), str(destino))
                        self.log(f"Movido: {nome_arquivo} -> {destino.relative_to(self.diretorio_raiz)}", "SUCCESS")
                        self.arquivos_movidos.append(f"{nome_arquivo} -> {destino.relative_to(self.diretorio_raiz)}")
                        
                except Exception as e:
                    self.erros.append(f"Erro ao mover {nome_arquivo}: {e}")
                    self.log(f"Erro ao mover {nome_arquivo}: {e}", "ERROR")

    def organizar_arquivos_fxml(self):
        """Organiza arquivos FXML"""
        self.log("Organizando arquivos FXML...", "INFO")
        
        arquivos_fxml = self.detectar_arquivos_fxml()
        
        for arquivo in arquivos_fxml:
            # Pula se já estiver na estrutura correta
            if "src/main/resources" in str(arquivo):
                continue
                
            nome_arquivo = arquivo.name
            destino = self.diretorio_raiz / "src/main/resources/fxml" / nome_arquivo
            
            try:
                destino.parent.mkdir(parents=True, exist_ok=True)
                if arquivo != destino:
                    shutil.move(str(arquivo), str(destino))
                    self.log(f"Movido: {nome_arquivo} -> resources/fxml/", "SUCCESS")
                    self.arquivos_movidos.append(f"{nome_arquivo} -> resources/fxml/")
            except Exception as e:
                self.erros.append(f"Erro ao mover {nome_arquivo}: {e}")
                self.log(f"Erro ao mover {nome_arquivo}: {e}", "ERROR")

    def organizar_arquivos_sql(self):
        """Organiza arquivos SQL"""
        self.log("Organizando arquivos SQL...", "INFO")
        
        arquivos_sql = self.detectar_arquivos_sql()
        
        for arquivo in arquivos_sql:
            # Pula se já estiver na estrutura correta
            if "database" in str(arquivo):
                continue
                
            nome_arquivo = arquivo.name
            destino = self.diretorio_raiz / "database" / nome_arquivo
            
            try:
                destino.parent.mkdir(parents=True, exist_ok=True)
                if arquivo != destino:
                    shutil.move(str(arquivo), str(destino))
                    self.log(f"Movido: {nome_arquivo} -> database/", "SUCCESS")
                    self.arquivos_movidos.append(f"{nome_arquivo} -> database/")
            except Exception as e:
                self.erros.append(f"Erro ao mover {nome_arquivo}: {e}")
                self.log(f"Erro ao mover {nome_arquivo}: {e}", "ERROR")

    def organizar_arquivos_especiais(self):
        """Organiza arquivos especiais (pom.xml, README, etc.)"""
        self.log("Organizando arquivos especiais...", "INFO")
        
        arquivos_especiais = {
            "pom.xml": "./",
            "module-info.java": "src/main/java/",
            ".gitignore": "./",
            "README.md": "docs/",
            "wireframes.md": "docs/",
            "run.bat": "scripts/",
            "run.sh": "scripts/",
            "build.bat": "scripts/",
            "install.sql": "database/"
        }
        
        for nome_arquivo, destino_rel in arquivos_especiais.items():
            arquivo = self.diretorio_raiz / nome_arquivo
            if arquivo.exists():
                destino = self.diretorio_raiz / destino_rel / nome_arquivo
                
                try:
                    destino.parent.mkdir(parents=True, exist_ok=True)
                    if arquivo != destino and not destino.exists():
                        shutil.move(str(arquivo), str(destino))
                        self.log(f"Movido: {nome_arquivo} -> {destino_rel}", "SUCCESS")
                        self.arquivos_movidos.append(f"{nome_arquivo} -> {destino_rel}")
                except Exception as e:
                    self.avisos.append(f"Aviso ao mover {nome_arquivo}: {e}")
                    self.log(f"Aviso ao mover {nome_arquivo}: {e}", "WARNING")

    def criar_pom_xml(self):
        """Cria pom.xml se não existir"""
        pom_path = self.diretorio_raiz / "pom.xml"
        
        if not pom_path.exists():
            pom_content = '''<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.gestao</groupId>
    <artifactId>gestao-projetos</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Sistema de Gestão de Projetos e Equipes</name>
    <description>Sistema para gerenciamento de projetos, equipes e tarefas</description>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <javafx.version>17.0.2</javafx.version>
        <mysql.version>8.0.33</mysql.version>
    </properties>

    <dependencies>
        <!-- JavaFX Controls -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- JavaFX FXML -->
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
        </dependency>

        <!-- MySQL Connector -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>gestao-projetos</finalName>
        
        <plugins>
            <!-- Compiler Plugin -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>11</source>
                    <target>11</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <!-- JavaFX Plugin -->
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <mainClass>com.gestao.projetos.MainApp</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>'''
            
            try:
                with open(pom_path, 'w', encoding='utf-8') as f:
                    f.write(pom_content)
                self.log("Arquivo pom.xml criado", "SUCCESS")
                self.arquivos_criados.append("pom.xml")
            except Exception as e:
                self.erros.append(f"Erro ao criar pom.xml: {e}")

    def criar_gitignore(self):
        """Cria .gitignore se não existir"""
        gitignore_path = self.diretorio_raiz / ".gitignore"
        
        if not gitignore_path.exists():
            gitignore_content = '''# Compiled class file
*.class

# Log file
*.log

# Package Files
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
.settings/
.project
.classpath

# OS
.DS_Store
Thumbs.db

# Database
*.db
*.sqlite

# Logs
logs/
*.out'''
            
            try:
                with open(gitignore_path, 'w', encoding='utf-8') as f:
                    f.write(gitignore_content)
                self.log("Arquivo .gitignore criado", "SUCCESS")
                self.arquivos_criados.append(".gitignore")
            except Exception as e:
                self.erros.append(f"Erro ao criar .gitignore: {e}")

    def verificar_maven(self) -> bool:
        """Verifica se Maven está instalado"""
        try:
            result = subprocess.run(['mvn', '-version'], 
                                  capture_output=True, text=True, timeout=10)
            return result.returncode == 0
        except Exception:
            return False

    def verificar_java(self) -> bool:
        """Verifica se Java está instalado"""
        try:
            result = subprocess.run(['java', '-version'], 
                                  capture_output=True, text=True, timeout=10)
            return result.returncode == 0
        except Exception:
            return False

    def baixar_dependencias(self):
        """Baixa dependências do Maven"""
        if not self.verificar_maven():
            self.erros.append("Maven não está instalado. Instale Maven para baixar dependências.")
            return

        if not self.verificar_java():
            self.erros.append("Java não está instalado. Instale Java 11+ para compilar o projeto.")
            return

        self.log("Baixando dependências do Maven...", "INFO")
        
        try:
            os.chdir(self.diretorio_raiz)
            result = subprocess.run(['mvn', 'dependency:resolve'], 
                                  capture_output=True, text=True, timeout=300)
            
            if result.returncode == 0:
                self.log("Dependências baixadas com sucesso", "SUCCESS")
            else:
                self.erros.append(f"Erro ao baixar dependências: {result.stderr}")
                self.log(f"Erro ao baixar dependências: {result.stderr}", "ERROR")
                
        except subprocess.TimeoutExpired:
            self.erros.append("Timeout ao baixar dependências")
        except Exception as e:
            self.erros.append(f"Erro ao executar Maven: {e}")

    def validar_estrutura(self) -> Dict:
        """Valida se todos os arquivos estão nos lugares corretos"""
        self.log("Validando estrutura do projeto...", "INFO")
        
        arquivos_faltando = []
        arquivos_encontrados = []
        
        for diretorio, arquivos in self.estrutura_projeto.items():
            caminho_dir = self.diretorio_raiz / diretorio
            
            if not caminho_dir.exists():
                self.erros.append(f"Diretório não encontrado: {diretorio}")
                continue
                
            for arquivo in arquivos:
                caminho_arquivo = caminho_dir / arquivo
                if caminho_arquivo.exists():
                    arquivos_encontrados.append(str(caminho_arquivo.relative_to(self.diretorio_raiz)))
                else:
                    arquivos_faltando.append(str(caminho_arquivo.relative_to(self.diretorio_raiz)))
        
        return {
            "encontrados": arquivos_encontrados,
            "faltando": arquivos_faltando
        }

    def compilar_projeto(self):
        """Tenta compilar o projeto"""
        if not self.verificar_maven():
            self.avisos.append("Maven não disponível - pulando compilação")
            return

        self.log("Tentando compilar o projeto...", "INFO")
        
        try:
            os.chdir(self.diretorio_raiz)
            result = subprocess.run(['mvn', 'compile'], 
                                  capture_output=True, text=True, timeout=300)
            
            if result.returncode == 0:
                self.log("Projeto compilado com sucesso!", "SUCCESS")
            else:
                self.avisos.append(f"Erros de compilação encontrados: {result.stderr}")
                self.log("Projeto com erros de compilação", "WARNING")
                
        except subprocess.TimeoutExpired:
            self.avisos.append("Timeout na compilação")
        except Exception as e:
            self.avisos.append(f"Erro ao compilar: {e}")

    def gerar_relatorio(self, validacao: Dict):
        """Gera relatório final"""
        print("\n" + "="*70)
        print("🎯 RELATÓRIO FINAL - ORGANIZAÇÃO DO PROJETO")
        print("="*70)
        
        # Estatísticas
        print(f"\n📊 ESTATÍSTICAS:")
        print(f"   ✅ Arquivos movidos: {len(self.arquivos_movidos)}")
        print(f"   ✅ Arquivos criados: {len(self.arquivos_criados)}")
        print(f"   ✅ Arquivos encontrados: {len(validacao['encontrados'])}")
        print(f"   ⚠️  Arquivos faltando: {len(validacao['faltando'])}")
        print(f"   ❌ Erros encontrados: {len(self.erros)}")
        print(f"   ⚠️  Avisos: {len(self.avisos)}")
        
        # Arquivos movidos
        if self.arquivos_movidos:
            print(f"\n📁 ARQUIVOS ORGANIZADOS:")
            for movimento in self.arquivos_movidos:
                print(f"   📂 {movimento}")
        
        # Arquivos criados
        if self.arquivos_criados:
            print(f"\n📄 ARQUIVOS CRIADOS:")
            for arquivo in self.arquivos_criados:
                print(f"   📝 {arquivo}")
        
        # Arquivos faltando
        if validacao['faltando']:
            print(f"\n❌ ARQUIVOS FALTANDO:")
            for arquivo in validacao['faltando']:
                print(f"   📄 {arquivo}")
        
        # Erros
        if self.erros:
            print(f"\n🚨 ERROS ENCONTRADOS:")
            for erro in self.erros:
                print(f"   ❌ {erro}")
        
        # Avisos
        if self.avisos:
            print(f"\n⚠️  AVISOS:")
            for aviso in self.avisos:
                print(f"   ⚠️  {aviso}")
        
        # Próximos passos
        print(f"\n🚀 PRÓXIMOS PASSOS:")
        print(f"   1. Configure o MySQL e execute database/database_script.sql")
        print(f"   2. Ajuste as credenciais em DatabaseConnection.java")
        print(f"   3. Execute: mvn javafx:run")
        
        # Status final
        if len(self.erros) == 0 and len(validacao['faltando']) < 5:
            print(f"\n🎉 PROJETO ORGANIZADO COM SUCESSO!")
            print(f"   O projeto está pronto para uso!")
        elif len(self.erros) > 0:
            print(f"\n⚠️  PROJETO ORGANIZADO COM ALGUNS ERROS")
            print(f"   Verifique os erros acima antes de continuar.")
        else:
            print(f"\n📋 PROJETO PARCIALMENTE ORGANIZADO")
            print(f"   Alguns arquivos podem estar faltando.")

def main():
    print("🔧 ORGANIZADOR DE PROJETO JAVA - SISTEMA DE GESTÃO")
    print("="*50)
    
    # Pega diretório atual ou argumento
    diretorio = sys.argv[1] if len(sys.argv) > 1 else "."
    
    try:
        organizador = ProjetoOrganizador(diretorio)
        
        # Executa organização
        organizador.log("Iniciando organização do projeto...", "INFO")
        
        # 1. Cria estrutura de diretórios
        organizador.criar_estrutura_diretorios()
        
        # 2. Organiza arquivos por tipo
        organizador.organizar_arquivos_java()
        organizador.organizar_arquivos_fxml()
        organizador.organizar_arquivos_sql()
        organizador.organizar_arquivos_especiais()
        
        # 3. Cria arquivos essenciais se não existirem
        organizador.criar_pom_xml()
        organizador.criar_gitignore()
        
        # 4. Baixa dependências
        organizador.baixar_dependencias()
        
        # 5. Compila projeto
        organizador.compilar_projeto()
        
        # 6. Valida estrutura
        validacao = organizador.validar_estrutura()
        
        # 7. Gera relatório
        organizador.gerar_relatorio(validacao)
        
    except Exception as e:
        print(f"\n❌ ERRO CRÍTICO: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()