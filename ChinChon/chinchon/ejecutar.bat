@echo off
chcp 65001 >nul
echo ========================================
echo    JUEGO CHIN-CHON
echo ========================================
echo.
cd /d "%~dp0"
java -cp . chinchon.Main
if errorlevel 1 (
    echo.
    echo Error al ejecutar el juego.
    echo Asegurese de que Java este instalado correctamente.
    pause
)

