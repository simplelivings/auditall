package com.faraway.auditall.config;

import com.faraway.auditall.interceptors.ReplaceStreamFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * 配置过滤器，过滤所有请求；
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-17 13:10
 */
@Configuration
public class FilterConfig {


    /**
     * 注册过滤器
     *
     * @return FilterRegistrationBean
     *
     */
    @Bean
    public FilterRegistrationBean someFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(replaceStreamFilter());
        registration.addUrlPatterns("/*");
        registration.setName("streamFilter");
        return registration;
    }

    @Bean(name = "replaceStreamFilter")
    public Filter replaceStreamFilter() {
        return new ReplaceStreamFilter();
    }

}
