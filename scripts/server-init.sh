#!/bin/bash

# 智能点餐系统 - 服务器初始化脚本

set -e

echo "========================================"
echo "智能点餐系统 - 服务器初始化"
echo "========================================"

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    echo "错误：请使用root用户执行此脚本"
    exit 1
fi

# 更新系统
echo "1. 更新系统..."
yum update -y

# 安装基础工具
echo "2. 安装基础工具..."
yum install -y git wget curl vim unzip

# 安装Java
echo "3. 安装Java 11..."
yum install -y java-11-openjdk java-11-openjdk-devel
java -version

# 安装Maven
echo "4. 安装Maven..."
cd /opt
wget https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz
tar -xzf apache-maven-3.8.6-bin.tar.gz
mv apache-maven-3.8.6 maven
cat > /etc/profile.d/maven.sh << 'EOF'
export M2_HOME=/opt/maven
export PATH=\$M2_HOME/bin:\$PATH
EOF
source /etc/profile.d/maven.sh
mvn -version

# 安装Node.js
echo "5. 安装Node.js 18..."
curl -fsSL https://rpm.nodesource.com/setup_18.x | bash -
yum install -y nodejs
node -v
npm -v

# 安装npm淘宝镜像
echo "6. 配置npm淘宝镜像..."
npm config set registry https://registry.npm.taobao.org

# 安装sshpass
echo "7. 安装sshpass..."
yum install -y sshpass

# 创建部署目录
echo "8. 创建部署目录..."
mkdir -p /opt/smart-order-hub/backend
mkdir -p /var/www/admin
mkdir -p /var/log/smart-order-hub
mkdir -p /opt/smart-order-hub/backend/uploads

# 设置权限
echo "9. 设置目录权限..."
chown -R root:root /opt/smart-order-hub
chown -R nginx:nginx /var/www/admin
chown -R root:root /var/log/smart-order-hub

# 配置防火墙
echo "10. 配置防火墙..."
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=8081/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --reload

# 配置Nginx
echo "11. 配置Nginx..."
cat > /etc/nginx/conf.d/smart-order.conf << 'EOF'
server {
    listen 80;
    server_name 106.15.90.212;
    
    # 前端管理后台
    location /admin/ {
        alias /var/www/admin/;
        try_files $uri $uri/ /admin/index.html;
        expires 7d;
        add_header Cache-Control "public, immutable";
    }
    
    # 后端API
    location /api/ {
        proxy_pass http://localhost:8081/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 300s;
        proxy_send_timeout 300s;
        proxy_read_timeout 300s;
    }
    
    # 文件上传
    location /uploads/ {
        alias /opt/smart-order-hub/backend/uploads/;
        client_max_body_size 50M;
    }
    
    # Gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml+rss text/javascript;
}
EOF

# 测试并重启Nginx
echo "12. 测试并重启Nginx..."
nginx -t && systemctl restart nginx
systemctl enable nginx

echo "========================================"
echo "服务器初始化完成！"
echo "========================================"
echo ""
echo "接下来请："
echo "1. 在Jenkins中配置凭据"
echo "2. 创建Jenkins任务"
echo "3. 触发构建"
echo ""
echo "服务器信息："
echo "Java版本：$(java -version 2>&1 | head -n 1)"
echo "Maven版本：$(mvn -version 2>&1 | head -n 1)"
echo "Node.js版本：$(node -v)"
echo "Nginx状态：$(systemctl is-active nginx)"
echo ""
