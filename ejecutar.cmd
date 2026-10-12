@echo off
setlocal
cd /d "%~dp0"
if not exist "dist\cu6-comprar-producto.jar" (
  call build.cmd
  if errorlevel 1 exit /b 1
)
java -jar "dist\cu6-comprar-producto.jar" %*

