package com.cosmetic.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类
 */
public class PasswordUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 密码加密
     */
    public static String encrypt(String password) {
        return encoder.encode(password);
    }

    /**
     * 密码验证
     */
    public static boolean verify(String password, String hashed) {
        return encoder.matches(password, hashed);
    }
}
