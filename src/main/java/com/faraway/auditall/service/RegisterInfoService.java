package com.faraway.auditall.service;

import com.faraway.auditall.entity.RegisterInfo;

import java.util.List;

public interface RegisterInfoService {
    public int findRegisterInfoByName(String userName);

    public int insertRegister(RegisterInfo registerInfo);

    public RegisterInfo findOneRegisterByName(String userName);

    public List<RegisterInfo> findAllRegister();
}
