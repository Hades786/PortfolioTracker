@echo off
REM Portfolio Tracker - Advanced Launcher
REM This script properly configures JavaFX and launches the application

setlocal enabledelayedexpansion

REM Set working directory
cd /d "%~dp0"

REM Check if compiled classes exist
if not exist "bin\PortfolioTrackerApp.class" (
    echo Compiling project...
    call :compile_project
    if errorlevel 1 (
        echo Compilation failed!
        pause
        exit /b 1
    )
)

REM Build classpath - include all JARs from lib directory
set CLASSPATH=bin
for /r lib %%F in (*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%F
)

echo.
echo ============================================
echo   Portfolio Tracker Application
echo ============================================
echo.
echo Java Version:
java -version
echo.
echo Classpath: %CLASSPATH:~0,100%...
echo.
echo Starting application...
echo (JavaFX window should appear in a few seconds)
echo.

REM Run the application with proper JavaFX configuration
java --module-path lib --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "%CLASSPATH%" PortfolioTrackerApp

REM Handle exit code
if %ERRORLEVEL% neq 0 (
    echo.
    echo Application exited with error code: %ERRORLEVEL%
    pause
)

endlocal
exit /b %ERRORLEVEL%

:compile_project
echo Compiling Java files...
if not exist "bin" mkdir bin

REM Build compilation classpath from local lib directory
set COMPILE_CP=bin
for /r lib %%F in (*.jar) do (
    set COMPILE_CP=!COMPILE_CP!;%%F
)

echo Compilation classpath: %COMPILE_CP:~0,100%...
javac -d bin -cp "%COMPILE_CP%" src\*.java
exit /b %ERRORLEVEL%
