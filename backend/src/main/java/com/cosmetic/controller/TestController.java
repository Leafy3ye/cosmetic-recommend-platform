package com.cosmetic.controller;

import com.cosmetic.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试Controller
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("系统运行正常");
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "化妆品推荐平台");
        info.put("version", "1.0.0");
        info.put("author", "叶夏嘉");
        return Result.success(info);
    }
}
