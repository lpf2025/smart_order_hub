# 华为云ECS云服务器部署指南

## 项目组成

1. **backend** - Spring Boot 后端服务（端口8081）
2. **pc-admin** - Vue.js 管理后台前端
3. **miniprogram-user** - 用户端微信小程序
4. **miniprogram-merchant** - 商家端微信小程序

## 一、华为云ECS云服务器申请步骤

### 1. 注册华为云账号
- 访问华为云官网：https://www.huaweicloud.com/
- 注册账号并完成实名认证
- 充值账户余额

### 2. 购买云服务器ECS

#### 推荐配置
- **实例规格**：通用计算型s6（2核4GB）
- **操作系统**：CentOS 7.9 64位
- **系统盘**：40GB 高IO
- **带宽**：3Mbps或5Mbps（按使用量计费）
- **计费模式**：按需计费或包年包月

#### 购买步骤
1. 登录华为云控制台
2. 进入"弹性云服务器ECS"
3. 点击"购买弹性云服务器"
4. 选择配置：
   - 计费模式：按需计费（灵活）或包年包月（更便宜）
   - 区域：选择离用户最近的区域
   - 镜像：CentOS 7.9 64位
   - 规格：通用计算型s6（2核4GB）
   - 系统盘：40GB 高IO
   - 网络：VPC，带宽按使用量计费
5. 设置登录凭证（密码或密钥）
6. 确认订单并支付

### 3. 配置安全组
1. 进入"网络控制台" → "安全组"
2. 创建安全组规则：
   - 入方向规则：
     - TCP 80（HTTP）
     - TCP 443（HTTPS）
     - TCP 8081（后端服务）
     - TCP 3306（MySQL数据库，如需外网访问）
   - 出方向规则：允许所有

## 二、部署步骤

### 步骤1：服务器环境准备

```bash
# 1. 连接到服务器
ssh root@your_server_ip

# 2. 更新系统
yum update -y

# 3. 安装Java环境
yum install -y java-1.8.0-openjdk java-1.8.0-openjdk-devel

# 4. 安装Maven
wget https://mirrors.aliyun.com/apache/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz
tar -zxvf apache-maven-3.8.6-bin.tar.gz
mv apache-maven-3.8.6 /usr/local/maven

# 5. 配置环境变量
echo 'export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk' >> /etc/profile
echo 'export MAVEN_HOME=/usr/local/maven' >> /etc/profile
echo 'export PATH=$PATH:$JAVA_HOME/bin:$MAVEN_HOME/bin' >> /etc/profile
source /etc/profile

# 6. 安装MySQL
yum install -y mysql-server
systemctl start mysqld
systemctl enable mysqld

# 7. 安装Nginx
yum install -y nginx
systemctl start nginx
systemctl enable nginx

# 8. 安装Node.js（用于部署前端）
curl -fsSL https://rpm.nodesource.com/setup_16.x | bash -
yum install -y nodejs
npm install -g pm2
```

### 步骤2：数据库部署

```bash
# 1. 创建数据库和用户
mysql -u root -p

CREATE DATABASE smart_order_hub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'smartorder'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON smart_order_hub.* TO 'smartorder'@'%';
FLUSH PRIVILEGES;

# 2. 导入数据库结构
mysql -u smartorder -p smart_order_hub < schema.sql
```

### 步骤3：后端服务部署

```bash
# 1. 创建项目目录
mkdir -p /opt/smart-order-hub
cd /opt/smart-order-hub

# 2. 上传后端代码
# 使用SCP或FTP上传backend文件夹到服务器

# 3. 修改配置文件
cd backend/src/main/resources
vi application.yml

# 修改以下配置：
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/smart_order_hub?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: smartorder
    password: your_password
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

# 4. 打包项目
cd /opt/smart-order-hub/backend
mvn clean package -DskipTests

# 5. 启动服务
nohup java -jar target/SmartOrderHubApplication.jar > backend.log 2>&1 &

# 6. 使用PM2管理进程（推荐）
pm2 start target/SmartOrderHubApplication.jar --name smartorder-backend
pm2 save
pm2 startup
```

### 步骤4：管理后台前端部署

```bash
# 1. 安装依赖并构建
cd /opt/smart-order-hub/pc-admin
npm install
npm run build

# 2. 配置Nginx
vi /etc/nginx/nginx.conf

# 添加以下配置：
server {
    listen 80;
    server_name your_domain.com;

    # 管理后台
    location /admin {
        alias /opt/smart-order-hub/pc-admin/dist;
        try_files $uri $uri/ /admin/index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # 文件上传路径
    location /uploads {
        alias /opt/smart-order-hub/backend/uploads;
    }
}

# 3. 重启Nginx
nginx -t
systemctl restart nginx
```

### 步骤5：微信小程序部署

微信小程序不需要部署到服务器，但需要配置服务器域名：

1. **配置小程序服务器域名**：
   - 登录微信公众平台
   - 进入"开发" → "开发管理" → "开发设置"
   - 配置服务器域名：
     - request合法域名：`https://your_domain.com`
     - socket合法域名：`wss://your_domain.com`
     - uploadFile合法域名：`https://your_domain.com`
     - downloadFile合法域名：`https://your_domain.com`

2. **修改小程序API地址**：
   
   **miniprogram-user/app.js**:
   ```javascript
   const config = {
     baseUrl: 'https://your_domain.com/api'
   };
   ```

   **miniprogram-merchant/app.js**:
   ```javascript
   const config = {
     baseUrl: 'https://your_domain.com/api'
   };
   ```

3. **上传小程序代码**：
   - 使用微信开发者工具
   - 点击"上传"按钮
   - 填写版本号和项目备注
   - 提交审核

### 步骤6：配置SSL证书（推荐）

```bash
# 1. 申请免费SSL证书（Let's Encrypt）
yum install -y certbot python2-certbot-nginx
certbot --nginx -d your_domain.com

# 2. 自动续期
echo "0 0,12 * * * root certbot renew --quiet" | crontab -
```

## 三、域名配置

1. **购买域名**：
   - 华为云域名服务或其他域名注册商
   - 购买域名并完成实名认证

2. **DNS解析**：
   - 进入华为云"域名服务"
   - 添加A记录：
     - 主机记录：@ 或 www
     - 记录值：你的服务器公网IP
     - TTL：600

## 四、监控和维护

```bash
# 1. 查看服务状态
pm2 status
pm2 logs smartorder-backend

# 2. 查看Nginx日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log

# 3. 查看后端日志
tail -f /opt/smart-order-hub/backend/backend.log

# 4. 防火墙配置
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --permanent --add-port=8081/tcp
firewall-cmd --reload
```

## 五、成本估算

### 华为云ECS（按需计费）
- 2核4GB服务器：约 ¥0.15/小时 = ¥108/月
- 40GB SSD硬盘：约 ¥0.35/GB/月 = ¥14/月
- 5Mbps带宽：约 ¥0.8/小时 = ¥576/月
- **总计：约 ¥700/月**

### 华为云ECS（包年包月）
- 2核4GB服务器：约 ¥900-1200/年
- 40GB SSD硬盘：约 ¥150-200/年
- 5Mbps带宽：约 ¥600-800/年
- **总计：约 ¥1650-2200/年 ≈ ¥138-183/月**

### 优化建议
1. 使用包年包月可享受折扣（约5-7折）
2. 使用CDN加速静态资源访问
3. 配置自动备份策略
4. 使用云监控服务监控服务器状态

## 六、常见问题

### 1. 端口无法访问
- 检查安全组规则是否开放
- 检查防火墙是否放行端口
- 检查服务是否正常启动

### 2. 数据库连接失败
- 检查MySQL服务是否启动
- 检查数据库用户权限
- 检查防火墙是否开放3306端口

### 3. 小程序无法连接
- 检查服务器域名是否配置
- 检查SSL证书是否有效
- 检查小程序域名是否在白名单中

### 4. 文件上传失败
- 检查Nginx配置的uploads路径
- 检查文件大小限制
- 检查目录权限

## 七、联系方式

- 华为云技术支持：https://support.huaweicloud.com/
- 华为云文档中心：https://support.huaweicloud.com/docs/
- 项目技术支持：[填写你的联系方式]
