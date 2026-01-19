#!/bin/bash

APP_NAME="smart-order-hub"
JAR_NAME="smart-order-hub-1.0.0.jar"
PORT=8081

echo "========================================"
echo "启动 $APP_NAME"
echo "========================================"

# 检查jar包是否存在
if [ ! -f "$JAR_NAME" ]; then
    echo "错误: jar包不存在: $JAR_NAME"
    exit 1
fi

# 检查端口是否被占用
PID=$(lsof -ti:$PORT)
if [ -n "$PID" ]; then
    echo "警告: 端口 $PORT 已被进程 $PID 占用"
    echo "正在停止旧进程..."
    kill -9 $PID
    sleep 2
fi

# 启动应用
echo "正在启动应用..."
nohup java -jar $JAR_NAME --spring.profiles.active=prod > app.log 2>&1 &

# 等待应用启动
sleep 10

# 检查应用是否启动成功
NEW_PID=$(lsof -ti:$PORT)
if [ -n "$NEW_PID" ]; then
    echo "========================================"
    echo "应用启动成功！"
    echo "进程ID: $NEW_PID"
    echo "端口: $PORT"
    echo "日志文件: app.log"
    echo "========================================"
    echo ""
    echo "查看日志: tail -f app.log"
    echo "停止应用: ./stop.sh"
else
    echo "========================================"
    echo "应用启动失败！"
    echo "请查看日志: tail -f app.log"
    echo "========================================"
    exit 1
fi
