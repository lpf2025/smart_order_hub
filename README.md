# 智能点餐系统

一个完整的智能点餐系统，包含用户端小程序、商家端小程序和PC管理后台。

## 项目结构

```
smart_rder_hub/
├── backend/                 # 后端服务（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartorder/
│   │   │   │   ├── config/          # 配置类
│   │   │   │   ├── controller/      # 控制器
│   │   │   │   ├── dto/            # 数据传输对象
│   │   │   │   ├── entity/         # 实体类
│   │   │   │   ├── mapper/         # 数据访问层
│   │   │   │   ├── service/        # 业务逻辑层
│   │   │   │   └── common/         # 通用类
│   │   │   └── resources/
│   │   │       ├── application.yml   # 配置文件
│   │   │       └── schema.sql       # 数据库脚本
│   └── pom.xml                    # Maven配置
├── miniprogram-user/        # 用户端小程序
│   ├── pages/               # 页面
│   ├── app.js              # 应用逻辑
│   └── app.json           # 应用配置
├── miniprogram-merchant/   # 商家端小程序
│   ├── pages/              # 页面
│   ├── app.js             # 应用逻辑
│   └── app.json          # 应用配置
└── pc-admin/             # PC管理后台（Vue 3 + Element Plus）
    ├── src/
    │   ├── views/        # 页面组件
    │   ├── router/       # 路由配置
    │   ├── utils/        # 工具函数
    │   ├── App.vue       # 根组件
    │   └── main.js      # 入口文件
    ├── package.json      # 依赖配置
    └── vite.config.js   # Vite配置
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Redis
- WebSocket

### 用户端小程序
- 微信小程序原生框架
- WebSocket实时通信

### 商家端小程序
- 微信小程序原生框架
- WebSocket实时通信

### PC管理后台
- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- Vite

## 功能特性

### 用户端小程序
- 扫码点餐
- 菜单浏览
- 购物车管理
- 订单创建
- 订单状态实时更新
- 订单查询

### 商家端小程序
- 订单管理（接单、出餐、配送、完成）
- 菜品管理
- 实时订单提醒
- 数据统计

### PC管理后台
- 门店管理
- 菜品管理
- 订单管理
- 用户管理
- 数据统计

## 快速开始

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- 微信开发者工具

### 后端启动

1. 创建数据库并执行SQL脚本：
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

2. 修改配置文件 `backend/src/main/resources/application.yml` 中的数据库连接信息

3. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 用户端小程序启动

1. 使用微信开发者工具打开 `miniprogram-user` 目录

2. 修改 `app.js` 中的 `baseUrl` 为实际后端地址

3. 点击编译运行

### 商家端小程序启动

1. 使用微信开发者工具打开 `miniprogram-merchant` 目录

2. 修改 `app.js` 中的 `baseUrl` 为实际后端地址

3. 点击编译运行

### PC管理后台启动

1. 安装依赖：
```bash
cd pc-admin
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

PC管理后台将在 http://localhost:3000 启动

## API接口文档

### 用户相关
- POST `/api/user/login` - 用户登录
- GET `/api/user/{id}` - 获取用户信息

### 门店相关
- GET `/api/merchant/{id}` - 获取门店详情
- GET `/api/merchant/list` - 获取门店列表

### 菜品相关
- GET `/api/dish/{id}` - 获取菜品详情
- GET `/api/dish/merchant/{merchantId}` - 获取门店菜品列表
- GET `/api/dish/merchant/{merchantId}/category/{category}` - 按分类获取菜品
- GET `/api/dish/merchant/{merchantId}/categories` - 获取菜品分类

### 订单相关
- POST `/api/order/create` - 创建订单
- GET `/api/order/{id}` - 获取订单详情
- GET `/api/order/orderNo/{orderNo}` - 根据订单号获取订单
- GET `/api/order/user/{userId}` - 获取用户订单列表
- GET `/api/order/merchant/{merchantId}` - 获取门店订单列表
- PUT `/api/order/{id}/status` - 更新订单状态
- PUT `/api/order/{id}/cancel` - 取消订单

### WebSocket
- WS `/ws/order?userId={userId}` - 订单实时通知

## 数据库设计

### 核心表
- `t_merchant` - 门店表
- `t_dish` - 菜品表
- `t_dish_spec` - 菜品规格表
- `t_user` - 用户表
- `t_user_address` - 用户地址表
- `t_order` - 订单表
- `t_order_item` - 订单项明细表
- `t_delivery` - 配送信息表
- `t_merchant_settlement` - 商家结算表
- `t_system_log` - 系统日志表

## 订单状态流转

1. 待接单 (status=1)
2. 待出餐 (status=2)
3. 待配送 (status=3)
4. 配送中 (status=4)
5. 已完成 (status=5)
6. 已取消 (status=6)

## 注意事项

1. 微信小程序需要配置服务器域名白名单
2. 支付功能需要申请微信支付
3. 订阅消息需要配置模板
4. 生产环境建议使用HTTPS
5. 建议配置Redis缓存提升性能

## 扩展功能建议

- 对接美团/饿了么开放平台
- 会员储值、优惠券、营销活动
- 智能取餐柜、配送轨迹追踪
- 数据分析模块

## 许可证

MIT License
