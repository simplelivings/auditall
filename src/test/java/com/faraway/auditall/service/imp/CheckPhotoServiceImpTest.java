package com.faraway.auditall.service.imp;

import com.faraway.auditall.entity.CheckPhoto;
import com.faraway.auditall.mapper.CheckPhotoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckPhotoServiceImpTest {

    @Autowired
    private CheckPhotoMapper checkPhotoMapper;

    @Test
    void findAllCheckPhoto() {
       List<CheckPhoto> checkPhotoList = checkPhotoMapper.selectList(null);
        System.out.println("checkPhotoList====="+checkPhotoList);
    }
}
