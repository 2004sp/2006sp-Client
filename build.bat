@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

set "CHECK_ONLY=0"
if /i "%~1"=="--check" (
    set "CHECK_ONLY=1"
) else if not "%~1"=="" (
    echo Usage: build.bat [--check]
    exit /b 1
)

set "SOURCE_DIR=src\main\java"
set "OUTPUT_DIR=build"
set "CLASSES_DIR=%OUTPUT_DIR%\classes"
set "SEMANTIC_CLASSES=%OUTPUT_DIR%\semantic-classes"
set "TEMP_CLASSES=%OUTPUT_DIR%\file-classes"
set "EMPTY_SOURCEPATH=%OUTPUT_DIR%\empty-sourcepath"
set "PACKAGE_DIR=%OUTPUT_DIR%\package-staging"
set "CLEAN_MANIFEST=%OUTPUT_DIR%\clean-manifest.mf"
set "OUTPUT_JAR=%OUTPUT_DIR%\Client.jar"
set "BASE_JAR=lib\client-runtime.jar"
set "THEME_JAR=lib\theme.jar"
set "LWJGL_JAR=lib\lwjgl-2.9.3.jar"
set "LWJGL_NATIVES_JAR=lib\lwjgl-platform-2.9.3-natives-windows.jar"
set "LWJGL_NATIVES_DIR=runtime\natives"
set "SOURCE_LIST=%OUTPUT_DIR%\override-sources.txt"
set "FAILED_LIST=%OUTPUT_DIR%\compile-failures.txt"
set "COMPILE_LOG=%OUTPUT_DIR%\compile-errors.txt"
set "LAST_COMPILE_LOG=%OUTPUT_DIR%\last-javac.txt"
set "PENDING_LIST=%OUTPUT_DIR%\pending-sources.txt"
set "NEXT_PENDING_LIST=%OUTPUT_DIR%\next-pending-sources.txt"
set "GROUP_SOURCE_LIST=%OUTPUT_DIR%\group-sources.txt"
set "PASS_LOG=%OUTPUT_DIR%\pass-errors.txt"
set "GROUP_LOG=%OUTPUT_DIR%\group-errors.txt"

where javac >nul 2>nul
if errorlevel 1 (
    echo ERROR: javac was not found on PATH.
    echo Install a JDK, preferably JDK 8 for this client, and make sure javac is on PATH.
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)

if "%CHECK_ONLY%"=="0" where jar >nul 2>nul
if "%CHECK_ONLY%"=="0" if errorlevel 1 (
    echo ERROR: jar was not found on PATH.
    echo Install a JDK, preferably JDK 8 for this client, and make sure jar is on PATH.
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)

if "%CHECK_ONLY%"=="0" if not exist "%BASE_JAR%" (
    echo ERROR: Preserved runtime JAR is missing: %BASE_JAR%
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)

if not exist "%SOURCE_DIR%" (
    echo ERROR: Source directory is missing: %SOURCE_DIR%
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)

if not exist "%LWJGL_JAR%" (
    call :download_file "%LWJGL_JAR%" "https://repo1.maven.org/maven2/org/lwjgl/lwjgl/lwjgl/2.9.3/lwjgl-2.9.3.jar"
    if errorlevel 1 (
        echo ERROR: Could not download LWJGL 2.9.3.
        if "%CHECK_ONLY%"=="0" pause
        exit /b 1
    )
)

if "%CHECK_ONLY%"=="0" if not exist "%LWJGL_NATIVES_JAR%" (
    call :download_file "%LWJGL_NATIVES_JAR%" "https://repo1.maven.org/maven2/org/lwjgl/lwjgl/lwjgl-platform/2.9.3/lwjgl-platform-2.9.3-natives-windows.jar"
    if errorlevel 1 (
        echo ERROR: Could not download LWJGL Windows natives.
        if "%CHECK_ONLY%"=="0" pause
        exit /b 1
    )
)

if "%CHECK_ONLY%"=="0" if not exist "%LWJGL_NATIVES_DIR%" mkdir "%LWJGL_NATIVES_DIR%"
if "%CHECK_ONLY%"=="0" if not exist "%LWJGL_NATIVES_DIR%\lwjgl.dll" if not exist "%LWJGL_NATIVES_DIR%\lwjgl64.dll" (
    echo Extracting LWJGL native libraries...
    pushd "%LWJGL_NATIVES_DIR%"
    jar xf "..\..\%LWJGL_NATIVES_JAR%"
    set "NATIVE_EXTRACT_ERROR=!ERRORLEVEL!"
    popd
    if not "!NATIVE_EXTRACT_ERROR!"=="0" (
        echo ERROR: Could not extract LWJGL native libraries.
        if "%CHECK_ONLY%"=="0" pause
        exit /b 1
    )
)

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
if not exist "%SEMANTIC_CLASSES%" mkdir "%SEMANTIC_CLASSES%"
rem Preserve previously successful recovered classes as compile-time dependencies only.
rem They are NOT automatically overlaid into the output JAR on this build.
if exist "%CLASSES_DIR%" xcopy /e /i /y /q "%CLASSES_DIR%\*" "%SEMANTIC_CLASSES%\" >nul 2>nul
if exist "%CLASSES_DIR%" rmdir /s /q "%CLASSES_DIR%"
if exist "%TEMP_CLASSES%" rmdir /s /q "%TEMP_CLASSES%"
if exist "%EMPTY_SOURCEPATH%" rmdir /s /q "%EMPTY_SOURCEPATH%"
if exist "%PACKAGE_DIR%" rmdir /s /q "%PACKAGE_DIR%"
mkdir "%CLASSES_DIR%"
mkdir "%TEMP_CLASSES%"
mkdir "%EMPTY_SOURCEPATH%"
> "%SOURCE_LIST%" type nul
> "%FAILED_LIST%" type nul
> "%COMPILE_LOG%" type nul
> "%PENDING_LIST%" type nul
> "%GROUP_LOG%" type nul
if exist "%NEXT_PENDING_LIST%" del /q "%NEXT_PENDING_LIST%"
if exist "%LAST_COMPILE_LOG%" del /q "%LAST_COMPILE_LOG%"

rem Always compile every Java source in the complete recovered source tree.
for /r "%SOURCE_DIR%" %%F in (*.java) do echo %%F>>"%SOURCE_LIST%"

for /f "tokens=2" %%V in ('javac -version 2^>^&1') do set "JAVAC_VERSION=%%V"
echo Compiling source overrides with javac %JAVAC_VERSION%...
echo Base runtime: %BASE_JAR%
echo.

set /a TOTAL=0
set /a SUCCESS=0
set /a FAILED=0

for /f "usebackq delims=" %%F in ("%SOURCE_LIST%") do (
    if exist "%%F" set /a TOTAL+=1
)

if %TOTAL% EQU 0 goto compile_complete

rem First try the selected source set as one javac invocation. This is important for
rem recovered/decompiled code because many renamed classes reference each other in
rem cycles (for example Client <-> Actor/AnimationSequence). Compiling them together
rem lets javac resolve the recovered source definitions instead of the obfuscated
rem member names in the preserved runtime JAR.
> "%GROUP_SOURCE_LIST%" type nul
for /f "usebackq delims=" %%F in ("%SOURCE_LIST%") do (
    if exist "%%F" (
        set "GROUP_ARG=%%F"
        set "GROUP_ARG=!GROUP_ARG:\=/!"
        echo "!GROUP_ARG!">>"%GROUP_SOURCE_LIST%"
    )
)

if exist "%TEMP_CLASSES%" rmdir /s /q "%TEMP_CLASSES%"
mkdir "%TEMP_CLASSES%"
echo Attempting grouped source compilation...
javac -encoding UTF-8 -source 8 -target 8 -Xmaxerrs 2000 -implicit:none -cp "%THEME_JAR%;%LWJGL_JAR%" -d "%TEMP_CLASSES%" @"%GROUP_SOURCE_LIST%" > "%GROUP_LOG%" 2>&1
if not errorlevel 1 (
    xcopy /e /i /y /q "%TEMP_CLASSES%\*" "%CLASSES_DIR%\" >nul 2>nul
    xcopy /e /i /y /q "%TEMP_CLASSES%\*" "%SEMANTIC_CLASSES%\" >nul 2>nul
    set /a SUCCESS=TOTAL
    echo Grouped source compilation succeeded.
    goto compile_complete
)

echo Grouped compilation still has damaged sources; switching to progressive recovery passes.
echo.
copy /y "%SOURCE_LIST%" "%PENDING_LIST%" >nul
set /a PASS=0

:compile_pass
set /a PASS+=1
set /a PASS_SUCCESS=0
set /a PASS_INDEX=0
> "%NEXT_PENDING_LIST%" type nul
> "%PASS_LOG%" type nul

echo Progressive compile pass !PASS!...
for /f "usebackq delims=" %%F in ("%PENDING_LIST%") do (
    if exist "%%F" (
        set /a PASS_INDEX+=1
        echo [pass !PASS! - !PASS_INDEX!] %%F

        if exist "%TEMP_CLASSES%" rmdir /s /q "%TEMP_CLASSES%"
        mkdir "%TEMP_CLASSES%"

        rem Prefer already recovered semantic classes over the preserved runtime JAR.
        javac -encoding UTF-8 -source 8 -target 8 -Xmaxerrs 2000 -implicit:none -sourcepath "%EMPTY_SOURCEPATH%" -cp "%CLASSES_DIR%;%SEMANTIC_CLASSES%;%THEME_JAR%;%LWJGL_JAR%" -d "%TEMP_CLASSES%" "%%F" > "%LAST_COMPILE_LOG%" 2>&1
        if errorlevel 1 (
            echo %%F>>"%NEXT_PENDING_LIST%"
            >>"%PASS_LOG%" echo ============================================================
            >>"%PASS_LOG%" echo %%F
            >>"%PASS_LOG%" echo ============================================================
            type "%LAST_COMPILE_LOG%" >>"%PASS_LOG%"
            >>"%PASS_LOG%" echo.
            echo   pending
        ) else (
            set /a SUCCESS+=1
            set /a PASS_SUCCESS+=1
            xcopy /e /i /y /q "%TEMP_CLASSES%\*" "%CLASSES_DIR%\" >nul 2>nul
    xcopy /e /i /y /q "%TEMP_CLASSES%\*" "%SEMANTIC_CLASSES%\" >nul 2>nul
            echo   OK - source override compiled.
        )
    )
)

echo.
if !PASS_SUCCESS! GTR 0 (
    rem Before another per-file pass, try all unresolved files together. This breaks
    rem circular dependency groups once their simpler dependencies have compiled.
    > "%GROUP_SOURCE_LIST%" type nul
    set /a GROUP_COUNT=0
    for /f "usebackq delims=" %%F in ("%NEXT_PENDING_LIST%") do (
        if exist "%%F" (
            set "GROUP_ARG=%%F"
            set "GROUP_ARG=!GROUP_ARG:\=/!"
            echo "!GROUP_ARG!">>"%GROUP_SOURCE_LIST%"
            set /a GROUP_COUNT+=1
        )
    )

    if !GROUP_COUNT! EQU 0 goto compile_complete

    if exist "%TEMP_CLASSES%" rmdir /s /q "%TEMP_CLASSES%"
    mkdir "%TEMP_CLASSES%"
    echo Retrying !GROUP_COUNT! unresolved files as a group...
    javac -encoding UTF-8 -source 8 -target 8 -Xmaxerrs 2000 -implicit:none -cp "%CLASSES_DIR%;%SEMANTIC_CLASSES%;%THEME_JAR%;%LWJGL_JAR%" -d "%TEMP_CLASSES%" @"%GROUP_SOURCE_LIST%" > "%GROUP_LOG%" 2>&1
    if not errorlevel 1 (
        xcopy /e /i /y /q "%TEMP_CLASSES%\*" "%CLASSES_DIR%\" >nul 2>nul
    xcopy /e /i /y /q "%TEMP_CLASSES%\*" "%SEMANTIC_CLASSES%\" >nul 2>nul
        set /a SUCCESS+=GROUP_COUNT
        echo Grouped retry succeeded.
        goto compile_complete
    )

    copy /y "%NEXT_PENDING_LIST%" "%PENDING_LIST%" >nul
    echo.
    goto compile_pass
)

rem No source compiled on this pass, so the remaining files have genuine compiler
rem problems (or a dependency cycle containing one). Keep only these final errors.
copy /y "%NEXT_PENDING_LIST%" "%FAILED_LIST%" >nul
copy /y "%PASS_LOG%" "%COMPILE_LOG%" >nul
>>"%COMPILE_LOG%" echo ============================================================
>>"%COMPILE_LOG%" echo GROUPED DIAGNOSTICS FOR REMAINING SOURCES
>>"%COMPILE_LOG%" echo ============================================================
type "%GROUP_LOG%" >>"%COMPILE_LOG%"
for /f "usebackq delims=" %%F in ("%FAILED_LIST%") do set /a FAILED+=1

:compile_complete

echo.
echo Source files considered: %TOTAL%
echo Source overrides compiled: %SUCCESS%
echo Source files with compiler errors: %FAILED%
if %FAILED% GTR 0 (
    echo Compiler diagnostics: %COMPILE_LOG%
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)
if %TOTAL% EQU 0 (
    echo ERROR: No Java source files were found under %SOURCE_DIR%.
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)
if "%CHECK_ONLY%"=="1" (
    echo Source check passed.
    exit /b 0
)

rem Build a clean artifact. The preserved runtime JAR contributes only the
rem Eclipse jar-in-jar launcher, its manifest and the embedded theme JAR.
rem Original obfuscated client/a/b classes are deliberately excluded.
mkdir "%PACKAGE_DIR%"
pushd "%PACKAGE_DIR%"
jar xf "..\..\%BASE_JAR%"
if errorlevel 1 (
    popd
    echo ERROR: Failed to extract packaging resources from %BASE_JAR%.
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)
popd
copy /y "%PACKAGE_DIR%\META-INF\MANIFEST.MF" "%CLEAN_MANIFEST%" >nul
pushd "%PACKAGE_DIR%"
jar xf "..\..\%LWJGL_JAR%"
if errorlevel 1 (
    popd
    echo ERROR: Failed to package LWJGL classes into %OUTPUT_JAR%.
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)
popd
if exist "%PACKAGE_DIR%\client" rmdir /s /q "%PACKAGE_DIR%\client"
if exist "%PACKAGE_DIR%\a" rmdir /s /q "%PACKAGE_DIR%\a"
if exist "%PACKAGE_DIR%\b" rmdir /s /q "%PACKAGE_DIR%\b"
xcopy /e /i /y /q "%CLASSES_DIR%\*" "%PACKAGE_DIR%\" >nul 2>nul
if exist "%PACKAGE_DIR%\META-INF" rmdir /s /q "%PACKAGE_DIR%\META-INF"
if exist "%OUTPUT_JAR%" del /q "%OUTPUT_JAR%"
jar cfm "%OUTPUT_JAR%" "%CLEAN_MANIFEST%" -C "%PACKAGE_DIR%" .
if errorlevel 1 (
    echo ERROR: Failed to create clean source-built %OUTPUT_JAR%.
    if "%CHECK_ONLY%"=="0" pause
    exit /b 1
)

echo.
echo Built %OUTPUT_JAR%.
echo Every Java source under %SOURCE_DIR% was included in this build.
if "%CHECK_ONLY%"=="0" pause
exit /b 0

:download_file
echo Downloading %~nx1...
powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object Net.WebClient).DownloadFile('%~2', '%~f1')"
if errorlevel 1 (
    if exist "%~1" del /q "%~1"
    exit /b 1
)
if not exist "%~1" exit /b 1
exit /b 0
