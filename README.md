# 基于SpringBoot的化妆品推荐平台

> 毕业设计项目 - 一个集成推荐算法的化妆品电商平台

## 项目简介

本项目是一个基于SpringBoot + Vue3的前后端分离的化妆品电商推荐平台，集成了完整的电商功能和协同过滤推荐算法。系统支持用户注册登录、商品浏览、购物车、订单管理等基础电商功能，并预留了基于用户协同过滤（UserCF）和物品协同过滤（ItemCF）的个性化推荐算法接口。

## 技术架构

### 后端技术栈
- **核心框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM框架**: MyBatis-Plus 3.5.3
- **缓存**: Redis
- **身份认证**: JWT (Json Web Token)
- **工具库**: Lombok, Hutool, FastJSON
- **构建工具**: Maven

### 前端技术栈
- **核心框架**: Vue 3.3
- **构建工具**: Vite 4.4
- **路由管理**: Vue Router 4.2
- **状态管理**: Pinia 2.1
- **UI组件库**: Element Plus 2.4
- **HTTP客户端**: Axios 1.5
- **图表库**: ECharts 5.4

## 项目结构

```
cosmetic-recommend-platform/
├── backend/                    # 后端项目
│   ├── src/main/java/com/cosmetic/
│   │   ├── common/            # 公共类（统一返回结果、分页等）
│   │   ├── config/            # 配置类（数据库、Redis、跨域等）
│   │   ├── controller/        # 控制器层
│   │   ├── dto/               # 数据传输对象
│   │   ├── entity/            # 实体类
│   │   ├── exception/         # 异常处理
│   │   ├── interceptor/       # 拦截器（JWT认证）
│   │   ├── mapper/            # 数据访问层
│   │   ├── service/           # 服务层
│   │   │   └── impl/         # 服务实现类
│   │   └── utils/             # 工具类
│   └── src/main/resources/
│       └── application.yml    # 配置文件
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/              # API接口封装
│   │   ├── components/       # 公共组件
│   │   ├── views/            # 页面组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # 状态管理
│   │   └── utils/            # 工具函数
│   └── package.json
└── README.md                  # 项目说明文档
```

## 核心功能

### 1. 用户模块
- ✅ 用户注册（支持普通用户和商家角色）
- ✅ 用户登录（JWT认证）
- ✅ 个人信息管理
- ✅ 权限控制（用户/商家/管理员）

### 2. 商品模块
- ✅ 商品CRUD操作
- ✅ 商品分类管理
- ✅ 商品搜索（关键词、分类筛选）
- ✅ 商品分页查询
- ✅ 热销商品推荐

### 3. 购物车模块
- ✅ 添加商品到购物车
- ✅ 修改购物车商品数量
- ✅ 删除购物车商品
- ✅ 清空购物车
- ✅ 购物车商品列表

### 4. 订单模块
- ✅ 创建订单
- ✅ 订单状态管理（待付款→待发货→待收货→待评价→已完成）
- ✅ 支付订单（模拟）
- ✅ 取消订单
- ✅ 确认收货
- ✅ 订单分页查询

### 5. 推荐算法模块（预留接口）
- ✅ 用户行为记录（浏览、收藏、加购、购买）
- ✅ 基于UserCF的推荐接口
- ✅ 基于ItemCF的推荐接口
- ✅ 用户相似度计算接口
- ✅ 商品相似度计算接口
- ✅ 推荐模型更新接口
- 🔄 推荐算法具体实现（待完善）

## 数据库设计

### 主要数据表
| 表名 | 说明 |
|------|------|
| tb_user | 用户表 |
| tb_product | 商品表 |
| tb_category | 商品分类表 |
| tb_order | 订单表 |
| tb_order_item | 订单明细表 |
| tb_cart | 购物车表 |
| tb_favorite | 收藏表 |
| tb_review | 评论表 |
| tb_user_behavior | 用户行为表（推荐算法数据源） |

## 快速开始

### 环境要求
- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Redis 5.0+ （可选，用于缓存优化）
- Maven 3.6+

### 后端启动

1. **配置数据库**

编辑 `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cosmetic_platform
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD}
```

2. **启动Redis**（可选）
```bash
redis-server
```

3. **启动后端服务**
```bash
cd backend
mvn clean package
java -jar target/cosmetic-recommend-platform-1.0.0.jar
```

后端服务地址: http://localhost:8080/api

**注意**: 首次启动会自动创建数据库和所有数据表！

### 前端启动

1. **安装依赖**
```bash
cd frontend
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

前端访问地址: http://localhost:5173

3. **生产构建**
```bash
npm run build
```

## API接口文档

### 用户接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| GET | /api/user/{id} | 获取用户信息 |
| PUT | /api/user/update | 更新用户信息 |

### 商品接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/product/page | 分页查询商品 |
| GET | /api/product/{id} | 获取商品详情 |
| GET | /api/product/hot | 获取热销商品 |
| GET | /api/product/recommend/{userId} | 获取推荐商品 |
| POST | /api/product/add | 添加商品 |
| PUT | /api/product/update | 更新商品 |
| DELETE | /api/product/{id} | 删除商品 |

### 购物车接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/cart/add | 添加到购物车 |
| GET | /api/cart/list/{userId} | 获取购物车列表 |
| PUT | /api/cart/update | 更新数量 |
| DELETE | /api/cart/{cartId} | 删除商品 |
| DELETE | /api/cart/clear/{userId} | 清空购物车 |

### 订单接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/order/create | 创建订单 |
| GET | /api/order/{id} | 获取订单详情 |
| GET | /api/order/page/{userId} | 分页查询订单 |
| PUT | /api/order/pay/{orderId} | 支付订单 |
| PUT | /api/order/cancel/{orderId} | 取消订单 |
| PUT | /api/order/confirm/{orderId} | 确认收货 |

### 推荐接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/recommend/user/{userId} | 获取个性化推荐 |
| GET | /api/recommend/user-cf/{userId} | UserCF推荐 |
| GET | /api/recommend/item-cf/{userId} | ItemCF推荐 |
| POST | /api/recommend/update-model | 更新推荐模型 |

## 推荐算法说明

### 用户行为权重设计
- **浏览**: 权重 1 分
- **收藏**: 权重 2 分
- **加入购物车**: 权重 3 分
- **购买**: 权重 5 分

### 推荐策略
1. **新用户**: 返回热销商品（冷启动策略）
2. **老用户**: 基于UserCF和ItemCF混合推荐
3. **兜底策略**: 推荐失败时返回热销商品

### 算法实现计划
- [ ] 实现余弦相似度计算
- [ ] 实现皮尔逊相关系数
- [ ] 构建用户-商品评分矩阵
- [ ] 实现UserCF协同过滤算法
- [ ] 实现ItemCF协同过滤算法
- [ ] 混合推荐策略
- [ ] Redis缓存相似度矩阵
- [ ] 定时任务更新推荐模型

## 项目特色

### 1. 前后端分离架构
- 后端提供RESTful API
- 前端SPA单页应用
- 数据交互清晰规范

### 2. 安全性设计
- JWT Token认证
- BCrypt密码加密
- 路由权限控制
- 跨域安全配置

### 3. 数据库自动初始化
- 首次启动自动创建数据库
- 自动创建所有数据表
- 无需手动执行SQL脚本

### 4. 现代化UI设计
- Element Plus组件库
- 响应式布局
- 渐变色设计
- 优秀的用户体验

### 5. 可扩展性强
- 推荐算法接口预留
- 清晰的分层架构
- 便于功能扩展

## 待开发功能

### 高优先级
- [ ] 完善推荐算法的具体实现
- [ ] 评论和评分功能
- [ ] 商品分类导航
- [ ] 图片上传功能
- [ ] 订单详情页面
- [ ] 收藏功能完善

### 中优先级
- [ ] 商家后台管理
- [ ] 管理员后台
- [ ] 数据统计可视化（ECharts）
- [ ] 支付功能对接
- [ ] 物流信息跟踪

### 低优先级
- [ ] 用户评价系统
- [ ] 积分系统
- [ ] 优惠券功能
- [ ] 秒杀活动
- [ ] 站内消息通知

## 技术亮点

1. **自动建表机制**: 使用`CommandLineRunner`实现数据库和表的自动创建
2. **JWT无状态认证**: 基于Token的无状态认证机制
3. **MyBatis-Plus集成**: 简化CRUD操作，提高开发效率
4. **统一异常处理**: 全局异常处理器，统一返回格式
5. **路由守卫**: 前端路由守卫，自动登录验证
6. **Pinia状态管理**: Vue3官方推荐的状态管理方案
7. **Vite构建**: 极速的开发体验和构建速度

## 注意事项

1. 首次运行后端会自动创建数据库 `cosmetic_platform`
2. 确保MySQL和Redis服务已启动
3. JWT Token有效期为24小时
4. 推荐算法接口已预留，需根据实际需求完善实现
5. 图片上传功能需配置文件存储路径

## 测试账号

可以使用注册功能创建账号，或者手动添加测试数据。

## 项目截图

（待添加）

## 开发进度

- [x] 后端项目基础架构搭建
- [x] 数据库设计和自动建表
- [x] 用户注册登录功能
- [x] 商品管理功能
- [x] 购物车功能
- [x] 订单管理功能
- [x] 推荐算法接口预留
- [x] 前端项目基础架构搭建
- [x] 前端页面开发
- [x] 前后端联调
- [ ] 推荐算法完善
- [ ] 功能测试
- [ ] 性能优化
- [ ] 文档完善

## 常见问题

**Q: 启动后端时提示数据库连接失败？**
A: 请检查MySQL服务是否启动，以及配置文件中的数据库密码是否正确。

**Q: 前端无法访问后端接口？**
A: 请检查后端服务是否启动，以及前端的API地址配置是否正确。

**Q: 推荐功能不准确？**
A: 当前推荐算法为简化版本，需要根据实际业务需求完善UserCF和ItemCF算法实现。

## 贡献指南

欢迎提出建议和改进意见！

## 作者信息

- **作者**: 叶夏嘉
- **项目类型**: 毕业设计
- **完成时间**: 2026年

## 许可证

本项目仅用于学习和毕业设计使用，未经许可不得用于商业用途。

## 致谢

感谢所有开源项目和技术社区的支持！

---

