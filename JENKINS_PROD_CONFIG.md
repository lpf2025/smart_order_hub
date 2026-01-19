# Jenkins 生产环境配置步骤（生产环境）

## 前提条件

- ✅ Jenkins已安装并正常运行
- ✅ 服务器已准备（106.15.90.212）
- ✅ GitHub仓库：https://github.com/lpf2025/smart_order_hub.git
- ✅ 服务器用户：root
- ✅ 服务器密码：已准备好

---

## 第一步：配置Jenkins凭据

### 1.1 添加服务器密码凭据

1. 登录Jenkins（例如：http://localhost:8080）
2. 点击右上角用户名 → "Manage Jenkins"
3. 点击左侧"Manage Credentials"
4. 点击"Global credentials (unrestricted)"
5. 点击左侧"Add Credentials"
6. 填写凭据信息：
   - **类型**：选择"Username with password"
   - **Scope**：选择"Global (Jenkins, nodes, items, all child items, etc)"
   - **Username**：`root`
   - **Password**：输入您的服务器root密码
   - **ID**：`server-password`
   - **Description**：`生产服务器root密码`
7. 点击"Create"

### 1.2 添加GitHub凭据（用于webhook触发）

1. 在同一页面继续点击"Add Credentials"
2. 填写凭据信息：
   - **类型**：选择"Secret text"
   - **Scope**：选择"Global"
   - **Secret**：`smart-order-hub-webhook-secret`（或自定义密钥）
   - **ID**：`github-webhook-secret`
   - **Description**：`GitHub Webhook密钥`
3. 点击"Create"

**重要**：记录这个密钥，稍后在GitHub配置webhook时需要使用

---

## 第二步：创建Jenkins任务

### 2.1 创建新任务

1. 返回Jenkins首页
2. 点击左侧"新建任务"
3. 输入任务名称：`smart-order-hub-prod`
4. 选择任务类型：点击"流水线（Pipeline）"
5. 点击"确定"按钮

### 2.2 配置General

在任务配置页面：

1. **描述**：
   ```
   智能点餐系统 - 生产环境自动部署
   ```

2. **构建触发器**：
   - 勾选"GitHub hook trigger for GITScm polling"
   - **GitHub hook URL**：`http://<Jenkins公网IP或域名>/github-webhook/`
   - **Secret**：`smart-order-hub-webhook-secret`（与第一步中创建的密钥一致）

### 2.3 配置Pipeline

在"Pipeline"部分：

1. **Definition**：选择"Pipeline script from SCM"
2. **SCM**：选择"Git"
3. **Repository URL**：`https://github.com/lpf2025/smart_order_hub.git`
4. **Credentials**：选择"624140273@qq.com"（如果没有，点击"Add"添加）
   - 类型：Username with password
   - Username：`624140273@qq.com`
   - Password：`135138785322qw`
5. **Branches to build**：`*/main`
6. **Script Path**：`Jenkinsfile`
7. **Lightweight checkout**：勾选（加快检出速度）

### 2.4 保存配置

1. 滚动到页面底部
2. 点击"保存"按钮
3. 点击"应用"按钮

---

## 第三步：配置GitHub Webhook

### 3.1 进入GitHub仓库设置

1. 访问：https://github.com/lpf2025/smart_order_hub/settings/hooks
2. 点击"Add webhook"按钮
3. 填写webhook信息：
   - **Payload URL**：`http://<Jenkins公网IP或域名>/github-webhook/`
   - **Content type**：选择"application/json"
   - **Secret**：`smart-order-hub-webhook-secret`（与Jenkins凭据中的密钥一致）
   - **Which events would you like to trigger this webhook?**
     - 勾选"Just the push event"
     - 勾选"Branch or tag creation"
   - **Active**：勾选"Active"

4. 点击"Add webhook"按钮

**重要**：
- 确保Jenkins服务器可以从外网访问（防火墙开放8080端口）
- 如果使用域名，确保域名已解析到Jenkins服务器IP
- Secret必须与Jenkins凭据中的Secret完全一致

---

## 第四步：初始化服务器

### 4.1 上传并执行初始化脚本

在您的本地电脑上执行：

```bash
# 1. 上传初始化脚本到服务器
scp scripts/server-init.sh root@106.15.90.212:/tmp/

# 2. SSH登录到服务器
ssh root@106.15.90.212

# 3. 执行初始化脚本
bash /tmp/server-init.sh
```

或者直接在服务器上执行：

```bash
# 下载初始化脚本
cd /tmp
wget https://raw.githubusercontent.com/lpf2025/smart_order_hub/main/scripts/server-init.sh

# 执行初始化脚本
bash server-init.sh
```

### 4.2 验证服务器初始化

初始化脚本会自动完成以下操作：

1. ✅ 安装Java 11
2. ✅ 安装Maven 3.8
3. ✅ 安装Node.js 18
4. ✅ 配置npm淘宝镜像
5. ✅ 安装sshpass
6. ✅ 创建部署目录
7. ✅ 配置防火墙
8. ✅ 配置Nginx
9. ✅ 重启Nginx服务

验证命令：

```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -version

# 检查Node.js版本
node -v

# 检查Nginx状态
systemctl status nginx

# 检查防火墙状态
firewall-cmd --list-all
```

---

## 第五步：手动触发首次构建

### 5.1 触发构建

1. 返回Jenkins首页
2. 找到任务"smart-order-hub-prod"
3. 点击"立即构建"按钮

### 5.2 查看构建日志

1. 点击构建号进入构建详情页
2. 查看"Console Output"标签页
3. 观察构建进度

构建流程：
1. ✅ 代码检出
2. ✅ 环境检查
3. ✅ 后端打包
4. ✅ 前端打包
5. ✅ 部署后端
6. ✅ 部署前端
7. ✅ 健康检查
8. ✅ 清理构建

### 5.3 验证部署

构建成功后，访问以下地址验证：

- **前端管理后台**：http://106.15.90.212/admin/
- **后端API健康检查**：http://106.15.90.212:8081/api/health

---

## 第六步：配置自动触发（可选）

### 6.1 配置定时构建

如果需要定时构建（例如每天凌晨2点）：

1. 进入任务配置页面
2. 在"构建触发器"部分
3. 勾选"Build periodically"
4. 输入Cron表达式：`H 2 * * *`
   - `H`：表示整点
   - `2`：表示凌晨2点
   - `* * * *`：每天执行

### 6.2 配置Webhook自动触发（推荐）

已经完成第三步的GitHub webhook配置后：
- 每次推送代码到main分支
- GitHub会自动触发Jenkins构建
- 无需手动操作

---

## 第七步：配置邮件通知（可选）

### 7.1 配置Jenkins邮件

1. 进入"Manage Jenkins" → "Configure System"
2. 找到"邮件通知"部分
3. 填写SMTP配置：
   - **SMTP server**：`smtp.qq.com`（或其他SMTP服务器）
   - **SMTP port**：`465`或`587`
   - **Use SSL**：勾选
   - **Username**：`624140273@qq.com`
   - **Password**：QQ邮箱授权码（不是QQ密码）
   - **Reply-To Address**：`624140273@qq.com`
   - **Default suffix E-mail Notification Recipient**：`624140273@qq.com`
4. 点击"Test configuration"测试邮件发送
5. 点击"保存"

---

## 常见问题排查

### 问题1：构建失败 - Maven依赖下载慢

**解决方案**：配置Maven阿里云镜像

```bash
# 在服务器上配置Maven镜像
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << 'EOF'
<settings>
    <mirrors>
        <mirror>
            <id>aliyunmaven</id>
            <mirrorOf>central</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
</settings>
EOF
```

### 问题2：构建失败 - npm install超时

**解决方案**：已在Jenkinsfile中配置淘宝镜像

```bash
npm install --registry=https://registry.npm.taobao.org
```

### 问题3：部署失败 - SSH连接超时

**解决方案**：检查网络连接和防火墙

```bash
# 测试SSH连接
ssh -o ConnectTimeout=10 root@106.15.90.212 "echo '连接成功'"

# 检查防火墙
firewall-cmd --list-all

# 如果SSH端口不是22，需要指定
ssh -p <端口号> root@106.15.90.212
```

### 问题4：健康检查失败

**解决方案**：检查服务启动状态

```bash
# 查看后端日志
tail -100 /var/log/smart-order-hub/app.log

# 查看进程
ps aux | grep smart-order-hub

# 查看端口
netstat -tlnp | grep 8081

# 手动重启服务
cd /opt/smart-order-hub/backend
./stop.sh
./start.sh
```

### 问题5：前端访问404

**解决方案**：检查Nginx配置和文件权限

```bash
# 检查Nginx配置
nginx -t

# 检查文件是否存在
ls -la /var/www/admin/

# 检查文件权限
ls -la /var/www/admin/index.html

# 重新加载Nginx配置
nginx -s reload
```

---

## 验证清单

部署完成后，请逐项验证：

- [ ] Jenkins任务创建成功
- [ ] GitHub webhook配置成功
- [ ] 服务器初始化完成
- [ ] 首次构建成功
- [ ] 前端可以访问：http://106.15.90.212/admin/
- [ ] 后端API正常：http://106.15.90.212:8081/api/health
- [ ] 邮件通知配置成功（可选）
- [ ] 定时构建配置成功（可选）

---

## 日常使用

### 手动部署

1. 修改代码并提交到GitHub
2. 推送到main分支
3. Jenkins自动触发构建（或手动点击"立即构建"）
4. 等待构建完成（约5-10分钟）
5. 查看构建日志
6. 验证部署成功

### 查看构建历史

1. 进入Jenkins任务页面
2. 点击"Build History"查看历史构建
3. 点击构建号查看详细日志

### 回滚到上一版本

如果新版本有问题，可以快速回滚：

```bash
# SSH登录服务器
ssh root@106.15.90.212

# 进入部署目录
cd /opt/smart-order-hub/backend

# 恢复备份
mv smart-order-hub-1.0.0.jar.backup smart-order-hub-1.0.0.jar

# 重启服务
./stop.sh
./start.sh
```

---

## 附录：快速参考

### Jenkinsfile位置
`https://github.com/lpf2025/smart_order_hub/blob/main/Jenkinsfile`

### 服务器初始化脚本位置
`https://github.com/lpf2025/smart_order_hub/blob/main/scripts/server-init.sh`

### 后端启动脚本位置
`https://github.com/lpf2025/smart_order_hub/blob/main/scripts/backend-start.sh`

### 后端停止脚本位置
`https://github.com/lpf2025/smart_order_hub/blob/main/scripts/backend-stop.sh`

---

## 重要提示

⚠️ **生产环境注意事项**：

1. **备份数据库**：首次部署前务必备份数据库
2. **测试环境验证**：新功能先在测试环境验证后再部署到生产
3. **监控日志**：定期检查应用日志，及时发现异常
4. **定期备份**：定期备份应用和数据库
5. **密码安全**：不要在代码中硬编码密码，使用Jenkins凭据
6. **网络隔离**：生产服务器使用独立网络，避免开发环境影响

---

配置完成后，您就可以通过Jenkins实现一键自动化部署了！
