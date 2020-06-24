package com.faraway.auditall.controller;

import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.mapper.AuditPhotoMapper;
import com.faraway.auditall.service.imp.AuditPhotoServiceImp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditPhotoControllerTest {

    @Autowired
    private AuditPhotoMapper auditPhotoMapper;

    @Test
    void insertAuditPhoto() {
        AuditPhoto auditPhoto = new AuditPhoto();
        auditPhoto.setUserName("lisi").setAuditPhoto("jkljl");

        auditPhotoMapper.insert(auditPhoto);
    }
}
