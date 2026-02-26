# Step1：订单-行为-推荐数据闭环修复

## 一、背景与目标

### 背景

化妆品推荐平台的推荐算法需要依赖真实的用户购买行为数据进行训练。但在修复前，系统存在以下断层：

1. **订单明细断层**：创建订单时只写入 `tb_order`，不写入 `tb_order_item`，导致无法追溯用户购买了哪些具体商品。
2. **购买行为缺失**：支付成功后未记录 `behavior_type=4`（购买）行为，推荐模块无法获取购买信号。
3. **商品销量不更新**：支付成功后 `tb_product.sales` 不累加，热销排序不准确。
4. **前后端状态语义不一致**：后端定义 `1=待发货`，前端展示为 `1=待收货`，导致用户看到错误状态。
5. **无幂等保护**：重复支付可能导致行为/销量重复写入。

### 目标

围绕"购买行为埋点 + 商品销量更新 + 订单链路一致性"完成修复，确保：

- 订单创建时同时落库明细（`tb_order_item`）
- 支付成功时记录购买行为（`behavior_type=4`）
- 支付成功时更新商品销量（`sales += quantity`）
- 重复支付幂等（不重复记行为/加销量）
- 前后端订单状态语义完全统一

---

## 二、改动点

### 后端改动

| 文件 | 改动摘要 |
|------|----------|
| `dto/OrderCreateRequest.java` | **新增**。订单创建请求 DTO，包含 `items` 明细列表（productId, productName, productImage, price, quantity） |
| `entity/Order.java` | 新增 `@TableField(exist=false) List<OrderItem> items` 瞬态字段，方便接口返回订单时携带明细 |
| `mapper/OrderItemMapper.java` | 新增 `selectByOrderId(Long orderId)` 方法 |
| `mapper/ProductMapper.java` | 新增 `incrementSales(Long productId, int quantity)` 原子累加销量方法 |
| `service/OrderService.java` | `createOrder` 参数改为 `OrderCreateRequest`；补充方法注释 |
| `service/impl/OrderServiceImpl.java` | **核心改动**：① 创建订单同时写入明细 ② 支付时加事务，记录购买行为+累加销量 ③ 幂等处理 ④ 查询订单时关联明细 |
| `controller/OrderController.java` | `createOrder` 接口入参改为 `@RequestBody OrderCreateRequest` |

### 前端改动

| 文件 | 改动摘要 |
|------|----------|
| `views/Cart.vue` | 结算时传递 `items` 商品明细列表（productId, productName, productImage, price, quantity） |
| `views/Orders.vue` | ① Tab 标签修正：`1=待发货, 2=待收货` ② 状态文案修正 ③ 新增订单商品明细展示区域 |
| `views/Profile.vue` | 订单快捷入口文案修正：`1=待发货`（原错误为"待收货"）；商家视图 `2=待收货`（原错误为"配送中"） |

### 文档

| 文件 | 说明 |
|------|------|
| `docs/step1-order-behavior-and-sales.md` | 本文档 |

---

## 三、统一订单状态定义

```
状态值    含义        可流转至          说明
------   --------    --------         ------
0        待付款      → 1（支付） / 5（取消）
1        待发货      → 2（发货）       支付成功后进入
2        待收货      → 3（确认收货）    商家发货后进入
3        待评价      → 4（评价完成）    用户确认收货后进入
4        已完成      终态
5        已取消      终态              仅从"待付款"可取消
```

---

## 四、数据流转图

```
用户在购物车选择商品 → 点击"结算"
        │
        ▼
  ┌─────────────────────────────────────┐
  │  POST /order/create                 │
  │  入参: OrderCreateRequest           │
  │    ├─ userId, totalAmount, ...      │
  │    └─ items: [{productId, price,    │
  │         quantity, productName, ...}] │
  └─────────────┬───────────────────────┘
                │
                ▼
  ┌─────────────────────────────┐
  │  写入 tb_order (status=0)   │
  │  写入 tb_order_item (N条)   │
  │  删除购物车已结算商品        │
  └─────────────┬───────────────┘
                │
                ▼
  用户在"我的订单"页点击"去支付"
                │
                ▼
  ┌─────────────────────────────────────────────┐
  │  PUT /order/pay/{orderId}?payType=1         │
  │                                             │
  │  @Transactional 事务内执行：                  │
  │  1. 校验 status==0（幂等：非0则静默返回）      │
  │  2. 更新 tb_order.status=1, payTime=now()   │
  │  3. 查询 tb_order_item（按 orderId）         │
  │  4. 遍历明细：                               │
  │     ├─ 记录 tb_user_behavior                │
  │     │   (userId, productId, type=4, weight=5)│
  │     └─ UPDATE tb_product                    │
  │         SET sales=sales+quantity             │
  └─────────────────────────────────────────────┘
                │
                ▼
  推荐模块（RecommendService）可查询
  tb_user_behavior WHERE behavior_type=4
  获取真实购买数据用于训练 UserCF/ItemCF
```

---

## 五、验证步骤与结果模板

### 手工验证路径

| 步骤 | 操作 | 预期结果 | 实际结果 |
|------|------|----------|----------|
| 1 | 购物车选择 2 个商品，提交订单 | 接口返回订单号 | |
| 2 | 查询 `tb_order` | 新增 1 条记录，status=0 | |
| 3 | 查询 `tb_order_item` | 新增 2 条记录，order_id 匹配 | |
| 4 | 支付该订单 | 接口返回成功 | |
| 5 | 查询 `tb_order` | status 变为 1，pay_time 已填充 | |
| 6 | 查询 `tb_user_behavior` | 新增 2 条 behavior_type=4 记录 | |
| 7 | 查询 `tb_product.sales` | 对应商品 sales 增加了购买数量 | |
| 8 | 再次支付同一订单 | 接口返回成功（幂等），行为/销量不重复增加 | |
| 9 | 前端订单列表 tab 筛选 | 各 tab 文案与后端状态一一对应 | |

### 边界场景检查

| 场景 | 处理策略 |
|------|----------|
| 订单无明细 | `createOrder` 抛出 `BusinessException("订单明细不能为空")` |
| 明细商品不存在 | `createOrder` 逐条校验，商品不存在则抛异常并回滚整个事务 |
| 支付已取消订单 | `payOrder` 抛出 `BusinessException("订单已取消，无法支付")` |
| 重复支付 | 幂等处理：status 已非 0 时静默返回，不重复写行为/销量 |
| 并发重复支付 | 当前策略依赖数据库行锁（selectById + updateById 在同一事务内）。高并发场景建议后续引入乐观锁（version 字段）或分布式锁 |

---

## 六、已知限制与下一步计划

### 当前限制

1. **并发幂等**：当前依赖单机事务+状态判断实现幂等。极端高并发场景下理论上可能存在 TOCTOU 竞态，建议后续给 `tb_order` 增加 `version` 乐观锁字段。
2. **单商家订单**：当前一个订单只有一个 `merchant_id`，多商家购物车结算时取第一个商品的商家。后续可按商家拆单。
3. **推荐算法**：本次仅打通数据链路，`RecommendServiceImpl` 中的 UserCF/ItemCF 算法仍为 TODO。
4. **库存扣减**：当前未实现下单扣库存逻辑，仅累加销量。

### 下一步计划

1. **Step2 - ItemCF 协同过滤实现**：基于 `tb_user_behavior` 中的购买数据实现基于物品的协同过滤推荐。
2. **Step3 - UserCF 协同过滤实现**：实现基于用户的协同过滤推荐。
3. **乐观锁**：给订单表增加 `version` 字段，防止并发状态变更。
4. **按商家拆单**：购物车多商家商品结算时自动拆分为多个订单。
5. **库存管理**：下单扣库存 + 支付超时自动释放。
