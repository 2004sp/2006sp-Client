@echo off
setlocal
cd /d "%~dp0"

rem Promote a freshly built replacement after the previous client releases Client.jar.
if exist "%~dp0build\Client-hunterfix.jar" move /y "%~dp0build\Client-hunterfix.jar" "%~dp0build\Client.jar" >nul
if exist "%~dp0build\Client-totallevelfix.jar" move /y "%~dp0build\Client-totallevelfix.jar" "%~dp0build\Client.jar" >nul

if not "%~1"=="" (
    echo Usage: run.bat
    exit /b 1
)

where java >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java is not installed or is not available on PATH.
    exit /b 1
)

if not exist "%~dp0build\Client.jar" (
    echo ERROR: build\Client.jar was not found. Run build.bat first.
    exit /b 1
)

if not exist "%~dp0runtime\cache\main_file_cache.dat2" (
    echo ERROR: The revision 443 cache is missing from runtime\cache.
    exit /b 1
)

pushd "%~dp0runtime"
echo Using revision 443 cache: %~dp0runtime\cache
java -Xms256m -Xmx1024m -Djava.library.path="%~dp0runtime\natives" -Dprs.clientRevision=443 "-Dprs.cache443=%~dp0runtime\cache" -jar "..\build\Client.jar"
set "EXIT_CODE=%ERRORLEVEL%"
popd

if not "%EXIT_CODE%"=="0" echo Client exited with code %EXIT_CODE%.
exit /b %EXIT_CODE%
