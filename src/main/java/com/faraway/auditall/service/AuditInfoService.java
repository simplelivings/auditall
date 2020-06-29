package com.faraway.auditall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface AuditInfoService{

    public AuditNum insertOrUpdateAuditInfo(AuditInfo auditInfo);

    public int generateExcel(AuditInfo auditInfo) throws FileNotFoundException, IOException;
}
