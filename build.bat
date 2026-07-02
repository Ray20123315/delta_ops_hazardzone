@echo off
chcp 65001 >nul
title Delta Force Mod Builder
color 0A

echo ========================================
echo   Delta Force Mod Build Tool
echo ========================================
echo.

:: 1. Check Java Environment
where java >nul 2>&1
if %errorlevel% neq 0 goto ERROR_NO_JAVA

:: 2. Check Gradle Wrapper
if not exist gradlew.bat goto GENERATE_WRAPPER

:START_BUILD
echo [1/4] Cleaning old build files...
call gradlew clean --no-daemon
echo.

echo [2/4] Building Mod JAR...
call gradlew build --no-daemon
if %errorlevel% neq 0 goto ERROR_BUILD_FAILED
echo.

echo [3/4] Copying JAR to Desktop...
for /f "tokens=*" %%f in ('dir /b /s build\libs\*.jar 2^>nul') do (
    copy "%%f" "%USERPROFILE%\Desktop\delta_ops-1.0.0.jar" /y >nul
)
echo [SUCCESS] JAR copied to Desktop!
echo.

echo [4/4] Done!
echo ========================================
echo   BUILD SUCCESSFUL
echo ========================================
pause
exit /b 0


:: ========================================
:: ERROR HANDLING LABELS (GOTO TARGETS)
:: ========================================

:GENERATE_WRAPPER
echo [INFO] gradlew.bat not found, generating...
call gradle wrapper --gradle-version 8.1.1
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