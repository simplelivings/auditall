package com.faraway.auditall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;
import com.faraway.auditall.entity.AuditPhoto;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface AuditInfoService{

    public AuditNum getAuditNum(String userName);

    public AuditNum insertOrUpdateAuditInfo(AuditInfo auditInfo);

    public void generateExcel(AuditPhoto auditPhoto) throws FileNotFoundException, IOException, MessagingException, InterruptedException;

    public void deleteAllAuditInfo();
}
