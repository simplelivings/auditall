package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.entity.CheckPhoto;
import com.faraway.auditall.mapper.CheckPhotoMapper;
import com.faraway.auditall.service.CheckInfoService;
import com.faraway.auditall.service.CheckPhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class CheckPhotoServiceImp implements CheckPhotoService {

    @Autowired
    private CheckPhotoMapper checkPhotoMapper;

    @Override
    public List<CheckPhoto> findAllCheckPhoto() {
        List<CheckPhoto> checkPhotoList = checkPhotoMapper.selectList(null);
        return checkPhotoList;
    }

    @Override
    public int insertOrUpdateCheckPhoto(CheckPhoto checkPhoto) {

        if (checkPhoto != null) {

            //查找数据库中是否有图片，如有删除
            QueryWrapper<CheckPhoto> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userName", checkPhoto.getUserName())
                    .eq("partNum", checkPhoto.getPartNum())
                    .eq("checkType", checkPhoto.getCheckType())
                    .eq("produceTime", checkPhoto.getProduceTime());
            checkPhotoMapper.delete(queryWrapper);

            //获得审核图片list
            int photoNumbers = 0;
            if(checkPhoto.getCheckPhotoList().size()>0){
                photoNumbers = checkPhoto.getCheckPhotoList().size();
            }

            //按图片list顺序，更新数据库
            if (photoNumbers > 0) {
                for (int i = 0; i < photoNumbers; i++) {
                    CheckPhoto checkPhotoTemp = new CheckPhoto();
                    if (checkPhoto.getUserName()!=null){
                        checkPhotoTemp.setUserName(checkPhoto.getUserName());
                    }
                    if (checkPhoto.getPartNum()!=null){
                        checkPhotoTemp.setPartNum(checkPhoto.getPartNum());
                    }
                    if (checkPhoto.getCheckType()!=null){
                        checkPhotoTemp.setCheckType(checkPhoto.getCheckType());
                    }
                    if (checkPhoto.getProduceTime()!=null){
                        checkPhotoTemp.setProduceTime(checkPhoto.getProduceTime());
                    }
                    checkPhotoTemp.setPhotoNumber(i);
                    checkPhotoMapper.insert(checkPhotoTemp);
                }
            }
            log.info("===检验图片 成功插入数据库===");
            return photoNumbers;
        } else {
            return 0;
        }
    }

    @Override
    public void deleteAllCheckPhoto() {
        checkPhotoMapper.delete(null);
    }
}
