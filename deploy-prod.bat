@echo off
chcp 65001 >nul
cd /d %~dp0

echo ========================================
echo Deploy to Production Server
echo ========================================

echo 1. Uploading backend jar...
scp backend\target\smart-order-hub-1.0.0.jar root@106.15.90.212:/opt/smart-order-hub/backend/

echo 2. Uploading frontend files...
scp -r pc-admin\dist\* root@106.15.90.212:/var/www/admin/

echo 3. Uploading startup scripts...
scp scripts\backend-start.sh root@106.15.90.212:/opt/smart-order-hub/backend/start.sh
scp scripts\backend-stop.sh root@106.15.90.212:/opt/smart-order-hub/backend/stop.sh

echo 4. Uploading Nginx config...
scp scripts\nginx-prod.conf root@106.15.90.212:/etc/nginx/conf.d/smart-order-hub.conf

echo ========================================
echo Upload completed!
echo Please SSH to server and run:
echo ========================================
echo ssh root@106.15.90.212
echo chmod +x /opt/smart-order-hub/backend/start.sh
echo chmod +x /opt/smart-order-hub/backend/stop.sh
echo nginx -t
echo systemctl reload nginx
echo cd /opt/smart-order-hub/backend
echo ./start.sh
echo ========================================

pause
