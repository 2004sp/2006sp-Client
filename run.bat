@echo off
setlocal
cd /d "%~dp0"

where java >nul 2>nul
if errorlevel 1 (
    echo ERROR: Java is not installed or is not available on PATH.
    exit /b 1
)

if not exist "%~dp0build\Client.jar" (
    echo ERROR: build\Client.jar was not found. Run build.bat first.
    exit /b 1
)

pushd "%~dp0runtime"
echo Preparing lossless tiled control-panel map...
java -Xms128m -Xmx768m -cp "..\build\Client.jar" worldmap.WorldMapTileExporter ".\cache"
if errorlevel 1 echo WARNING: Control-panel map export failed; the client will still start.
java -Xms256m -Xmx1024m -Djava.library.path="%~dp0runtime\natives" -jar "..\build\Client.jar"
set "EXIT_CODE=%ERRORLEVEL%"
popd

if not "%EXIT_CODE%"=="0" echo Client exited with code %EXIT_CODE%.
exit /b %EXIT_CODE%
