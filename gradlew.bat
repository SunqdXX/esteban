@echo off
setlocal
set "DIR=%~dp0"
set "JAVA_EXE=java.exe"
if defined JAVA_HOME set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
"%JAVA_EXE%" -version >nul 2>&1 || (echo u need java 17 or newer, install it or set JAVA_HOME & exit /b 1)
"%JAVA_EXE%" -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew -jar "%DIR%gradle\wrapper\gradle-wrapper.jar" %*
exit /b %ERRORLEVEL%
