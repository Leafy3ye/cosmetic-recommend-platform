package com.cosmetic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 数据库初始化器 - 自动创建数据库和表
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // 创建数据库
        createDatabase();
        // 创建表
        createTables();
        // 更新表结构（添加新字段）
        alterTables();
        // 初始化商品数据的商家ID
        initProductMerchantId();
    }

    /**
     * 创建数据库
     */
    private void createDatabase() {
        try {
            // 由于连接URL中已添加 createDatabaseIfNotExist=true 参数
            // MySQL会自动创建数据库（如果不存在）
            dataSource.getConnection().close();
            System.out.println("数据库创建/检查成功：cosmetic_platform");
        } catch (Exception e) {
            System.err.println("数据库连接失败：" + e.getMessage());
        }
    }

    /**
     * 创建表
     */
    private void createTables() {
        try {
            // 用户表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_user (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID'," +
                    "username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名'," +
                    "password VARCHAR(200) NOT NULL COMMENT '密码'," +
                    "nickname VARCHAR(50) COMMENT '昵称'," +
                    "avatar VARCHAR(500) COMMENT '头像'," +
                    "email VARCHAR(100) COMMENT '邮箱'," +
                    "phone VARCHAR(20) COMMENT '手机号'," +
                    "gender INT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女'," +
                    "role INT DEFAULT 0 COMMENT '角色：0-用户，1-商家，2-管理员'," +
                    "status INT DEFAULT 0 COMMENT '状态：0-正常，1-禁用'," +
                    "receiver_name VARCHAR(50) COMMENT '收货人姓名'," +
                    "receiver_phone VARCHAR(20) COMMENT '收货人电话'," +
                    "province VARCHAR(50) COMMENT '省份'," +
                    "city VARCHAR(50) COMMENT '城市'," +
                    "district VARCHAR(50) COMMENT '区/县'," +
                    "detail_address VARCHAR(500) COMMENT '详细地址'," +
                    "is_default INT DEFAULT 0 COMMENT '是否默认地址：0-否，1-是'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "deleted INT DEFAULT 0 COMMENT '逻辑删除'," +
                    "INDEX idx_username (username)," +
                    "INDEX idx_role (role)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'");

            // 商品分类表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_category (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID'," +
                    "name VARCHAR(50) NOT NULL COMMENT '分类名称'," +
                    "parent_id BIGINT DEFAULT 0 COMMENT '父分类ID'," +
                    "level INT DEFAULT 1 COMMENT '分类层级'," +
                    "sort INT DEFAULT 0 COMMENT '排序号'," +
                    "icon VARCHAR(200) COMMENT '分类图标'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "deleted INT DEFAULT 0 COMMENT '逻辑删除'," +
                    "INDEX idx_parent (parent_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表'");

            // 商品表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_product (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID'," +
                    "name VARCHAR(200) NOT NULL COMMENT '商品名称'," +
                    "description TEXT COMMENT '商品描述'," +
                    "category_id BIGINT COMMENT '分类ID'," +
                    "brand VARCHAR(100) COMMENT '品牌'," +
                    "image VARCHAR(500) COMMENT '商品主图'," +
                    "images TEXT COMMENT '商品图片列表'," +
                    "price DECIMAL(10,2) NOT NULL COMMENT '商品价格'," +
                    "original_price DECIMAL(10,2) COMMENT '原价'," +
                    "stock INT DEFAULT 0 COMMENT '库存数量'," +
                    "sales INT DEFAULT 0 COMMENT '销量'," +
                    "merchant_id BIGINT COMMENT '商家ID'," +
                    "status INT DEFAULT 1 COMMENT '状态：0-下架，1-上架'," +
                    "tags VARCHAR(500) COMMENT '商品标签'," +
                    "skin_type VARCHAR(100) COMMENT '适用肤质'," +
                    "effect VARCHAR(200) COMMENT '功效'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "deleted INT DEFAULT 0 COMMENT '逻辑删除'," +
                    "INDEX idx_category (category_id)," +
                    "INDEX idx_merchant (merchant_id)," +
                    "INDEX idx_status (status)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表'");

            // 订单表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_order (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID'," +
                    "order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号'," +
                    "user_id BIGINT NOT NULL COMMENT '用户ID'," +
                    "merchant_id BIGINT COMMENT '商家ID'," +
                    "total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额'," +
                    "pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额'," +
                    "receiver_name VARCHAR(50) COMMENT '收货人'," +
                    "receiver_phone VARCHAR(20) COMMENT '收货电话'," +
                    "receiver_address VARCHAR(500) COMMENT '收货地址'," +
                    "status INT DEFAULT 0 COMMENT '订单状态：0-待付款，1-待发货，2-待收货，3-待评价，4-已完成，5-已取消'," +
                    "pay_type INT COMMENT '支付方式'," +
                    "pay_time DATETIME COMMENT '支付时间'," +
                    "delivery_time DATETIME COMMENT '发货时间'," +
                    "finish_time DATETIME COMMENT '完成时间'," +
                    "remark VARCHAR(500) COMMENT '备注'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "deleted INT DEFAULT 0 COMMENT '逻辑删除'," +
                    "INDEX idx_order_no (order_no)," +
                    "INDEX idx_user (user_id)," +
                    "INDEX idx_status (status)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表'");

            // 订单明细表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_order_item (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID'," +
                    "order_id BIGINT NOT NULL COMMENT '订单ID'," +
                    "product_id BIGINT NOT NULL COMMENT '商品ID'," +
                    "product_name VARCHAR(200) COMMENT '商品名称'," +
                    "product_image VARCHAR(500) COMMENT '商品图片'," +
                    "price DECIMAL(10,2) NOT NULL COMMENT '商品价格'," +
                    "quantity INT NOT NULL COMMENT '购买数量'," +
                    "subtotal DECIMAL(10,2) NOT NULL COMMENT '小计'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "deleted INT DEFAULT 0 COMMENT '逻辑删除'," +
                    "INDEX idx_order (order_id)," +
                    "INDEX idx_product (product_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表'");

            // 购物车表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_cart (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID'," +
                    "user_id BIGINT NOT NULL COMMENT '用户ID'," +
                    "product_id BIGINT NOT NULL COMMENT '商品ID'," +
                    "quantity INT NOT NULL DEFAULT 1 COMMENT '数量'," +
                    "checked INT DEFAULT 1 COMMENT '是否选中'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "INDEX idx_user (user_id)," +
                    "UNIQUE INDEX idx_user_product (user_id, product_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表'");

            // 收藏表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_favorite (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID'," +
                    "user_id BIGINT NOT NULL COMMENT '用户ID'," +
                    "product_id BIGINT NOT NULL COMMENT '商品ID'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "INDEX idx_user (user_id)," +
                    "UNIQUE INDEX idx_user_product (user_id, product_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表'");

            // 评论表
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_review (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID'," +
                    "user_id BIGINT NOT NULL COMMENT '用户ID'," +
                    "product_id BIGINT NOT NULL COMMENT '商品ID'," +
                    "order_id BIGINT COMMENT '订单ID'," +
                    "rating INT NOT NULL COMMENT '评分'," +
                    "content TEXT COMMENT '评论内容'," +
                    "images TEXT COMMENT '评论图片'," +
                    "likes INT DEFAULT 0 COMMENT '点赞数'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                    "deleted INT DEFAULT 0 COMMENT '逻辑删除'," +
                    "INDEX idx_user (user_id)," +
                    "INDEX idx_product (product_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表'");

            // 用户行为表（用于推荐算法）
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tb_user_behavior (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '行为ID'," +
                    "user_id BIGINT NOT NULL COMMENT '用户ID'," +
                    "product_id BIGINT NOT NULL COMMENT '商品ID'," +
                    "behavior_type INT NOT NULL COMMENT '行为类型：1-浏览，2-收藏，3-加购物车，4-购买'," +
                    "weight INT DEFAULT 1 COMMENT '行为权重'," +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                    "INDEX idx_user (user_id)," +
                    "INDEX idx_product (product_id)," +
                    "INDEX idx_behavior (behavior_type)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户行为表'");

            System.out.println("数据库表创建/检查成功");
        } catch (Exception e) {
            System.err.println("数据库表创建失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 更新表结构 - 添加新字段
     */
    private void alterTables() {
        try {
            // 检查并添加用户表的地址字段
            addColumnIfNotExists("tb_user", "receiver_name", 
                "VARCHAR(50) COMMENT '收货人姓名'");
            addColumnIfNotExists("tb_user", "receiver_phone", 
                "VARCHAR(20) COMMENT '收货人电话'");
            addColumnIfNotExists("tb_user", "province", 
                "VARCHAR(50) COMMENT '省份'");
            addColumnIfNotExists("tb_user", "city", 
                "VARCHAR(50) COMMENT '城市'");
            addColumnIfNotExists("tb_user", "district", 
                "VARCHAR(50) COMMENT '区/县'");
            addColumnIfNotExists("tb_user", "detail_address", 
                "VARCHAR(500) COMMENT '详细地址'");
            addColumnIfNotExists("tb_user", "is_default", 
                "INT DEFAULT 0 COMMENT '是否默认地址：0-否，1-是'");
            
            // 修改 avatar 字段类型以支持 base64 图片存储
            modifyColumnType("tb_user", "avatar", "LONGTEXT COMMENT '头像'");
            
            System.out.println("数据库表结构更新成功");
        } catch (Exception e) {
            System.err.println("数据库表结构更新失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 添加列（如果不存在）
     */
    private void addColumnIfNotExists(String tableName, String columnName, String columnDefinition) {
        try {
            // 检查列是否存在
            String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName, columnName);
            
            if (count == null || count == 0) {
                // 列不存在，添加列
                String alterSql = String.format("ALTER TABLE %s ADD COLUMN %s %s", 
                    tableName, columnName, columnDefinition);
                jdbcTemplate.execute(alterSql);
                System.out.println("添加列: " + tableName + "." + columnName);
            }
        } catch (Exception e) {
            System.err.println("添加列失败 " + tableName + "." + columnName + ": " + e.getMessage());
        }
    }

    /**
     * 修改列类型
     */
    private void modifyColumnType(String tableName, String columnName, String columnDefinition) {
        try {
            // 检查列是否存在
            String checkSql = "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?";
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, tableName, columnName);
            
            if (count != null && count > 0) {
                // 列存在，修改列类型
                String alterSql = String.format("ALTER TABLE %s MODIFY COLUMN %s %s", 
                    tableName, columnName, columnDefinition);
                jdbcTemplate.execute(alterSql);
                System.out.println("修改列类型: " + tableName + "." + columnName);
            }
        } catch (Exception e) {
            System.err.println("修改列类型失败 " + tableName + "." + columnName + ": " + e.getMessage());
        }
    }

    /**
     * 初始化商品的商家ID
     * 将所有没有merchant_id或merchant_id为NULL的商品关联到商家测试用户
     */
    private void initProductMerchantId() {
        try {
            // 查找商家测试用户(角色为1的用户)
            String findMerchantSql = "SELECT id FROM tb_user WHERE role = 1 AND deleted = 0 LIMIT 1";
            Long merchantId = null;
            
            try {
                merchantId = jdbcTemplate.queryForObject(findMerchantSql, Long.class);
            } catch (Exception e) {
                System.out.println("未找到商家用户,跳过商品merchant_id初始化");
                return;
            }
            
            if (merchantId != null) {
                // 更新所有没有merchant_id的商品
                String updateSql = "UPDATE tb_product SET merchant_id = ? WHERE merchant_id IS NULL OR merchant_id = 0";
                int updatedCount = jdbcTemplate.update(updateSql, merchantId);
                
                if (updatedCount > 0) {
                    System.out.println("已将 " + updatedCount + " 个商品关联到商家(ID: " + merchantId + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("初始化商品merchant_id失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

