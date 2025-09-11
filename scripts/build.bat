@echo off
echo Compilando Sistema de Gestao de Projetos...
echo.

REM Verifica se Maven está instalado
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERRO: Maven nao encontrado!
    echo Por favor, instale o Apache Maven.
    pause
    exit /b 1
)

REM Limpa e compila o projeto
echo Limpando projeto anterior...
mvn clean

echo Compilando projeto...
mvn compile

echo Empacotando aplicacao...
mvn package

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo COMPILACAO CONCLUIDA COM SUCESSO!
    echo ========================================
    echo.
    echo Para executar o sistema:
    echo java -jar target/gestao-projetos.jar
    echo.
    echo Ou execute: run.bat
    echo.
) else (
    echo.
    echo ========================================
    echo ERRO NA COMPILACAO!
    echo ========================================
    echo Verifique os erros acima.
)

pause