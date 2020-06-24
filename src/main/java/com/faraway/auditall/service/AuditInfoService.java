package com.faraway.auditall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;

public interface AuditInfoService{

    public AuditNum insertOrUpdateAuditInfo(AuditInfo auditInfo);
}
