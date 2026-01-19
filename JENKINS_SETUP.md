# Jenkins 自动部署配置完整指南

## 目录
1. [Jenkins安装](#jenkins安装)
2. [Jenkins配置](#jenkins配置)
3. [创建Jenkins任务](#创建jenkins任务)
4. [服务器准备](#服务器准备)
5. [部署测试](#部署测试)
6. [常见问题](#常见问题)

---

## Jenkins安装

### Windows安装

```powershell
# 1. 下载Jenkins
# 访问 https://www.jenkins.io/download/
# 下载Windows版本

# 2. 安装Jenkins
# 运行安装程序，按照提示完成安装

# 3. 访问Jenkins
# 浏览器访问 http://localhost:8080

# 4. 解锁Jenkins
# 查看初始密码：C:\Program Files\Jenkins\home\secrets\initialAdminPassword
# 输入密码，安装推荐插件

# 5. 创建管理员账号
# 用户名：admin
# 密码：自定义
```

### Linux安装

```bash
# 1. 安装Java
sudo yum install -y java-11-openjdk

# 2. 添加Jenkins仓库
sudo wget -O /etc/yum.repos.d/jenkins.repo \
    https://pkg.jenkins.io/redhat-stable/jenkins.repo

# 3. 安装Jenkins
sudo yum install -y jenkins

# 4. 启动Jenkins
sudo systemctl start jenkins
sudo systemctl enable jenkins

# 5. 查看初始密码
sudo cat /var/lib/jenkins/secrets/initialAdminPassword

# 6. 访问Jenkins
# 浏览器访问 http://服务器IP:8080
```

---

## Jenkins配置

### 1. 安装必要插件

登录Jenkins后，安装以下插件：

1. **Git Plugin** - Git版本控制
2. **Pipeline Plugin** - 流水线支持
3. **SSH Agent Plugin** - SSH连接
4. **Email Extension Plugin** - 邮件通知
5. **Timestamper Plugin** - 时间戳

安装步骤：
1. 进入"Manage Jenkins" → "Manage Plugins"
2. 搜索并安装上述插件
3. 安装完成后重启Jenkins

### 2. 配置全局工具

#### 配置JDK
1. 进入"Manage Jenkins" → "Global Tool Configuration"
2. 找到"JDK"部分
3. 点击"JDK安装"
4. 配置：
   - Name: `JDK11`
   - JAVA_HOME: `/usr/lib/jvm/java-11-openjdk` (Linux)
   - 或：`C:\Program Files\Java\jdk-11` (Windows)

#### 配置Maven
1. 在同一页面找到"Maven"部分
2. 点击"Maven安装"
3. 配置：
   - Name: `Maven3.8`
   - MAVEN_HOME: 自动安装或指定路径

#### 配置Node.js
1. 在同一页面找到"Node.js"部分
2. 点击"Node.js安装"
3. 配置：
   - Name: `Node18`
   - 自动安装：勾选，选择版本18.x

---

## 创建Jenkins任务

### 步骤1：创建新任务

1. 登录Jenkins
2. 点击首页"新建任务"
3. 输入任务名称：`smart-order-hub-prod`
4. 选择"流水线（Pipeline）"
5. 点击"确定"

### 步骤2：配置Pipeline

在任务配置页面，找到"Pipeline"部分：

#### 选项1：Pipeline script from SCM（推荐）

1. Definition：选择"Pipeline script from SCM"
2. SCM：选择"Git"
3. Repository URL：`https://github.com/lpf2025/smart_order_hub.git`
4. Branches to build：`*/main`
5. Script Path：`Jenkinsfile`
6. Lightweight checkout：勾选（加快检出速度）

#### 选项2：Pipeline script（直接编写）

如果不想使用Jenkinsfile，可以直接在Jenkins中编写：

```groovy
pipeline {
    agent any
    
    environment {
        // 服务器配置
        SERVER_HOST = '106.15.90.212'
        SERVER_USER = 'root'
        SERVER_PASSWORD = credentials('server-password')
        
        // 部署路径
        BACKEND_DEPLOY_DIR = '/opt/smart-order-hub/backend'
        FRONTEND_DEPLOY_DIR = '/var/www/admin'
        
        // 应用配置
        JAR_NAME = 'smart-order-hub-1.0.0.jar'
        JAR_PORT = '8081'
    }
    
    stages {
        stage('代码检出') {
            steps {
                echo '开始检出代码...'
                checkout scm
            }
        }
        
        stage('后端打包') {
            steps {
                echo '开始打包后端...'
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('前端打包') {
            steps {
                echo '开始打包前端...'
                dir('pc-admin') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }
        
        stage('部署后端') {
            steps {
                echo '开始部署后端...'
                sh """
                    # 创建部署目录
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'mkdir -p ${BACKEND_DEPLOY_DIR}'
                    
                    # 停止旧服务
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && ./stop.sh || true'
                    
                    # 上传jar包
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no backend/target/${JAR_NAME} ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/
                    
                    # 上传启动脚本
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no scripts/backend-start.sh ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/start.sh
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no scripts/backend-stop.sh ${SERVER_USER}@${SERVER_HOST}:${BACKEND_DEPLOY_DIR}/stop.sh
                    
                    # 启动新服务
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && chmod +x start.sh stop.sh && ./start.sh'
                    
                    echo '后端部署完成'
                """
            }
        }
        
        stage('部署前端') {
            steps {
                echo '开始部署前端...'
                sh """
                    # 创建部署目录
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'mkdir -p ${FRONTEND_DEPLOY_DIR}'
                    
                    # 清空旧文件
                    sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'rm -rf ${FRONTEND_DEPLOY_DIR}/*'
                    
                    # 上传前端文件
                    sshpass -p '${SERVER_PASSWORD}' scp -o StrictHostKeyChecking=no -r pc-admin/dist/* ${SERVER_USER}@${SERVER_HOST}:${FRONTEND_DEPLOY_DIR}/
                    
                    echo '前端部署完成'
                """
            }
        }
        
        stage('健康检查') {
            steps {
                echo '开始健康检查...'
                sh """
                    # 等待服务启动
                    sleep 30
                    
                    # 检查后端服务
                    curl -f http://${SERVER_HOST}:${JAR_PORT}/api/health || exit 1
                    
                    # 检查前端服务
                    curl -f http://${SERVER_HOST}/admin/ || exit 1
                    
                    echo '健康检查通过'
                """
            }
        }
    }
    
    post {
        success {
            echo '部署成功！'
            emailext (
                subject: '✅ 智能点餐系统部署成功',
                body: """
                    <h2>部署成功</h2>
                    <p>项目：智能点餐系统</p>
                    <p>环境：生产环境</p>
                    <p>部署时间：${new Date()}</p>
                    <p>后端地址：http://${SERVER_HOST}:${JAR_PORT}</p>
                    <p>前端地址：http://${SERVER_HOST}/admin/</p>
                """,
                to: '624140273@qq.com',
                mimeType: 'text/html'
            )
        }
        failure {
            echo '部署失败！'
            emailext (
                subject: '❌ 智能点餐系统部署失败',
                body: """
                    <h2>部署失败</h2>
                    <p>项目：智能点餐系统</p>
                    <p>环境：生产环境</p>
                    <p>部署时间：${new Date()}</p>
                    <p>请检查Jenkins日志获取详细信息</p>
                """,
                to: '624140273@qq.com',
                mimeType: 'text/html'
            )
        }
    }
}
```

### 步骤3：配置构建触发器

在"构建触发器"部分：

#### 选项1：手动触发
- 勾选"Build periodically"
- 不填写时间表，仅手动触发

#### 选项2：定时构建
- 勾选"Build periodically"
- 输入Cron表达式，例如：
  - `H/30 * * * *` - 每30分钟构建一次
  - `H 2 * * *` - 每天凌晨2点构建
  - `H H(9-18) * * 1-5` - 工作日每小时构建

#### 选项3：Git webhook触发（推荐）
1. 在GitHub仓库设置中添加webhook
2. Payload URL：`http://Jenkins地址/github-webhook/`
3. Secret：自定义密钥
4. 勾选"GitHub hook trigger for GITScm polling"

---

## 服务器准备

### 1. 安装必要工具

```bash
# 更新系统
sudo yum update -y

# 安装基础工具
sudo yum install -y git maven nodejs npm curl wget vim

# 安装sshpass（用于非交互式SSH）
sudo yum install -y sshpass

# 安装Java（如果还没有）
sudo yum install -y java-11-openjdk

# 验证安装
java -version
mvn -version
node -v
npm -v
```

### 2. 创建部署目录

```bash
# 后端部署目录
sudo mkdir -p /opt/smart-order-hub/backend
sudo chown -R root:root /opt/smart-order-hub

# 前端部署目录
sudo mkdir -p /var/www/admin
sudo chown -R nginx:nginx /var/www/admin

# 日志目录
sudo mkdir -p /var/log/smart-order-hub
sudo chown -R root:root /var/log/smart-order-hub
```

### 3. 配置Nginx

```bash
# 创建Nginx配置文件
sudo vim /etc/nginx/conf.d/smart-order.conf
```

添加以下配置：

```nginx
server {
    listen 80;
    server_name 106.15.90.212;
    
    # 前端管理后台
    location /admin/ {
        alias /var/www/admin/;
        try_files $uri $uri/ /admin/index.html;
        
        # 缓存配置
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
        
        # 超时配置
        proxy_connect_timeout 300s;
        proxy_send_timeout 300s;
        proxy_read_timeout 300s;
    }
    
    # 文件上传
    location /uploads/ {
        alias /opt/smart-order-hub/backend/uploads/;
        
        # 允许上传大文件
        client_max_body_size 50M;
    }
    
    # Gzip压缩
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
}
```

重启Nginx：

```bash
# 测试配置
sudo nginx -t

# 重启Nginx
sudo systemctl restart nginx

# 设置开机自启
sudo systemctl enable nginx
```

### 4. 配置防火墙

```bash
# 开放端口
sudo firewall-cmd --permanent --add-port=80/tcp
sudo firewall-cmd --permanent --add-port=8081/tcp
sudo firewall-cmd --permanent --add-port=443/tcp

# 重载防火墙
sudo firewall-cmd --reload
```

### 5. 上传部署脚本

```bash
# 进入部署目录
cd /opt/smart-order-hub/backend

# 创建启动脚本
cat > start.sh << 'EOF'
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
nohup java -jar $JAR_NAME --spring.profiles.active=prod > /var/log/smart-order-hub/app.log 2>&1 &

# 等待应用启动
sleep 10

# 检查应用是否启动成功
NEW_PID=$(lsof -ti:$PORT)
if [ -n "$NEW_PID" ]; then
    echo "========================================"
    echo "应用启动成功！"
    echo "进程ID: $NEW_PID"
    echo "端口: $PORT"
    echo "日志文件: /var/log/smart-order-hub/app.log"
    echo "========================================"
else
    echo "========================================"
    echo "应用启动失败！"
    echo "请查看日志: tail -f /var/log/smart-order-hub/app.log"
    echo "========================================"
    exit 1
fi
EOF

# 创建停止脚本
cat > stop.sh << 'EOF'
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
EOF

# 设置执行权限
chmod +x start.sh stop.sh
```

---

## 部署测试

### 1. 手动触发构建

1. 进入Jenkins任务页面
2. 点击"立即构建"
3. 查看构建日志

### 2. 验证部署

#### 检查后端服务

```bash
# 查看进程
ps aux | grep smart-order-hub

# 查看端口
netstat -tlnp | grep 8081

# 查看日志
tail -f /var/log/smart-order-hub/app.log

# 测试API
curl http://106.15.90.212:8081/api/health
```

#### 检查前端服务

```bash
# 查看文件
ls -la /var/www/admin/

# 测试访问
curl http://106.15.90.212/admin/
```

### 3. 浏览器测试

1. 访问前端管理后台：`http://106.15.90.212/admin/`
2. 访问后端API：`http://106.15.90.212:8081/api/health`

---

## 常见问题

### 1. Jenkins构建失败

#### 问题：Maven构建失败
```bash
# 清理Maven缓存
rm -rf ~/.m2/repository

# 重新构建
```

#### 问题：npm install失败
```bash
# 清理npm缓存
npm cache clean --force

# 重新安装
npm install --registry=https://registry.npm.taobao.org
```

### 2. 部署失败

#### 问题：SSH连接失败
```bash
# 检查SSH密钥
ls -la ~/.ssh/

# 测试SSH连接
ssh root@106.15.90.212

# 检查防火墙
sudo firewall-cmd --list-all
```

#### 问题：端口被占用
```bash
# 查看占用进程
lsof -ti:8081

# 停止进程
kill -9 <PID>
```

### 3. 服务启动失败

#### 问题：Java内存不足
```bash
# 增加JVM内存
nohup java -Xms512m -Xmx1024m -jar $JAR_NAME --spring.profiles.active=prod > app.log 2>&1 &
```

#### 问题：数据库连接失败
```bash
# 检查数据库连接
mysql -h rm-uf6fjt8k75xbfc1phso.mysql.rds.aliyuncs.com -u root -p

# 检查防火墙
sudo firewall-cmd --list-all
```

---

## 高级配置

### 1. 配置多环境

创建不同的Jenkins任务：
- `smart-order-hub-dev` - 开发环境
- `smart-order-hub-test` - 测试环境
- `smart-order-hub-prod` - 生产环境

每个任务使用不同的配置：
```groovy
environment {
    SERVER_HOST = env == 'prod' ? '106.15.90.212' : 'dev-server-ip'
    BACKEND_DEPLOY_DIR = env == 'prod' ? '/opt/smart-order-hub/backend' : '/opt/smart-order-hub-dev/backend'
    FRONTEND_DEPLOY_DIR = env == 'prod' ? '/var/www/admin' : '/var/www/admin-dev'
}
```

### 2. 配置回滚

在Jenkinsfile中添加回滚阶段：

```groovy
stage('回滚') {
    when {
        expression { return params.ROLLBACK }
    }
    steps {
        echo '开始回滚...'
        sh """
            sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && ./stop.sh'
            sshpass -p '${SERVER_PASSWORD}' ssh -o StrictHostKeyChecking=no ${SERVER_USER}@${SERVER_HOST} 'cd ${BACKEND_DEPLOY_DIR} && git checkout HEAD~1 && ./start.sh'
        """
    }
}
```

### 3. 配置通知

除了邮件通知，还可以配置：
- 企业微信通知
- 钉钉通知
- Slack通知
- 短信通知

---

## 总结

### 完整部署流程

1. **开发阶段**
   - 开发人员在本地开发
   - 提交代码到GitHub

2. **自动触发**
   - Git webhook触发Jenkins构建
   - 或手动触发构建

3. **构建阶段**
   - Jenkins检出代码
   - 打包后端和前端
   - 运行测试

4. **部署阶段**
   - 上传jar包到服务器
   - 上传前端文件到服务器
   - 重启服务

5. **验证阶段**
   - 健康检查
   - 发送通知

### 优势

- ✅ 自动化部署，减少人工操作
- ✅ 版本控制，可追溯历史
- ✅ 快速回滚，降低风险
- ✅ 统一流程，提高效率
- ✅ 实时监控，及时发现问题

---

## 附录：Jenkinsfile完整示例

项目根目录的[Jenkinsfile](file:///d:/git/smart_rder_hub/Jenkinsfile)已经包含了完整的流水线配置，可以直接使用。
