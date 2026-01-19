#!/bin/bash

APP_NAME="smart-order-hub"
PORT=8081

echo "========================================"
echo "停止 $APP_NAME"
echo "========================================"

# 查找进程
PID=$(lsof -ti:$PORT)

if [ -z "$PID" ]; then
    echo "应用未运行"
    exit 0
fi

echo "正在停止进程 $PID..."

# 优雅停止
kill $PID

# 等待进程结束
for i in {1..10}; do
    if ! lsof -ti:$PORT > /dev/null 2>&1; then
        echo "应用已停止"
        exit 0
    fi
    sleep 1
    echo "等待进程结束... ($i/10)"
done

# 强制停止
echo "强制停止进程..."
kill -9 $PID

sleep 2

if ! lsof -ti:$PORT > /dev/null 2>&1; then
    echo "========================================"
    echo "应用已停止"
    echo "========================================"
else
    echo "========================================"
    echo "停止失败！"
    echo "========================================"
    exit 1
fi
