@echo off
echo Iniciando Sistema de Gestao de Projetos...
echo.

REM Verifica se Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Java nao encontrado!
    echo Por favor, instale o Java 11 ou superior.
    pause
    exit /b 1
)

REM Executa o sistema
java -jar target/gestao-projetos.jar

if %errorlevel% neq 0 (
    echo.
    echo ERRO: Falha ao executar o sistema!
    echo Verifique se o MySQL esta rodando e as configuracoes estao corretas.
    pause
)
