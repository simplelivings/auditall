package com.faraway.auditall.service;

import com.faraway.auditall.entity.RegisterInfo;

public interface RegisterInfoService {
    public int findRegisterInfoByName(String userName);

    public int insertRegister(RegisterInfo registerInfo);
}
