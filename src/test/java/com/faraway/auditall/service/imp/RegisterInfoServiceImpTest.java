package com.faraway.auditall.service.imp;

import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.mapper.RegisterInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterInfoServiceImpTest {

    @Autowired
    private RegisterInfoMapper registerInfoMapper;

    @Test
    void findRegisterInfoByName() {

        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setUserName("lis").setEmailAddress("fjdkl@139.com");
        registerInfoMapper.insert(registerInfo);
        System.out.println("ok");

        List<RegisterInfo> registerInfoList = registerInfoMapper.selectList(null);
        for (int i = 0; i < registerInfoList.size(); i++) {
            System.out.println("email+++++++++"+registerInfoList.get(i).getEmailAddress());
        }
    }
}
