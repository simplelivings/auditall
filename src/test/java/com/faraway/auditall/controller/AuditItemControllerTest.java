package com.faraway.auditall.controller;

import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.service.imp.AuditItemServiceImp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditItemControllerTest {

    @Autowired
    private AuditItemServiceImp auditItemServiceImp;

    @Test
    void insertAuditItem() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String fileName = sd.format(date);
        System.out.println("-----------"+fileName);
    }
}
