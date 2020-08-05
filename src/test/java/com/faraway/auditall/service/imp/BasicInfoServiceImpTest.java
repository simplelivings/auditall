package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.mapper.AuditInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BasicInfoServiceImpTest {

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Test
    void findAuditNum() {
        QueryWrapper<AuditInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName","李四");

        auditInfoMapper.delete(queryWrapper);
        System.out.println("delete Ok");

    }
}
