@echo off
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1" -EvidencePath "%~dp0build\evidencias"
exit /b %ERRORLEVEL%

