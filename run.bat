@echo off
:: 設定編碼防止亂碼
chcp 65001 > nul

echo ====================================================
echo   Loading COLLECT ALL...
echo ====================================================

:: 執行遊戲程式
java -jar out/artifacts/Intro_to_CS_Lab_Final_Project_jar/Intro_to_CS_Lab_Final_Project.jar

if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Please check the error message!
    pause
)