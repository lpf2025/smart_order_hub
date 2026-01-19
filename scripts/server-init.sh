#!/bin/bash

echo "========================================"
echo "智能点餐系统 - 服务器初始化脚本"
echo "========================================"
echo ""

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then
    echo "请使用root用户运行此脚本"
    exit 1
fi

# 更新系统
echo "1. 更新系统..."
yum update -y

# 安装Java 11
echo "2. 安装Java 11..."
yum install -y java-11-openjdk java-11-openjdk-devel

# 安装Maven 3.8
echo "3. 安装Maven 3.8..."
cd /opt
wget https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz
tar -xzf apache-maven-3.8.6-bin.tar.gz
mv apache-maven-3.8.6 maven
rm apache-maven-3.8.6-bin.tar.gz

# 配置Maven环境变量
echo 'export M2_HOME=/opt/maven' >> /etc/profile
echo 'export PATH=$M2_HOME/bin:$PATH' >> /etc/profile
source /etc/profile

# 安装Node.js 18
echo "4. 安装Node.js 18..."
curl -fsSL https://rpm.nodesource.com/setup_18.x | bash -
yum install -y nodejs

# 配置npm淘宝镜像
echo "5. 配置npm淘宝镜像..."
npm config set registry https://registry.npmmirror.com

# 安装sshpass
echo "6. 安装sshpass..."
yum install -y sshpass

# 创建部署目录
echo "7. 创建部署目录..."
mkdir -p /opt/smart-order-hub/backend
mkdir -p /var/www/admin
mkdir -p /var/log/smart-order-hub

# 配置防火墙
echo "8. 配置防火墙..."
firewall-cmd --permanent --add-port=8081/tcp
firewall-cmd --permanent --add-port=8082/tcp
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --reload

# 配置Nginx
echo "9. 配置Nginx..."
if ! command -v nginx &> /dev/null; then
    yum install -y nginx
fi

# 上传Nginx配置文件（需要手动上传）
echo "10. 请手动上传nginx-prod.conf到服务器并执行："
echo "   cp nginx-prod.conf /etc/nginx/conf.d/smart-order-hub.conf"
echo "   nginx -t"
echo "   systemctl reload nginx"

# 重启Nginx服务
echo "11. 重启Nginx服务..."
systemctl enable nginx
systemctl start nginx

# 验证安装
echo ""
echo "========================================"
echo "验证安装"
echo "========================================"
echo "Java版本："
java -version
echo ""
echo "Maven版本："
mvn -version
echo ""
echo "Node.js版本："
node -v
echo ""
echo "npm版本："
npm -v
echo ""
echo "Nginx状态："
systemctl status nginx --no-pager
echo ""
echo "防火墙规则："
firewall-cmd --list-all
echo ""

echo "========================================"
echo "初始化完成！"
echo "========================================"
echo ""
echo "下一步："
echo "1. 上传nginx-prod.conf到服务器"
echo "2. 执行：cp nginx-prod.conf /etc/nginx/conf.d/smart-order-hub.conf"
echo "3. 执行：nginx -t"
echo "4. 执行：systemctl reload nginx"
echo "5. 在Jenkins中触发首次构建"
echo ""
