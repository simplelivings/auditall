package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.mapper.AuditPhotoMapper;
import com.faraway.auditall.service.AuditPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditPhotoServiceImp implements AuditPhotoService {

    @Autowired
    private AuditPhotoMapper auditPhotoMapper;

    @Override
    public int insertOrUpdateAuditPhoto(AuditPhoto auditPhoto) {

        QueryWrapper<AuditPhoto> queryWrapper = new QueryWrapper<>();
        //根据用户名和审核页码，新建查询条件；
        queryWrapper.eq("userName", auditPhoto.getUserName()).eq("auditPage",auditPhoto.getAuditPage());
        int photoNumbers = auditPhoto.getAuditPhotoList().size();//前端回传图片数量

        QueryWrapper<AuditPhoto> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userName", auditPhoto.getUserName());

        switch (photoNumbers) {
            case 2://前端返回图片数量为2
                auditPhotoMapper.delete(queryWrapper);//清空符合条件数据库中数据
                for (int i = 0; i < photoNumbers; i++) {//插入图片编号至数据库
                    AuditPhoto tempAuditPhoto = new AuditPhoto();
                    tempAuditPhoto.setUserName(auditPhoto.getUserName());
                    tempAuditPhoto.setAuditPage(auditPhoto.getAuditPage());
                    tempAuditPhoto.setPhotoNumber(i);
                    auditPhotoMapper.insert(tempAuditPhoto);
                }
                break;
            case 1:
                auditPhotoMapper.delete(queryWrapper);//清空符合条件数据库中数据
                AuditPhoto tempAuditPhoto = new AuditPhoto();//插入图片编号至数据库
                tempAuditPhoto.setUserName(auditPhoto.getUserName());
                tempAuditPhoto.setAuditPage(auditPhoto.getAuditPage());
                tempAuditPhoto.setPhotoNumber(0);
                auditPhotoMapper.insert(tempAuditPhoto);
                break;
            default:
                auditPhotoMapper.delete(queryWrapper);//清空符合条件数据库中数据
                break;
        }
        List<AuditPhoto> auditPhotoList = auditPhotoMapper.selectList(queryWrapper1);
        System.out.println("==========图片成功插入数据库============");

        return photoNumbers;
    }
}
