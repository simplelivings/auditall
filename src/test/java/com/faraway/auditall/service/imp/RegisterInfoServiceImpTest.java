package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.mapper.RegisterInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterInfoServiceImpTest {

    @Autowired
    private RegisterInfoMapper registerInfoMapper;

    @Autowired
    private RegisterInfoServiceImp registerInfoServiceImp;

    @Test
    void findRegisterInfoByName() {
        QueryWrapper<RegisterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName","fan");
        RegisterInfo registerInfo = registerInfoMapper.selectOne(queryWrapper);
        registerInfo.setUserRight(1);
        registerInfoMapper.update(registerInfo,queryWrapper);
        Date date = new Date();
        long i = date.getTime();
        long j = registerInfo.getCreateTime().getTime();

        System.out.println("=======minus time===="+Math.round(28/30+0.5d));
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
//        int i = df.format(date) - df.format(registerInfo.getCreateTime());
        System.out.println("====register time====="+registerInfo.getCreateTime());
        System.out.println("====date====="+date);
    }

    @Test
    void checkRegisterStatue(){
        List<RegisterInfo> registerInfoList = registerInfoServiceImp.findAllRegister();
        for (RegisterInfo registerInfo : registerInfoList) {
            if (registerInfo.getRegisterStatue() > 0){
                Long registerTime = Math.round((new Date().getTime() - registerInfo.getUpdateTime().getTime()) /86400000/30+0d);
                if (registerTime > 12){
                    registerInfo.setRegisterStatue(0);
                    registerInfoServiceImp.updateRegister(registerInfo);
                }
            }
        }

    }
}
