package com.faraway.auditall.controller;


import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;
import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.service.imp.AuditInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/auditinfo")
@CrossOrigin  //解决跨域
public class AuditInfoController {

    @Autowired
    private AuditInfoServiceImp auditInfoServiceImp;

    @PostMapping(value = "/insert")
    public AuditNum insertOrUpdateAuditInfo(@RequestBody AuditInfo auditInfo) {
        AuditNum auditNum = new AuditNum();
        if (auditInfo!=null && auditInfo.getUserName()!=null && auditInfo.getUserName().length()>0){
            return auditInfoServiceImp.insertOrUpdateAuditInfo(auditInfo);
        }else{
            return auditNum;
        }
    }

    @GetMapping(value = "/getnum")
    public AuditNum getAuditNum(@RequestParam String userName){
        return auditInfoServiceImp.getAuditNum(userName);
    }
}
