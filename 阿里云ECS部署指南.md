# 阿里云轻量应用服务器部署指南

## 项目组成

1. **backend** - Spring Boot 后端服务（端口8081）
2. **pc-admin** - Vue.js 管理后台前端
3. **miniprogram-user** - 用户端微信小程序
4. **miniprogram-merchant** - 商家端微信小程序

## 一、前期准备：注册与实名认证（必做）

### 1. 注册阿里云账号
- 访问阿里云官网：https://www.aliyun.com/
- 点击右上角"注册"
- 可用手机号/邮箱注册，或支付宝/淘宝/钉钉快捷登录
- 设置安全密码并绑定手机

### 2. 实名认证
- 登录后进入"账号中心→实名认证"
- **个人用户**：支付宝扫码或上传身份证，1-2分钟自动审核
- **企业用户**：上传营业执照+法人扫脸，审核1-2个工作日

### 3. 安全加固
- 开启MFA多因素认证，降低账号被盗风险
- 绑定手机号和邮箱
- 设置强密码

### 4. 充值账户余额
- 进入"费用中心→充值"
- 支持支付宝、微信、银行卡等多种支付方式

## 二、产品选型：按场景选服务器

### 产品对比

| 产品类型 | 适用场景 | 特点 | 新手推荐 |
|---------|---------|------|---------|
| **轻量应用服务器** | 个人博客、测试环境、小型应用 | 预装环境、操作简单、成本低 | 2核2G+40G ESSD+200M峰值带宽（新用户秒杀价38元/年） |
| **云服务器ECS** | 企业应用、定制化部署、高并发业务 | 灵活配置、可扩展、适配复杂场景 | 2核2G+40G ESSD+3M固定带宽（99元/年） |

### 推荐配置：轻量应用服务器

- **实例规格**：2核2GB
- **操作系统**：CentOS 7.9 64位
- **系统盘**：40GB ESSD云盘
- **带宽**：200Mbps峰值带宽
- **计费模式**：包年包月
- **价格**：¥38/年（新用户特惠价）

## 三、购买配置

### 方式一：快速购买（新手/临时测试）

1. 进入轻量应用服务器产品页：https://www.aliyun.com/product/swas
2. 点击"快速购买"
3. 系统默认填充基础配置：
   - 实例规格：2核2GB
   - 系统盘：40GB ESSD
   - 带宽：200Mbps峰值
4. 仅需选择：
   - 地域：选择离用户最近的区域（如华东1-杭州、华东2-上海）
   - 镜像：CentOS 7.9 64位
   - 购买时长：1年
5. 设置登录凭证（密码）
6. 确认订单并支付（¥38/年）
7. 系统1分钟内自动创建实例，状态为"运行中"即成功

### 方式二：自定义购买（推荐）

1. 登录阿里云控制台
2. 进入"轻量应用服务器"
3. 点击"创建实例"

#### 基础配置
- **付费类型**：包年包月（1年），年付8-9折
- **地域/可用区**：选择离用户最近的区域（如华东1-杭州、华东2-上海），降低延迟
- **镜像**：CentOS 7.9 64位
- **套餐规格**：2核2GB/40GB ESSD/200Mbps峰值带宽
- **购买数量**：1台
- **购买时长**：1年

#### 系统配置
- **实例名称**：smart-order-server（可自定义）
- **登录凭证**：设置密码（建议使用强密码，包含大小写字母、数字和特殊字符）

#### 确认订单
- 勾选服务协议
- 用支付宝/微信/银行卡支付
- 系统5分钟内自动创建实例，状态为"运行中"即成功

## 四、配置防火墙

1. 进入"轻量应用服务器" → "服务器列表"
2. 找到你的服务器，点击"管理"
3. 点击"防火墙" → "添加规则"
4. 添加以下规则：
   - TCP 22（SSH）- 0.0.0.0/0
   - TCP 80（HTTP）- 0.0.0.0/0
   - TCP 443（HTTPS）- 0.0.0.0/0
   - TCP 8081（后端服务）- 0.0.0.0/0
   - TCP 3306（MySQL数据库，如需外网访问）- 0.0.0.0/0

## 五、部署步骤

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

## 六、域名配置与备案

### 1. 购买域名
- 阿里云域名服务或其他域名注册商
- 购买域名并完成实名认证

### 2. DNS解析
- 进入阿里云"域名" → "域名解析"
- 添加A记录：
  - 主机记录：@ 或 www
  - 记录值：你的服务器公网IP
  - TTL：600

### 3. ICP备案（重要）
- **备案要求**：搭建网站需完成ICP备案
- **备案条件**：包年包月≥3个月可备案，按量付费不可备案
- **备案流程**：
  1. 登录阿里云备案系统
  2. 填写主体信息和网站信息
  3. 上传身份证、营业执照等材料
  4. 阿里云初审（1-2个工作日）
  5. 管局审核（20个工作日左右）
- **备案期间**：网站可以正常访问，但不能通过域名访问

## 七、实例初始化与安全优化

### 1. 登录实例
- 控制台获取公网IP
- 通过SSH（Linux）登录：`ssh root@your_server_ip`
- 使用购买时设置的密码验证

### 2. 安全优化
- 修改默认SSH端口（从22改为其他端口）
- 禁用root远程登录，使用普通用户
- 配置防火墙规则，只开放必要端口
- 定期更新系统与软件补丁
- 安装fail2ban防止暴力破解

### 3. 环境部署
- Linux系统安装Docker、Nginx等
- 或使用云市场镜像预装环境（如宝塔面板）

## 八、监控和维护

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
firewall-cmd --permanent --add-port/8081/tcp
firewall-cmd --reload
```

## 九、成本估算

### 阿里云轻量应用服务器（推荐）
- **2核2GB服务器**：¥38/年（新用户特惠价）
- **40GB ESSD云盘**：包含在价格中
- **200Mbps峰值带宽**：包含在价格中
- **总计：¥38/年 ≈ ¥3.2/月**

### 与其他方案对比

| 项目 | 轻量应用服务器 | ECS经济型e实例 | 华为云 |
|------|---------------|---------------|--------|
| **年付价格** | ¥38/年 | ¥99/年 | ¥1650-2200/年 |
| **配置** | 2核2G/40G/200M | 2核2G/40G/3M | 2核4G/40G/5M |
| **性价比** | 极高 | 高 | 一般 |
| **适合场景** | 个人、小型项目 | 个人、小型项目 | 政企 |

### 优化建议
1. **强烈推荐轻量应用服务器**：仅需¥38/年，性价比极高
2. 新用户特惠价格，续费价格可能略有上涨
3. 使用CDN加速静态资源访问
4. 配置自动备份策略
5. 使用云监控服务监控服务器状态

## 十、常见问题与注意事项

### 1. 端口无法访问
- 检查防火墙规则是否开放
- 检查系统防火墙是否放行端口
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

### 5. 新用户优惠购买失败
- 确保已完成个人实名认证
- 确保账号是新用户（未购买过该类型服务器）
- 联系阿里云客服咨询

### 6. 备案相关问题
- **备案时间**：通常需要20个工作日左右
- **备案期间**：可以通过IP访问，但不能通过域名访问
- **备案要求**：包年包月≥3个月才能备案

### 7. 续费相关问题
- **续费提醒**：包年包月到期前15天会提醒
- **续费价格**：续费同价可享折扣
- **按量付费**：及时充值，避免实例释放

### 8. 扩容相关问题
- **业务增长时**：可在控制台升级实例规格、带宽、存储
- **在线升级**：无需停机（部分操作需重启）
- **弹性伸缩**：支持按需调整配置

## 十一、联系方式

- 阿里云技术支持：https://help.aliyun.com/
- 阿里云文档中心：https://help.aliyun.com/document_detail/
- 阿里云轻量应用服务器：https://www.aliyun.com/product/swas
- 阿里云备案服务：https://beian.aliyun.com/
- 项目技术支持：[填写你的联系方式]

## 十二、优势总结

### 为什么选择阿里云轻量应用服务器？

1. **价格优势明显**：仅需¥38/年，比华为云节省95%以上费用
2. **适合个人用户**：无需企业认证，个人实名即可购买
3. **配置充足**：2核2GB完全满足点餐系统需求
4. **操作简单**：轻量应用服务器界面简洁，易于管理
5. **生态完善**：微信小程序、移动应用等生态支持好
6. **文档丰富**：遇到问题容易找到解决方案
7. **市场份额最大**：用户基数大，社区活跃
8. **技术支持响应快**：7x24小时技术支持
9. **新用户特惠**：个人用户也能享受超低价格
10. **易于升级**：后续可以随时升级配置

### 配置说明

**2核2GB配置完全够用的原因：**
- Spring Boot 后端服务：约512MB-1GB内存
- MySQL数据库：约512MB-1GB内存
- Nginx（前端）：约50-100MB内存
- 系统和其他服务：约200-300MB内存
- **总计需求：约1.5-2.4GB内存**

**支持能力：**
- 支持几十到上百个并发用户
- 适合个人项目或小型商户使用
- 200Mbps峰值带宽足够支持小程序访问

**什么时候需要升级？**
- 如果日活用户超过500人
- 如果并发请求经常超过50个
- 如果发现服务器响应变慢

**升级方案：**
- 可以直接升级到更高配置
- 阿里云支持在线升级，无需重新部署

### 与华为云对比

| 项目 | 阿里云轻量应用服务器 | 华为云 | 优势 |
|------|---------------------|--------|------|
| **年付价格** | ¥38/年 | ¥1650-2200/年 | 阿里云 |
| **续费价格** | 可能略有上涨 | 可能上涨 | 阿里云 |
| **配置** | 2核2G/40G/200M | 2核4G/40G/5M | 华为云 |
| **性价比** | 极高 | 一般 | 阿里云 |
| **适合场景** | 个人、中小企业 | 政企 | 阿里云 |
| **认证要求** | 个人实名 | 个人/企业实名 | 持平 |
| **操作难度** | 简单 | 较复杂 | 阿里云 |

**结论：强烈推荐使用阿里云轻量应用服务器！**

仅需¥38/年，完全满足点餐系统需求，性价比极高！
