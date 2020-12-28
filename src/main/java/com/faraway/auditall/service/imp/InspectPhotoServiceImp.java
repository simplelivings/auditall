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

    /**
     *
     * @param inspectPhoto
     * @return
     *
     * 图片插入或更新至数据库
     * 1 根据用户名和审核页码，新建查询条件；
     * 2 获得用户名，审核页面信息
     * 3 依据图片数量，先删除已有数据，并将信息存入数据库中
     *   图片编号：0为第一张图片，1位第二张图片
     */

    @Override
    public int insertOrUpdateInspectPhoto(InspectPhoto inspectPhoto) {
        String userName = "";
        int auditPage = 0;

        QueryWrapper<InspectPhoto> queryWrapper = new QueryWrapper<>();
        //1 根据用户名和审核页码，新建查询条件；
        queryWrapper.eq("userName", inspectPhoto.getUserName()).eq("auditPage",inspectPhoto.getAuditPage());
        int photoNumbers = inspectPhoto.getAuditPhotoList().size();//前端回传图片数量

        //2 获得用户名，审核页面信息
        if (inspectPhoto.getUserName()!=null){
            userName = inspectPhoto.getUserName();
        }
        if (inspectPhoto.getAuditPage()!=null){
            auditPage = inspectPhoto.getAuditPage();
        }

        //3 依据图片数量，先删除已有数据，并将数据存入数据库中
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
        log.info("===分层审核图片  成功插入数据库===");
        return photoNumbers;
    }

    @Override
    public void deleteAllInspectPhoto() {
        inspectPhotoMapper.delete(null);
    }
}
