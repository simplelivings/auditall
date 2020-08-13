package com.faraway.auditall.controller;

import com.faraway.auditall.entity.AuditNum;
import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.service.imp.BasicInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/basicinfo")
@CrossOrigin  //解决跨域
public class BasicInfoController {

    @Autowired
    private BasicInfoServiceImp basicInfoServiceImp;


    //接收login页面请求数据，返回审核表单的num
    @PostMapping("/login")
    public BasicInfo findAuditNum(@RequestBody BasicInfo basicInfo){
        if (basicInfo!=null){
            int auditNum = basicInfoServiceImp.findAuditNum(basicInfo);
            String token = UUID.randomUUID()+"";
            basicInfo.setAuditNum(auditNum);
            basicInfo.setToken(token);
            basicInfoServiceImp.insertOrUpdateBasicInfo(basicInfo);
            return basicInfo;
        }else{
            return null;
        }
    }

    @PostMapping("/superlogin")
    public int findAuditNumSuper(@RequestBody BasicInfo basicInfo){
        System.out.println("=====basicInfo==="+basicInfo);
        if (basicInfo!=null){
            return basicInfoServiceImp.findAuditNumSuper(basicInfo);
        }else{
            return -1;
        }
    }
}
