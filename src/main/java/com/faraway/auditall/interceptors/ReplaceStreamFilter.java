package com.faraway.auditall.interceptors;

import com.faraway.auditall.config.RequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 过滤器，用于解决POST请求时，数据流只能读取一次；
 * 将数据流读如包装类中，从包装类中直接读取数据；
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-17 13:08
 */

@Slf4j

public class ReplaceStreamFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("StreamFilter初始化...");
        System.out.println("StreamFilter初始化...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //过滤器，不过滤某些url的方法
//        HttpServletRequest servletRequest =(HttpServletRequest) request;
//        String path = ((HttpServletRequest)request).getRequestURI();
//        System.out.println("====path====="+path);
//        if (path.startsWith("/auditphoto/insert")){
//            System.out.println("===========hahhahahhah");
//            chain.doFilter(servletRequest,response);
//        }else {
//        }

        //通过此包装类，将request中的流数据，包装至requestWrapper的body中
        ServletRequest requestWrapper = new RequestWrapper((HttpServletRequest) request);
        chain.doFilter(requestWrapper, response);
    }

    @Override
    public void destroy() {
        log.info("StreamFilter销毁...");
        System.out.println("StreamFilter销毁...");
    }
}
