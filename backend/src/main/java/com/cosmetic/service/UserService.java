package com.cosmetic.service;

import com.cosmetic.dto.LoginRequest;
import com.cosmetic.dto.LoginResponse;
import com.cosmetic.dto.RegisterRequest;
import com.cosmetic.entity.User;

/**
 * 用户Service接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据ID获取用户
     */
    User getUserById(Long id);

    /**
     * 根据用户名获取用户
     */
    User getUserByUsername(String username);

    /**
     * 更新用户信息
     */
    void updateUser(User user);
}

