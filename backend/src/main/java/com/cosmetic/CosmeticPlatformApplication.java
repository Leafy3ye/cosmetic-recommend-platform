package com.cosmetic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 化妆品推荐平台启动类
 * @author YeXiaJia
 */
@SpringBootApplication
@MapperScan("com.cosmetic.mapper")
public class CosmeticPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmeticPlatformApplication.class, args);
        System.out.println("========================================");
        System.out.println("化妆品推荐平台启动成功！");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("========================================");
    }
}

