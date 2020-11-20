package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.mapper.RegisterInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RegisterInfoServiceImpTest {

    @Autowired
    private RegisterInfoMapper registerInfoMapper;

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
}
