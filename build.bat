@echo off
chcp 65001 >nul
title Delta Force Mod Builder
color 0A

echo ========================================
echo   Delta Force Mod Build Tool
echo ========================================
echo.

:: Switch to ASCII-only project path (avoids JNI percent-encoded path decoding errors)
if /i "%CD%" neq "C:\Users\ray20\Desktop\delta_ops_hazardzone" (
    echo [INFO] Switching to ASCII-only project path...
    cd /d "C:\Users\ray20\Desktop\delta_ops_hazardzone"
)
echo Project Path: %CD%
echo.

:: 1. Check Java Environment
where java >nul 2>&1
if %errorlevel% neq 0 goto ERROR_NO_JAVA

:: 2. Check Gradle Wrapper
if not exist gradlew.bat goto GENERATE_WRAPPER

:START_BUILD
echo [1/4] Cleaning old build files...
call gradlew clean --no-daemon
if %errorlevel% neq 0 goto ERROR_BUILD_FAILED
echo.

echo [2/4] Building Mod JAR...
call gradlew build --no-daemon
if %errorlevel% neq 0 goto ERROR_BUILD_FAILED
echo.

:: Find the most recently built JAR
echo [3/4] Copying JAR to Desktop...
set JAR_FILE=
for /f "tokens=*" %%f in ('dir /b /s build\libs\*.jar 2^>nul') do set JAR_FILE=%%f
if defined JAR_FILE (
    copy "%JAR_FILE%" "%USERPROFILE%\Desktop\delta_ops-1.0.0.jar" /y >nul
    echo [SUCCESS] JAR copied to Desktop!
) else (
    echo [WARNING] No JAR found in build/libs.
)
echo.

echo [4/4] Done!
echo ========================================
echo   BUILD SUCCESSFUL
echo ========================================
echo   Output: %JAR_FILE%
echo ========================================
pause
exit /b 0

:: ========================================
:: ERROR HANDLING LABELS
:: ========================================

:GENERATE_WRAPPER
echo [INFO] gradlew.bat not found, generating...
call gradle wrapper --gradle-version 8.8
if %errorlevel% neq 0 goto ERROR_GRADLE_FAILED
goto START_BUILD

:ERROR_NO_JAVA
echo [ERROR] Java not found! Please install Java 17 and set PATH.
pause
exit /b 1

:ERROR_GRADLE_FAILED
echo [ERROR] Failed to generate Gradle wrapper!
pause
exit /b 1

:ERROR_BUILD_FAILED
echo [ERROR] Build failed! Check errors above.
pause
exit /b 1