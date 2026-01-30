# 化妆品推荐平台 - 后端项目

## 项目简介
基于SpringBoot的化妆品推荐平台后端系统，采用前后端分离架构，集成了用户管理、商品管理、订单管理、购物车等电商基础功能，并预留了协同过滤推荐算法接口。

## 技术栈
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: MyBatis-Plus 3.5.3
- **缓存**: Redis
- **认证**: JWT
- **工具**: Lombok, Hutool, FastJSON

## 项目结构
```
backend/
├── src/main/java/com/cosmetic/
│   ├── common/              # 公共类（统一返回结果等）
│   ├── config/              # 配置类（MyBatis-Plus、CORS、Redis等）
│   ├── controller/          # 控制器层
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类
│   ├── exception/           # 异常处理
│   ├── interceptor/         # 拦截器（JWT认证）
│   ├── mapper/              # 数据访问层
│   ├── service/             # 服务层
│   │   └── impl/           # 服务实现类
│   └── utils/               # 工具类
└── src/main/resources/
    └── application.yml      # 配置文件
```

## 主要功能模块

### 1. 用户模块
- 用户注册
- 用户登录（JWT认证）
- 用户信息管理
- 支持三种角色：普通用户、商家、管理员

### 2. 商品模块
- 商品CRUD操作
- 商品分类管理
- 商品搜索（关键词、分类筛选）
- 商品分页查询
- 热销商品推荐

### 3. 购物车模块
- 添加商品到购物车
- 更新购物车商品数量
- 删除购物车商品
- 查看购物车列表

### 4. 订单模块
- 创建订单
- 订单状态流转（待付款→待发货→待收货→待评价→已完成）
- 取消订单
- 订单分页查询

### 5. 推荐算法模块（预留接口）
- 基于用户协同过滤（UserCF）的推荐
- 基于物品协同过滤（ItemCF）的推荐
- 用户行为记录（浏览、收藏、加购、购买）
- 用户/商品相似度计算接口
- 推荐模型更新接口

## 数据库设计

### 主要数据表
1. **tb_user** - 用户表
2. **tb_product** - 商品表
3. **tb_category** - 分类表
4. **tb_order** - 订单表
5. **tb_order_item** - 订单明细表
6. **tb_cart** - 购物车表
7. **tb_favorite** - 收藏表
8. **tb_review** - 评论表
9. **tb_user_behavior** - 用户行为表（用于推荐算法）

## 快速开始

### 环境要求
- JDK 1.8+
- MySQL 8.0+
- Redis 5.0+
- Maven 3.6+

### 配置数据库
修改 `application.yml` 中的数据库配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cosmetic_platform
    username: root
    password: your_password
```

### 启动项目
```bash
# 进入项目目录
cd backend

# 使用Maven编译
mvn clean package

# 运行项目
java -jar target/cosmetic-recommend-platform-1.0.0.jar
```

项目启动后会自动创建数据库和表结构。

### 访问接口
- 后端地址: http://localhost:8080/api
- Swagger文档: 待配置

## API接口说明

### 用户接口
- POST `/api/user/register` - 用户注册
- POST `/api/user/login` - 用户登录
- GET `/api/user/{id}` - 获取用户信息
- PUT `/api/user/update` - 更新用户信息

### 商品接口
- GET `/api/product/page` - 分页查询商品
- GET `/api/product/{id}` - 获取商品详情
- GET `/api/product/hot` - 获取热销商品
- GET `/api/product/recommend/{userId}` - 获取推荐商品
- POST `/api/product/add` - 添加商品（需要商家/管理员权限）
- PUT `/api/product/update` - 更新商品
- DELETE `/api/product/{id}` - 删除商品

### 购物车接口
- POST `/api/cart/add` - 添加到购物车
- GET `/api/cart/list/{userId}` - 获取购物车列表
- PUT `/api/cart/update` - 更新数量
- DELETE `/api/cart/{cartId}` - 删除商品

### 订单接口
- POST `/api/order/create` - 创建订单
- GET `/api/order/{id}` - 获取订单详情
- GET `/api/order/page/{userId}` - 分页查询订单
- PUT `/api/order/pay/{orderId}` - 支付订单
- PUT `/api/order/cancel/{orderId}` - 取消订单
- PUT `/api/order/confirm/{orderId}` - 确认收货

### 推荐接口
- GET `/api/recommend/user/{userId}` - 获取个性化推荐
- GET `/api/recommend/user-cf/{userId}` - UserCF推荐
- GET `/api/recommend/item-cf/{userId}` - ItemCF推荐
- POST `/api/recommend/update-model` - 更新推荐模型

## 推荐算法说明

### 用户行为权重
- 浏览：1分
- 收藏：2分
- 加购物车：3分
- 购买：5分

### 推荐策略
1. **新用户**：返回热销商品
2. **老用户**：基于UserCF和ItemCF混合推荐
3. **兜底策略**：当推荐失败时，返回热销商品

### 待实现功能
- [ ] 完善UserCF算法实现
- [ ] 完善ItemCF算法实现
- [ ] 实现用户/商品相似度矩阵计算
- [ ] 添加Redis缓存优化性能
- [ ] 实现定时任务更新推荐模型
- [ ] 添加推荐结果的多样性和新颖性优化

## 注意事项
1. 首次运行会自动创建数据库和表结构
2. JWT Token有效期为24小时
3. 密码使用BCrypt加密存储
4. 推荐算法接口已预留，待后续完善

## 后续优化方向
1. 完善推荐算法的具体实现
2. 添加Swagger API文档
3. 实现文件上传功能（商品图片）
4. 添加评论和评分功能
5. 实现数据统计和可视化
6. 优化查询性能（添加索引、使用Redis缓存）
7. 添加单元测试

## 作者
叶夏嘉 - 毕业设计项目

## 许可证
本项目仅用于学习和毕业设计使用

