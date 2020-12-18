package com.faraway.auditall.interceptors;

import com.faraway.auditall.config.RequestWrapper;
import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.mapper.BasicInfoMapper;
import com.faraway.auditall.service.BasicInfoService;
import com.faraway.auditall.service.imp.BasicInfoServiceImp;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userName = "";

        //如果不是映射到方法，直接通过
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        //如果是CORS的预检请求，则返回；以接收真正的请求头信息。
        if ("OPTIONS".equals(request.getMethod())){
            return false;
        }

        //get方法，获取用户名
        if ("GET".equals(request.getMethod())){
            userName = request.getParameter("userName");
        }


        //post方法，getParameter方法不适用，从包装类中 获取用户名;
        if ("POST".equals(request.getMethod())){
            RequestWrapper requestWrapper = new RequestWrapper(request);
            if (requestWrapper.getBodyString()!=null && requestWrapper.getBodyString().length() >0){
                String tempString = requestWrapper.getBodyString();
                String tempArrays[] = tempString.split("userName\":\"");
                userName = tempArrays[1].substring(0,tempArrays[1].indexOf("\""));
            }
        }

        //从请求头中，获取token;
        String token = request.getHeader("token");

//        System.out.println("=====token===="+token);
//        System.out.println("=====userName===="+userName);

        //从redis中，根据用户名，获取存储的token
        String tokenServer = "";
        if (userName!=null){
            if (redisTemplate.opsForValue().get(userName)!=null){
                tokenServer = redisTemplate.opsForValue().get(userName).toString();
            }
        }
//        System.out.println("=====tokenServer===="+tokenServer);

        //判断客户端token与数据库token，是否一致；一致则放行，否则将拦截。
        if (token!=null && token.equals(tokenServer)){
            return true;
        }else{
            System.out.println("+++++++++++你被拦截了+++++++++++++++"+request);
            return false;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
