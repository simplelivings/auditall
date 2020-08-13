package com.faraway.auditall.service.imp;

import com.faraway.auditall.mapper.AuditInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditInfoServiceImpTest {

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Test
    void cleanDataBase() {
        auditInfoMapper.delete(null);
    }
}
