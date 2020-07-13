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
        return auditInfoServiceImp.insertOrUpdateAuditInfo(auditInfo);
    }

//    @PostMapping("/generate")
//    public void generateExcel(@RequestBody AuditPhoto auditPhoto) throws IOException, MessagingException, InterruptedException {
//        auditInfoServiceImp.generateExcel(auditPhoto);
//    }

    public String sendMail(){
        return "";
    }

}
