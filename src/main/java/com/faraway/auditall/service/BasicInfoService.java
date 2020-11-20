package com.faraway.auditall.service;

import com.faraway.auditall.entity.BasicInfo;

import javax.mail.MessagingException;

public interface BasicInfoService {

    public int findAuditNum(BasicInfo basicInfo);
    public int findAuditNumSuper(BasicInfo basicInfo);
    public int insertOrUpdateBasicInfo(BasicInfo basicInfo);
    public void deleteAllBasicInfo();
    public BasicInfo findBasicInfoByName(String name);
    public int deleteTempData(String userName);
    public int checkRegisterStatus(String userName);
}
