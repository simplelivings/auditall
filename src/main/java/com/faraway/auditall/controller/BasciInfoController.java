package com.faraway.auditall.controller;

import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.service.imp.BasicInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/basicinfo")
@CrossOrigin  //解决跨域
public class BasciInfoController {

    @Autowired
    private BasicInfoServiceImp basicInfoServiceImp;


    //接收login页面请求数据，返回审核表单的num
    @PostMapping("/login")
    public int findAuditNum(@RequestBody BasicInfo basicInfo){
        if (basicInfo!=null){
            return basicInfoServiceImp.findAuditNum(basicInfo);
        }else{
            return -1;
        }
    }
}
