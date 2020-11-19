package com.faraway.auditall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;
import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.mapper.AuditPhotoMapper;
import com.faraway.auditall.mapper.BasicInfoMapper;
import com.faraway.auditall.service.imp.BasicInfoServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 处理登录页面请求
 */

@RestController
@RequestMapping("/basicinfo")
@CrossOrigin  //解决跨域
@Slf4j
public class BasicInfoController {

    @Autowired
    private BasicInfoServiceImp basicInfoServiceImp;

    @Autowired
    private RedisTemplate redisTemplate;


    //接收login页面请求数据，返回审核表单的num
    @PostMapping("/login")
    public BasicInfo findAuditNum(@RequestBody BasicInfo basicInfo, HttpServletResponse response) {

        if (basicInfo != null) {

            //获取审核表单的num
            int auditNum = basicInfoServiceImp.findAuditNum(basicInfo);
            basicInfo.setAuditNum(auditNum);

            //生成token
            String token = UUID.randomUUID() + "";

            //token放入redis中，key为用户名,有效期为10分钟；
            redisTemplate.opsForValue().set(basicInfo.getUserName(),token,10, TimeUnit.MINUTES);

            //不存在已登录用户，或用户信息输入正确后，数据插入数据库
            if (auditNum<100 && auditNum!=-1){
                basicInfoServiceImp.insertOrUpdateBasicInfo(basicInfo);
            }

            //token放入响应头中，服务端接收并存储至localStorage中
            response.setHeader("token",token);
            return basicInfo;
        } else {
            return null;
        }
    }


    //超级用户登录
    @PostMapping("/superlogin")
    public int findAuditNumSuper(@RequestBody BasicInfo basicInfo) {
        if (basicInfo != null) {
            return basicInfoServiceImp.findAuditNumSuper(basicInfo);
        } else {
            return -1;
        }
    }

    //用户点击退出按钮后，清空redis相应key，避免影响用户登录
    @GetMapping("/clearData")
    public int clearRedisKeys(@RequestParam("userName")String userName){
      if (userName !=null && userName.length()>0){
          return basicInfoServiceImp.deleteTempData(userName);
      }
      return -1;
    }

}
