# 智能点餐系统 - 打包部署说明

## 环境配置说明

### 小程序端

小程序会根据运行环境自动切换API地址：

- **开发环境（开发版/体验版）**: `http://localhost:8081/api`
- **生产环境（正式版）**: `http://106.15.90.212:8081/api`

配置文件：
- `config.js` - 环境配置
- `env.js` - 环境判断逻辑

### 后端

后端使用Spring Profile区分环境：

- **开发环境（dev）**: 日志级别debug
- **生产环境（prod）**: 日志级别info

配置文件：
- `application.yml` - 主配置文件
- `application-dev.yml` - 开发环境配置
- `application-prod.yml` - 生产环境配置

---

## 打包方法

### 一、小程序打包

#### 1. 测试环境打包

双击运行：`miniprogram-merchant\build-test.bat`

或在微信开发者工具中：
1. 点击【工具】→【构建npm】（如果使用了npm）
2. 点击【工具】→【上传】
3. 填写版本号和项目备注
4. 点击【上传】

上传后，在微信公众平台：
1. 登录小程序管理后台
2. 进入【版本管理】
3. 找到刚上传的版本
4. 点击【设为体验版】

#### 2. 生产环境打包

双击运行：`miniprogram-merchant\build-prod.bat`

或在微信开发者工具中：
1. 点击【工具】→【构建npm】（如果使用了npm）
2. 点击【工具】→【上传】
3. 填写版本号和项目备注
4. 点击【上传】

上传后，在微信公众平台：
1. 登录小程序管理后台
2. 进入【版本管理】
3. 找到刚上传的版本
4. 点击【提交审核】
5. 审核通过后点击【发布】

---

### 二、后端打包

#### 1. 测试环境打包

双击运行：`backend-build-test.bat`

或在命令行中执行：
```bash
cd backend
mvn clean package -DskipTests
```

打包成功后，jar包位置：`backend\target\smart-order-hub-1.0.0.jar`

启动命令：
```bash
java -jar target\smart-order-hub-1.0.0.jar --spring.profiles.active=dev
```

#### 2. 生产环境打包

双击运行：`backend-build-prod.bat`

或在命令行中执行：
```bash
cd backend
mvn clean package -DskipTests
```

打包成功后，jar包位置：`backend\target\smart-order-hub-1.0.0.jar`

启动命令：
```bash
java -jar target\smart-order-hub-1.0.0.jar --spring.profiles.active=prod
```

---

## 部署说明

### 后端部署

1. 将打包好的jar包上传到服务器
2. 使用以下命令启动：

**测试环境：**
```bash
nohup java -jar smart-order-hub-1.0.0.jar --spring.profiles.active=dev > app.log 2>&1 &
```

**生产环境：**
```bash
nohup java -jar smart-order-hub-1.0.0.jar --spring.profiles.active=prod > app.log 2>&1 &
```

3. 查看日志：
```bash
tail -f app.log
```

4. 停止服务：
```bash
ps -ef | grep smart-order-hub
kill -9 <进程ID>
```

---

## 注意事项

1. **小程序环境切换**：
   - 开发工具中默认使用开发环境
   - 体验版使用开发环境
   - 正式版使用生产环境

2. **后端环境切换**：
   - 通过启动参数`--spring.profiles.active`指定环境
   - 默认使用dev环境

3. **数据库配置**：
   - 开发和生产环境使用同一个数据库
   - 如需使用不同数据库，请修改对应的配置文件

4. **端口配置**：
   - 后端默认端口：8081
   - 前端管理后台默认端口：3000
   - 小程序无需配置端口

---

## 常见问题

### 1. 小程序打包后API地址不对

检查`config.js`中的配置是否正确，确保：
- 开发环境使用localhost地址
- 生产环境使用服务器地址

### 2. 后端启动失败

检查：
- Java版本是否为JDK 8或以上
- 数据库连接是否正常
- 端口8081是否被占用

### 3. 小程序无法请求接口

检查：
- 后端服务是否正常启动
- API地址是否正确
- 网络是否通畅
