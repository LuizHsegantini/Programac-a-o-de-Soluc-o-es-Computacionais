#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Script para verificar e instalar dependências do projeto Java
Autor: Sistema Automatizado  
Versão: 1.0
"""

import os
import sys
import subprocess
import urllib.request
import zipfile
import tarfile
import platform
from pathlib import Path
import json
import shutil

class VerificadorAmbiente:
    def __init__(self):
        self.sistema = platform.system().lower()
        self.arquitetura = platform.machine().lower()
        self.diretorio_raiz = Path(".").resolve()
        self.diretorio_temp = self.diretorio_raiz / "temp_downloads"
        self.erros = []
        self.instalacoes = []

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

    def criar_diretorio_temp(self):
        """Cria diretório temporário para downloads"""
        if not self.diretorio_temp.exists():
            self.diretorio_temp.mkdir(exist_ok=True)

    def limpar_diretorio_temp(self):
        """Remove diretório temporário"""
        if self.diretorio_temp.exists():
            shutil.rmtree(self.diretorio_temp)

    def verificar_java(self) -> dict:
        """Verifica instalação do Java"""
        try:
            result = subprocess.run(['java', '-version'], 
                                  capture_output=True, text=True, timeout=10)
            if result.returncode == 0:
                # Extrai versão do Java
                output = result.stderr  # Java -version escreve no stderr
                versao = "Desconhecida"
                if "version" in output:
                    lines = output.split('\n')
                    for line in lines:
                        if 'version' in line and '"' in line:
                            versao = line.split('"')[1]
                            break
                
                return {
                    "instalado": True,
                    "versao": versao,
                    "comando": "java",
                    "output": output
                }
        except Exception as e:
            self.log(f"Erro ao verificar Java: {e}", "ERROR")
        
        return {"instalado": False}

    def verificar_maven(self) -> dict:
        """Verifica instalação do Maven"""
        try:
            result = subprocess.run(['mvn', '-version'], 
                                  capture_output=True, text=True, timeout=10)
            if result.returncode == 0:
                output = result.stdout
                versao = "Desconhecida"
                if "Apache Maven" in output:
                    lines = output.split('\n')
                    for line in lines:
                        if 'Apache Maven' in line:
                            versao = line.split()[2]
                            break
                
                return {
                    "instalado": True,
                    "versao": versao,
                    "comando": "mvn",
                    "output": output
                }
        except Exception as e:
            self.log(f"Erro ao verificar Maven: {e}", "ERROR")
        
        return {"instalado": False}

    def verificar_mysql(self) -> dict:
        """Verifica instalação do MySQL"""
        comandos = ['mysql', 'mysqld']
        
        for comando in comandos:
            try:
                result = subprocess.run([comando, '--version'], 
                                      capture_output=True, text=True, timeout=10)
                if result.returncode == 0:
                    output = result.stdout
                    versao = "Desconhecida"
                    if "mysql" in output.lower():
                        # Tenta extrair versão
                        parts = output.split()
                        for i, part in enumerate(parts):
                            if "Ver" in part or "version" in part.lower():
                                if i + 1 < len(parts):
                                    versao = parts[i + 1]
                                    break
                    
                    return {
                        "instalado": True,
                        "versao": versao,
                        "comando": comando,
                        "output": output
                    }
            except Exception:
                continue
        
        return {"instalado": False}

    def verificar_git(self) -> dict:
        """Verifica instalação do Git"""
        try:
            result = subprocess.run(['git', '--version'], 
                                  capture_output=True, text=True, timeout=10)
            if result.returncode == 0:
                output = result.stdout
                versao = output.split()[2] if len(output.split()) > 2 else "Desconhecida"
                
                return {
                    "instalado": True,
                    "versao": versao,
                    "comando": "git",
                    "output": output
                }
        except Exception as e:
            self.log(f"Erro ao verificar Git: {e}", "ERROR")
        
        return {"instalado": False}

    def baixar_arquivo(self, url: str, destino: Path) -> bool:
        """Baixa arquivo da internet"""
        try:
            self.log(f"Baixando {url}...", "INFO")
            urllib.request.urlretrieve(url, destino)
            return True
        except Exception as e:
            self.erros.append(f"Erro ao baixar {url}: {e}")
            self.log(f"Erro ao baixar {url}: {e}", "ERROR")
            return False

    def obter_urls_java(self) -> dict:
        """Retorna URLs de download do Java 11 baseado no sistema"""
        urls = {
            "windows": {
                "x86_64": "https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.zip",
                "aarch64": "https://download.oracle.com/java/17/latest/jdk-17_windows-aarch64_bin.zip"
            },
            "linux": {
                "x86_64": "https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz",
                "aarch64": "https://download.oracle.com/java/17/latest/jdk-17_linux-aarch64_bin.tar.gz"
            },
            "darwin": {  # macOS
                "x86_64": "https://download.oracle.com/java/17/latest/jdk-17_macos-x64_bin.tar.gz",
                "aarch64": "https://download.oracle.com/java/17/latest/jdk-17_macos-aarch64_bin.tar.gz"
            }
        }
        
        return urls.get(self.sistema, {}).get(self.arquitetura)

    def obter_urls_maven(self) -> str:
        """Retorna URL de download do Maven"""
        return "https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"

    def instalar_java_portable(self) -> bool:
        """Instala Java de forma portátil"""
        self.log("Tentando instalar Java 17 de forma portátil...", "INFO")
        
        url = self.obter_urls_java()
        if not url:
            self.erros.append(f"URL do Java não disponível para {self.sistema} {self.arquitetura}")
            return False

        self.criar_diretorio_temp()
        
        # Baixa Java
        arquivo_java = self.diretorio_temp / "java.zip" if url.endswith('.zip') else self.diretorio_temp / "java.tar.gz"
        
        if not self.baixar_arquivo(url, arquivo_java):
            return False

        # Extrai Java
        try:
            diretorio_java = self.diretorio_raiz / "java"
            diretorio_java.mkdir(exist_ok=True)
            
            if arquivo_java.suffix == '.zip':
                with zipfile.ZipFile(arquivo_java, 'r') as zip_ref:
                    zip_ref.extractall(diretorio_java)
            else:
                with tarfile.open(arquivo_java, 'r:gz') as tar_ref:
                    tar_ref.extractall(diretorio_java)
            
            # Encontra diretório extraído
            subdirs = [d for d in diretorio_java.iterdir() if d.is_dir()]
            if subdirs:
                java_home = subdirs[0]
                
                # Cria script de ambiente
                self.criar_script_ambiente(java_home)
                
                self.log(f"Java instalado em: {java_home}", "SUCCESS")
                self.instalacoes.append(f"Java 17 instalado em {java_home}")
                return True
                
        except Exception as e:
            self.erros.append(f"Erro ao extrair Java: {e}")
            self.log(f"Erro ao extrair Java: {e}", "ERROR")
        
        return False

    def instalar_maven_portable(self) -> bool:
        """Instala Maven de forma portátil"""
        self.log("Tentando instalar Maven de forma portátil...", "INFO")
        
        url = self.obter_urls_maven()
        self.criar_diretorio_temp()
        
        # Baixa Maven
        arquivo_maven = self.diretorio_temp / "maven.zip"
        
        if not self.baixar_arquivo(url, arquivo_maven):
            return False

        # Extrai Maven
        try:
            diretorio_maven = self.diretorio_raiz / "maven"
            diretorio_maven.mkdir(exist_ok=True)
            
            with zipfile.ZipFile(arquivo_maven, 'r') as zip_ref:
                zip_ref.extractall(diretorio_maven)
            
            # Encontra diretório extraído
            subdirs = [d for d in diretorio_maven.iterdir() if d.is_dir()]
            if subdirs:
                maven_home = subdirs[0]
                
                self.log(f"Maven instalado em: {maven_home}", "SUCCESS")
                self.instalacoes.append(f"Maven instalado em {maven_home}")
                return True
                
        except Exception as e:
            self.erros.append(f"Erro ao extrair Maven: {e}")
            self.log(f"Erro ao extrair Maven: {e}", "ERROR")
        
        return False

    def criar_script_ambiente(self, java_home: Path):
        """Cria scripts para configurar ambiente"""
        
        # Script Windows
        script_bat = self.diretorio_raiz / "setup_env.bat"
        conteudo_bat = f'''@echo off
echo Configurando ambiente Java e Maven...

set JAVA_HOME={java_home}
set MAVEN_HOME={self.diretorio_raiz / "maven"}
set PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%

echo JAVA_HOME=%JAVA_HOME%
echo MAVEN_HOME=%MAVEN_HOME%
echo.
echo Ambiente configurado! Execute:
echo mvn clean compile
echo mvn javafx:run
echo.
cmd /k
'''
        
        # Script Linux/Mac
        script_sh = self.diretorio_raiz / "setup_env.sh"
        conteudo_sh = f'''#!/bin/bash
echo "Configurando ambiente Java e Maven..."

export JAVA_HOME="{java_home}"
export MAVEN_HOME="{self.diretorio_raiz / "maven"}"
export PATH="$JAVA_HOME/bin:$MAVEN_HOME/bin:$PATH"

echo "JAVA_HOME=$JAVA_HOME"
echo "MAVEN_HOME=$MAVEN_HOME"
echo ""
echo "Ambiente configurado! Execute:"
echo "mvn clean compile"
echo "mvn javafx:run"
echo ""

bash
'''
        
        try:
            with open(script_bat, 'w', encoding='utf-8') as f:
                f.write(conteudo_bat)
            
            with open(script_sh, 'w', encoding='utf-8') as f:
                f.write(conteudo_sh)
            
            # Torna script executável no Linux/Mac
            if self.sistema != "windows":
                os.chmod(script_sh, 0o755)
            
            self.log("Scripts de ambiente criados: setup_env.bat e setup_env.sh", "SUCCESS")
            
        except Exception as e:
            self.log(f"Erro ao criar scripts: {e}", "ERROR")

    def verificar_e_instalar_dependencias(self):
        """Verifica e instala dependências se necessário"""
        self.log("Verificando dependências do sistema...", "INFO")
        
        # Verifica Java
        java_info = self.verificar_java()
        if java_info["instalado"]:
            self.log(f"Java encontrado: {java_info['versao']}", "SUCCESS")
        else:
            self.log("Java não encontrado. Tentando instalar...", "WARNING")
            if not self.instalar_java_portable():
                self.erros.append("Falha ao instalar Java automaticamente")
        
        # Verifica Maven
        maven_info = self.verificar_maven()
        if maven_info["instalado"]:
            self.log(f"Maven encontrado: {maven_info['versao']}", "SUCCESS")
        else:
            self.log("Maven não encontrado. Tentando instalar...", "WARNING")
            if not self.instalar_maven_portable():
                self.erros.append("Falha ao instalar Maven automaticamente")
        
        # Verifica MySQL
        mysql_info = self.verificar_mysql()
        if mysql_info["instalado"]:
            self.log(f"MySQL encontrado: {mysql_info['versao']}", "SUCCESS")
        else:
            self.log("MySQL não encontrado", "WARNING")
            self.erros.append("MySQL precisa ser instalado manualmente")
        
        # Verifica Git
        git_info = self.verificar_git()
        if git_info["instalado"]:
            self.log(f"Git encontrado: {git_info['versao']}", "SUCCESS")
        else:
            self.log("Git não encontrado", "WARNING")

    def baixar_connector_mysql(self):
        """Baixa MySQL Connector se necessário"""
        self.log("Verificando MySQL Connector...", "INFO")
        
        connector_dir = self.diretorio_raiz / "lib"
        connector_dir.mkdir(exist_ok=True)
        
        connector_jar = connector_dir / "mysql-connector-java-8.0.33.jar"
        
        if not connector_jar.exists():
            url = "https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar"
            
            self.criar_diretorio_temp()
            temp_jar = self.diretorio_temp / "mysql-connector.jar"
            
            if self.baixar_arquivo(url, temp_jar):
                shutil.move(str(temp_jar), str(connector_jar))
                self.log("MySQL Connector baixado", "SUCCESS")
                self.instalacoes.append("MySQL Connector baixado")
            else:
                self.erros.append("Falha ao baixar MySQL Connector")
        else:
            self.log("MySQL Connector já existe", "INFO")

    def criar_arquivos_faltantes(self):
        """Cria arquivos essenciais que podem estar faltando"""
        
        # TarefaFormController.java
        tarefa_form_controller = self.diretorio_raiz / "src/main/java/com/gestao/projetos/controller/TarefaFormController.java"
        if not tarefa_form_controller.exists():
            tarefa_form_controller.parent.mkdir(parents=True, exist_ok=True)
            conteudo = '''package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import com.gestao.projetos.MainApp;
import com.gestao.projetos.dao.TarefaDAO;
import com.gestao.projetos.dao.ProjetoDAO;
import com.gestao.projetos.dao.UsuarioDAO;
import com.gestao.projetos.model.Tarefa;
import com.gestao.projetos.model.Projeto;
import com.gestao.projetos.model.Usuario;
import com.gestao.projetos.util.SessionManager;
import com.gestao.projetos.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para formulário de tarefa
 */
public class TarefaFormController {
    
    @FXML private Label lblTitle;
    @FXML private TextField txtTitulo;
    @FXML private TextArea txtDescricao;
    @FXML private ComboBox<Projeto> cbProjeto;
    @FXML private ComboBox<Usuario> cbResponsavel;
    @FXML private ComboBox<Tarefa.Status> cbStatus;
    @FXML private ComboBox<Tarefa.Prioridade> cbPrioridade;
    @FXML private DatePicker dpDataPrevista;
    @FXML private Button btnSalvar;
    
    private TarefaDAO tarefaDAO = new TarefaDAO();
    private ProjetoDAO projetoDAO = new ProjetoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Tarefa tarefa; // null = nova tarefa
    
    @FXML
    private void initialize() {
        setupComboBoxes();
    }
    
    private void setupComboBoxes() {
        try {
            // Status
            cbStatus.setItems(FXCollections.observableArrayList(Tarefa.Status.values()));
            cbStatus.setValue(Tarefa.Status.PENDENTE);
            
            // Prioridade
            cbPrioridade.setItems(FXCollections.observableArrayList(Tarefa.Prioridade.values()));
            cbPrioridade.setValue(Tarefa.Prioridade.MEDIA);
            
            // Projetos
            List<Projeto> projetos = projetoDAO.findAll();
            cbProjeto.setItems(FXCollections.observableArrayList(projetos));
            
            // Usuários
            List<Usuario> usuarios = usuarioDAO.findAll();
            cbResponsavel.setItems(FXCollections.observableArrayList(usuarios));
            
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar dados: " + e.getMessage());
        }
    }
    
    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
        lblTitle.setText("Editar Tarefa");
        btnSalvar.setText("Atualizar");
        
        // Preenche campos
        txtTitulo.setText(tarefa.getTitulo());
        txtDescricao.setText(tarefa.getDescricao());
        cbStatus.setValue(tarefa.getStatus());
        cbPrioridade.setValue(tarefa.getPrioridade());
        dpDataPrevista.setValue(tarefa.getDataPrevistaConclusao());
        
        // Seleciona projeto e responsável
        try {
            Projeto projeto = projetoDAO.findById(tarefa.getProjetoId());
            cbProjeto.setValue(projeto);
            
            Usuario responsavel = usuarioDAO.findById(tarefa.getResponsavelId());
            cbResponsavel.setValue(responsavel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            if (tarefa == null) {
                criarNovaTarefa();
            } else {
                atualizarTarefa();
            }
            
            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao salvar tarefa: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnSalvar.getScene().getWindow();
        stage.close();
    }
    
    private boolean validarCampos() {
        if (!ValidationUtils.isNotEmpty(txtTitulo, "Título")) return false;
        if (!ValidationUtils.isNotEmpty(txtDescricao, "Descrição")) return false;
        if (!ValidationUtils.hasSelection(cbProjeto, "Projeto")) return false;
        if (!ValidationUtils.hasSelection(cbResponsavel, "Responsável")) return false;
        if (!ValidationUtils.hasSelection(cbStatus, "Status")) return false;
        if (!ValidationUtils.hasSelection(cbPrioridade, "Prioridade")) return false;
        
        return true;
    }
    
    private void criarNovaTarefa() throws Exception {
        Tarefa novaTarefa = new Tarefa();
        novaTarefa.setTitulo(txtTitulo.getText().trim());
        novaTarefa.setDescricao(txtDescricao.getText().trim());
        novaTarefa.setProjetoId(cbProjeto.getValue().getId());
        novaTarefa.setResponsavelId(cbResponsavel.getValue().getId());
        novaTarefa.setStatus(cbStatus.getValue());
        novaTarefa.setPrioridade(cbPrioridade.getValue());
        novaTarefa.setDataPrevistaConclusao(dpDataPrevista.getValue());
        
        tarefaDAO.save(novaTarefa);
        MainApp.showInfo("Sucesso", "Tarefa cadastrada com sucesso.");
    }
    
    private void atualizarTarefa() throws Exception {
        tarefa.setTitulo(txtTitulo.getText().trim());
        tarefa.setDescricao(txtDescricao.getText().trim());
        tarefa.setProjetoId(cbProjeto.getValue().getId());
        tarefa.setResponsavelId(cbResponsavel.getValue().getId());
        tarefa.setStatus(cbStatus.getValue());
        tarefa.setPrioridade(cbPrioridade.getValue());
        tarefa.setDataPrevistaConclusao(dpDataPrevista.getValue());
        
        tarefaDAO.update(tarefa);
        MainApp.showInfo("Sucesso", "Tarefa atualizada com sucesso.");
    }
}'''
            
            try:
                with open(tarefa_form_controller, 'w', encoding='utf-8') as f:
                    f.write(conteudo)
                self.log("TarefaFormController.java criado", "SUCCESS")
                self.instalacoes.append("TarefaFormController.java criado")
            except Exception as e:
                self.erros.append(f"Erro ao criar TarefaFormController.java: {e}")

        # TarefaForm.fxml
        tarefa_form_fxml = self.diretorio_raiz / "src/main/resources/fxml/TarefaForm.fxml"
        if not tarefa_form_fxml.exists():
            tarefa_form_fxml.parent.mkdir(parents=True, exist_ok=True)
            conteudo_fxml = '''<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.*?>

<VBox xmlns="http://javafx.com/javafx/11.0.1" xmlns:fx="http://javafx.com/fxml/1" fx:controller="com.gestao.projetos.controller.TarefaFormController">
   <children>
      <Label fx:id="lblTitle" text="Cadastro de Tarefa">
         <font>
            <Font name="System Bold" size="16.0" />
         </font>
      </Label>
      
      <GridPane hgap="10.0" vgap="15.0">
        <columnConstraints>
          <ColumnConstraints hgrow="SOMETIMES" maxWidth="120.0" minWidth="120.0" prefWidth="120.0" />
          <ColumnConstraints hgrow="SOMETIMES" minWidth="10.0" prefWidth="300.0" />
        </columnConstraints>
        <rowConstraints>
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
          <RowConstraints minHeight="10.0" prefHeight="60.0" vgrow="SOMETIMES" />
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
          <RowConstraints minHeight="10.0" prefHeight="30.0" vgrow="SOMETIMES" />
        </rowConstraints>
         <children>
            <Label text="Título:" />
            <TextField fx:id="txtTitulo" GridPane.columnIndex="1" />
            
            <Label text="Descrição:" GridPane.rowIndex="1" />
            <TextArea fx:id="txtDescricao" prefRowCount="3" GridPane.columnIndex="1" GridPane.rowIndex="1" />
            
            <Label text="Projeto:" GridPane.rowIndex="2" />
            <ComboBox fx:id="cbProjeto" prefWidth="200.0" GridPane.columnIndex="1" GridPane.rowIndex="2" />
            
            <Label text="Responsável:" GridPane.rowIndex="3" />
            <ComboBox fx:id="cbResponsavel" prefWidth="200.0" GridPane.columnIndex="1" GridPane.rowIndex="3" />
            
            <Label text="Status:" GridPane.rowIndex="4" />
            <ComboBox fx:id="cbStatus" prefWidth="200.0" GridPane.columnIndex="1" GridPane.rowIndex="4" />
            
            <Label text="Prioridade:" GridPane.rowIndex="5" />
            <ComboBox fx:id="cbPrioridade" prefWidth="200.0" GridPane.columnIndex="1" GridPane.rowIndex="5" />
            
            <Label text="Data Prevista:" GridPane.rowIndex="6" />
            <DatePicker fx:id="dpDataPrevista" GridPane.columnIndex="1" GridPane.rowIndex="6" />
         </children>
      </GridPane>
      
      <HBox alignment="CENTER" spacing="10.0">
         <children>
            <Button fx:id="btnSalvar" mnemonicParsing="false" onAction="#handleSalvar" text="Salvar" />
            <Button mnemonicParsing="false" onAction="#handleCancelar" text="Cancelar" />
         </children>
      </HBox>
   </children>
   <padding>
      <Insets bottom="20.0" left="20.0" right="20.0" top="20.0" />
   </padding>
</VBox>'''
            
            try:
                with open(tarefa_form_fxml, 'w', encoding='utf-8') as f:
                    f.write(conteudo_fxml)
                self.log("TarefaForm.fxml criado", "SUCCESS")
                self.instalacoes.append("TarefaForm.fxml criado")
            except Exception as e:
                self.erros.append(f"Erro ao criar TarefaForm.fxml: {e}")

    def gerar_relatorio_ambiente(self):
        """Gera relatório do ambiente"""
        print("\n" + "="*70)
        print("🔧 RELATÓRIO DE VERIFICAÇÃO DO AMBIENTE")
        print("="*70)
        
        # Verifica novamente todas as dependências
        java_info = self.verificar_java()
        maven_info = self.verificar_maven()
        mysql_info = self.verificar_mysql()
        git_info = self.verificar_git()
        
        print(f"\n🔍 DEPENDÊNCIAS VERIFICADAS:")
        
        # Java
        if java_info["instalado"]:
            print(f"   ✅ Java: {java_info['versao']}")
        else:
            print(f"   ❌ Java: Não instalado")
        
        # Maven
        if maven_info["instalado"]:
            print(f"   ✅ Maven: {maven_info['versao']}")
        else:
            print(f"   ❌ Maven: Não instalado")
        
        # MySQL
        if mysql_info["instalado"]:
            print(f"   ✅ MySQL: {mysql_info['versao']}")
        else:
            print(f"   ❌ MySQL: Não instalado")
        
        # Git
        if git_info["instalado"]:
            print(f"   ✅ Git: {git_info['versao']}")
        else:
            print(f"   ⚠️  Git: Não instalado (opcional)")
        
        # Instalações realizadas
        if self.instalacoes:
            print(f"\n📦 INSTALAÇÕES REALIZADAS:")
            for instalacao in self.instalacoes:
                print(f"   ✅ {instalacao}")
        
        # Erros encontrados
        if self.erros:
            print(f"\n❌ ERROS ENCONTRADOS:")
            for erro in self.erros:
                print(f"   ❌ {erro}")
        
        # Instruções finais
        print(f"\n📋 PRÓXIMOS PASSOS:")
        
        if not java_info["instalado"] or not maven_info["instalado"]:
            print(f"   1. Execute setup_env.bat (Windows) ou setup_env.sh (Linux/Mac)")
            print(f"   2. Configure o MySQL e execute database/database_script.sql")
            print(f"   3. Ajuste credenciais em DatabaseConnection.java")
            print(f"   4. Execute: mvn clean compile")
            print(f"   5. Execute: mvn javafx:run")
        else:
            print(f"   1. Configure o MySQL e execute database/database_script.sql")
            print(f"   2. Ajuste credenciais em DatabaseConnection.java")
            print(f"   3. Execute: mvn clean compile")
            print(f"   4. Execute: mvn javafx:run")
        
        if not mysql_info["instalado"]:
            print(f"\n⚠️  MYSQL NÃO ENCONTRADO:")
            print(f"   Baixe e instale MySQL de: https://dev.mysql.com/downloads/")
            print(f"   Ou use XAMPP: https://www.apachefriends.org/")

def main():
    print("🔧 VERIFICADOR DE AMBIENTE JAVA")
    print("="*40)
    
    try:
        verificador = VerificadorAmbiente()
        
        # Executa verificação e instalação
        verificador.log("Iniciando verificação do ambiente...", "INFO")
        
        # 1. Verifica e instala dependências
        verificador.verificar_e_instalar_dependencias()
        
        # 2. Baixa MySQL Connector
        verificador.baixar_connector_mysql()
        
        # 3. Cria arquivos faltantes
        verificador.criar_arquivos_faltantes()
        
        # 4. Limpa arquivos temporários
        verificador.limpar_diretorio_temp()
        
        # 5. Gera relatório final
        verificador.gerar_relatorio_ambiente()
        
    except Exception as e:
        print(f"\n❌ ERRO CRÍTICO: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
