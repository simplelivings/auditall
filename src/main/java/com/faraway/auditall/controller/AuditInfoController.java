package com.faraway.auditall.controller;

import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;
import com.faraway.auditall.service.imp.AuditInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auditinfo")
@CrossOrigin  //解决跨域
public class AuditInfoController {

    @Autowired
    private AuditInfoServiceImp auditInfoServiceImp;

    @PostMapping("/insert")
    public AuditNum insertOrUpdateAuditInfo(@RequestBody AuditInfo auditInfo) {
        return auditInfoServiceImp.insertOrUpdateAuditInfo(auditInfo);
    }
}
