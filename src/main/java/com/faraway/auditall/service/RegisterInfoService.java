package com.faraway.auditall.service;

import com.faraway.auditall.entity.RegisterInfo;

import javax.mail.MessagingException;
import java.util.List;

public interface RegisterInfoService {
    public int findRegisterInfoByName(String userName);

    public int insertRegister(RegisterInfo registerInfo);

    public RegisterInfo findOneRegisterByName(String userName);

    public List<RegisterInfo> findAllRegister();

    public void sendEmailHyperLinks(String userName) throws MessagingException;

    public int updateRegister(RegisterInfo registerInfo);

    public int findRegisterByPhone(String phone);

    public int findRegisterByUserId(String userId);

    public int deleteRegisterByName(String userName);

}
