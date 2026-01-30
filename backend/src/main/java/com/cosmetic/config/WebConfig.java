package com.cosmetic.config;

import com.cosmetic.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired(required = false)
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (jwtInterceptor != null) {
            registry.addInterceptor(jwtInterceptor)
                    .addPathPatterns("/**")
                    .excludePathPatterns(
                            "/user/login",
                            "/user/register",
                            "/product/page",
                            "/product/hot",
                            "/product/*",
                            "/test/**",
                            "/error"
                    );
        }
    }
}
