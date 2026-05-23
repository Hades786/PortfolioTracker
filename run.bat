@echo off
REM Portfolio Tracker Application Launcher
REM This script sets up the environment and runs the JavaFX application

setlocal enabledelayedexpansion
cd /d "%~dp0"

REM Build classpath from local lib directory
if not exist "bin" mkdir bin
if not exist "bin\PortfolioTrackerApp.class" (
    echo Compiling project...
    javac -d bin --module-path lib -cp "lib/*" src\*.java
    if errorlevel 1 (
        echo Compilation failed!
        pause
        exit /b 1
    )
)
set CLASSPATH=bin
for %%F in (lib\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%F
)

echo.
echo Portfolio Tracker Application
echo =============================
echo.
echo Java Version:
java -version
echo.
echo Starting application...
echo.

REM Run the application with JavaFX module path
java --module-path lib --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "%CLASSPATH%" PortfolioTrackerApp

REM Keep window open if there's an error
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with error code: %ERRORLEVEL%
    pause
)
