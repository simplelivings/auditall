package com.faraway.auditall.interceptors;

import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.mapper.BasicInfoMapper;
import com.faraway.auditall.service.BasicInfoService;
import com.faraway.auditall.service.imp.BasicInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private BasicInfoServiceImp basicInfoServiceImp;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getParameter("auditWay");
        String name = request.getParameter("userName");
        BasicInfo basicInfo = basicInfoServiceImp.findBasicInfoByName(name);

        if (token!=null && token.equals(basicInfo.getToken())){
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
