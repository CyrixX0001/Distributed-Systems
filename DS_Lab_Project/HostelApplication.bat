@echo off
title Hostel App Launcher
color 0B

echo ==================================================
echo   CLEANING UP OLD PROCESSES...
echo ==================================================
:: 1. Kill any existing Java instances so we don't have "Ghost Servers"
taskkill /F /IM java.exe >nul 2>&1
taskkill /F /IM javaw.exe >nul 2>&1

echo.
echo ==================================================
echo   COMPILING (Just to be safe)...
echo ==================================================
cd src
javac final_project/server/*.java final_project/client/*.java final_project/client/panels/*.java final_project/client/shared/*.java

if %errorlevel% neq 0 (
    echo [ERROR] Compilation Failed.
    pause
    exit /b
)

echo.
echo ==================================================
echo   STARTING APP (Server is Minimized)
echo ==================================================

:: 2. Start Server Minimized (User won't see the black box unless they click it)
:: We use 'start /MIN' so it stays out of your face.
start /MIN "Hostel Server (Do Not Close)" java final_project.server.HostelServer

:: Wait 3 seconds for server to boot
timeout /t 3 >nul

:: 3. Start Client and WAIT. 
:: The script pauses here until you close the Client window.
echo App is running. Closing the App window will stop everything.
java final_project.client.HostelClientApp

:: 4. Cleanup Trigger
echo.
echo ==================================================
echo   SHUTTING DOWN SYSTEM...
echo ==================================================
taskkill /F /IM java.exe >nul 2>&1
echo Done.