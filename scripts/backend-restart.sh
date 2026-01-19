#!/bin/bash

APP_NAME="smart-order-hub"
JAR_NAME="smart-order-hub-1.0.0.jar"
PORT=8081

echo "========================================"
echo "重启 $APP_NAME"
echo "========================================"

# 停止应用
./stop.sh

# 等待2秒
sleep 2

# 启动应用
./start.sh

echo "========================================"
echo "重启完成"
echo "========================================"
