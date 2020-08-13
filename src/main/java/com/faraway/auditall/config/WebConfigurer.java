package com.faraway.auditall.config;

import com.faraway.auditall.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/register/find",
                        "/register/insert",
                        "/basicinfo/login",
                        "/auditinfo/insert",
                        "/auditinfo/getnum",
                        "/auditphoto/insert",
                        "/auditphoto/insertandexcel",
                        "/auditinfo/generate",
                        "/basicinfo/superlogin",
                        "/audititem/insert",
                        "/check/checkInfo",
                        "/check/insert",
                        "/checkphoto/insert",
                        "/check/testExcel");
    }
}
