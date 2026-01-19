@echo off
echo ========================================
echo 前端管理后台 - 生产环境打包
echo ========================================
echo.
echo 正在打包生产环境...
echo.
cd pc-admin
call npm run build
echo.
if %ERRORLEVEL% EQU 0 (
    echo ========================================
    echo 打包成功！
    echo ========================================
    echo dist目录位置: pc-admin\dist
    echo.
    echo 部署说明:
    echo 1. 将pc-admin\dist目录下的所有文件上传到服务器
    echo 2. 上传到服务器的 /var/www/admin 目录
    echo 3. 配置Nginx指向该目录
    echo.
) else (
    echo ========================================
    echo 打包失败！
    echo ========================================
)
cd ..
pause
