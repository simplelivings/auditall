package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditName;
import com.faraway.auditall.mapper.AuditNameMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditNameServiceImpTest {

    @Autowired
    private AuditNameMapper auditNameMapper;

    @Test
    void findPassword() {
        QueryWrapper<AuditName> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName","super");
        AuditName auditName = auditNameMapper.selectOne(queryWrapper);
        System.out.println("================="+auditName.getPassword());
    }

    @Test
    void findAllSender() {
        List<AuditName> auditNameList = auditNameMapper.selectList(null);
        for (int i = 0; i < auditNameList.size(); i++) {
            System.out.println("============"+auditNameList.get(i).getSender());
        }

    }

    @Test
    void findAllReceiver() {
    }
}
