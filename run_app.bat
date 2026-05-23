@echo off
REM Portfolio Tracker - Complete Launcher
REM Handles JavaFX setup and application launch

setlocal enabledelayedexpansion

cls
echo.
echo ===============================================
echo   Portfolio Tracker - Launching Application
echo ===============================================
echo.

cd /d "%~dp0"

REM Check for compiled classes
if not exist "bin\PortfolioTrackerApp.class" (
    echo Building project...
    python build_and_run.py
    exit /b %ERRORLEVEL%
)

REM Extract native libraries from JAR files
set NATIVE_DIR=native
if not exist "%NATIVE_DIR%" mkdir "%NATIVE_DIR%"

echo Extracting native libraries...
for %%F in (lib\*-win.jar) do (
    echo   - Extracting from %%~nxF
    powershell -NoProfile -Command "Add-Type -AssemblyName System.IO.Compression.FileSystem; $zip=[System.IO.Compression.ZipFile]::OpenRead('%%F'); foreach($entry in $zip.Entries) { if($entry.Name -like '*.dll') { [System.IO.Compression.ZipFileExtensions]::ExtractToFile($entry, '%NATIVE_DIR%\\' + $entry.Name, $true) } }; $zip.Dispose()"
)

REM Build runtime classpath
set CLASSPATH=bin
for %%F in (lib\*.jar) do (
    set CLASSPATH=!CLASSPATH!;%%F
)

echo.
echo Launching JavaFX Application...
echo Java Version:
java -version
echo.

REM Attempt 1: Standard JavaFX launch
java --module-path lib --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "%CLASSPATH%" PortfolioTrackerApp

REM If that fails, try with extracted native library path
if %ERRORLEVEL% neq 0 (
    echo Attempting alternative launch method...
    java --module-path lib --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -Djava.library.path="%NATIVE_DIR%" -cp "%CLASSPATH%" PortfolioTrackerApp
)

if %ERRORLEVEL% neq 0 (
    echo.
    echo NOTICE: JavaFX desktop environment not available
    echo Please ensure you have a proper display/windowing system
    echo.
    pause
)

endlocal
