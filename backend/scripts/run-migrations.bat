@echo off
REM Script para executar migrations no PostgreSQL (Windows)
REM Uso: run-migrations.bat

echo 🔄 Executando migrations do banco de dados...

REM Configurações do banco
set DB_HOST=localhost
set DB_PORT=5432
set DB_USER=italo
set DB_PASSWORD=Senha@123
set DB_NAME=consulta_sql

REM Diretório das migrations
set MIGRATIONS_DIR=./migrations

REM Verificar se o diretório de migrations existe
if not exist "%MIGRATIONS_DIR%" (
    echo ❌ Diretório de migrations não encontrado: %MIGRATIONS_DIR%
    exit /b 1
)

REM Executar cada arquivo de migration em ordem
for %%f in ("%MIGRATIONS_DIR%\*.sql") do (
    echo 📄 Executando migration: %%~nxf
    
    REM Executar a migration
    set PGPASSWORD=%DB_PASSWORD%
    psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "%%f"
    
    if !errorlevel! equ 0 (
        echo ✅ Migration %%~nxf executada com sucesso
    ) else (
        echo ❌ Erro ao executar migration %%~nxf
        exit /b 1
    )
)

echo 🎉 Todas as migrations foram executadas com sucesso!
pause
