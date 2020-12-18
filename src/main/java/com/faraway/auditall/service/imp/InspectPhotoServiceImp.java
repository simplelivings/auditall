package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.entity.InspectPhoto;
import com.faraway.auditall.mapper.InspectPhotoMapper;
import com.faraway.auditall.service.InspectPhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TODO
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-21 11:37
 */
@Service
@Slf4j
public class InspectPhotoServiceImp implements InspectPhotoService {

    @Autowired
    private InspectPhotoMapper inspectPhotoMapper;

    @Override
    public int insertOrUpdateInspectPhoto(InspectPhoto inspectPhoto) {
        QueryWrapper<InspectPhoto> queryWrapper = new QueryWrapper<>();
        //根据用户名和审核页码，新建查询条件；
        queryWrapper.eq("userName", inspectPhoto.getUserName()).eq("auditPage",inspectPhoto.getAuditPage());
        int photoNumbers = inspectPhoto.getAuditPhotoList().size();//前端回传图片数量

        log.info("===分层审核图片  photoNumbers==="+photoNumbers);

        QueryWrapper<InspectPhoto> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("userName", inspectPhoto.getUserName());
        String userName = "";
        int auditPage = 0;
        if (inspectPhoto.getUserName()!=null){
            userName = inspectPhoto.getUserName();
        }
        if (inspectPhoto.getAuditPage()!=null){
            auditPage = inspectPhoto.getAuditPage();
        }

        switch (photoNumbers) {
            case 2://前端返回图片数量为2
                inspectPhotoMapper.delete(queryWrapper);//清空符合条件数据库中数据
                for (int i = 0; i < photoNumbers; i++) {//插入图片编号至数据库
                    InspectPhoto tempAuditPhoto = new InspectPhoto();
                    tempAuditPhoto.setUserName(userName);
                    tempAuditPhoto.setAuditPage(auditPage);
                    tempAuditPhoto.setPhotoNumber(i);
                    inspectPhotoMapper.insert(tempAuditPhoto);
                }
                break;
            case 1:
                inspectPhotoMapper.delete(queryWrapper);//清空符合条件数据库中数据
                InspectPhoto tempAuditPhoto = new InspectPhoto();
                tempAuditPhoto.setUserName(userName);
                tempAuditPhoto.setAuditPage(auditPage);
                tempAuditPhoto.setPhotoNumber(0);
                inspectPhotoMapper.insert(tempAuditPhoto);
                break;
            default:
                inspectPhotoMapper.delete(queryWrapper);//清空符合条件数据库中数据
                break;
        }
//        List<In> auditPhotoList = inspectPhotoMapper.selectList(queryWrapper1);

        log.info("===分层审核图片  成功插入数据库===");
        return photoNumbers;
    }

    @Override
    public void deleteAllInspectPhoto() {
        inspectPhotoMapper.delete(null);
    }
}
