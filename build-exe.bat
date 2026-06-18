@echo off
setlocal
chcp 65001 >nul

set DIST_DIR=%~dp0target\dist

echo ============================================
echo  Construyendo HiperTacoBrons.exe
echo ============================================

echo.
echo [1/2] Compilando proyecto...
call mvnw package
if errorlevel 1 (
    echo ERROR: Fallo la compilacion.
    pause
    exit /b 1
)

echo.
echo [2/2] Generando ejecutable con jpackage...

if not defined JAVA_HOME (
    echo ERROR: Variable JAVA_HOME no definida.
    echo Establecela en: C:\Program Files\Java\openlogic-openjdk-17.0.14+7-windows-x64
    pause
    exit /b 1
)

set "JMOD_PATH=%JAVA_HOME%\jmods"
if not exist "%JMOD_PATH%" (
    echo ERROR: No se encontraron jmods en %JMOD_PATH%
    pause
    exit /b 1
)

echo JAVA_HOME: %JAVA_HOME%
echo JMOD_PATH: %JMOD_PATH%

if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"

jpackage --type app-image ^
    --input "%~dp0target" ^
    --main-jar "HTB-1.0-SNAPSHOT.jar" ^
    --module-path "%JMOD_PATH%" ^
    --add-modules javafx.controls,javafx.fxml ^
    --name "HiperTacoBrons" ^
    --app-version "1.0" ^
    --vendor "HTB" ^
    --dest "%DIST_DIR%"

if errorlevel 1 (
    echo.
    echo ERROR: jpackage fallo.
    pause
    exit /b 1
)

echo.
echo ============================================
echo  LISTO! Ejecutable creado en:
echo  %DIST_DIR%\HiperTacoBrons\HiperTacoBrons.exe
echo ============================================
pause
