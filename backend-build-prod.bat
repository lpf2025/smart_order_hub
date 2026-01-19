@echo off
echo ========================================
echo 后端 - 生产环境打包
echo ========================================
echo.
echo 正在打包生产环境jar包...
echo.
cd backend
call mvn clean package -DskipTests
echo.
if %ERRORLEVEL% EQU 0 (
    echo ========================================
    echo 打包成功！
    echo ========================================
    echo jar包位置: backend\target\smart-order-hub-1.0.0.jar
    echo.
    echo 启动命令:
    echo java -jar target\smart-order-hub-1.0.0.jar --spring.profiles.active=prod
    echo.
) else (
    echo ========================================
    echo 打包失败！
    echo ========================================
)
cd ..
pause
