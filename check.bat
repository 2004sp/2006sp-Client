@echo off
setlocal EnableExtensions
cd /d "%~dp0"

call build.bat --check
if errorlevel 1 exit /b 1

if not exist "build\test-classes" mkdir "build\test-classes"
javac -encoding UTF-8 -source 8 -target 8 -cp "build\classes;lib\theme.jar;lib\lwjgl-2.9.3.jar" -d "build\test-classes" "src\test\java\client\ClientSmokeTest.java"
if errorlevel 1 exit /b 1

java -cp "build\test-classes;build\classes;lib\theme.jar;lib\lwjgl-2.9.3.jar" client.ClientSmokeTest
exit /b %ERRORLEVEL%
