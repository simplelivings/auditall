package com.faraway.auditall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.*;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.mapper.AuditItemMapper;
import com.faraway.auditall.mapper.InspectInfoMapper;
import com.faraway.auditall.service.imp.AuditInfoServiceImp;
import com.faraway.auditall.service.imp.AuditPhotoServiceImp;
import com.faraway.auditall.service.imp.InspectInfoServiceImp;
import com.faraway.auditall.service.imp.InspectPhotoServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-21 11:39
 */


@RestController
@RequestMapping("/inspectPhoto")
@CrossOrigin  //解决跨域
@Slf4j
public class InspectPhotoController {

    @Autowired
    private InspectPhotoServiceImp inspectPhotoServiceImp;

    @Autowired
    private InspectInfoMapper inspectInfoMapper;

    @Autowired
    private InspectInfoServiceImp inspectInfoServiceImp;

    @PostMapping(value = "/insert")
    public int insertAuditPhoto(@RequestBody InspectPhoto inspectPhoto) throws IOException, MessagingException, InterruptedException {

        //图片存放路径
        String PATH = "src/picture/";
        File file0 = new File(PATH);
        if (!file0.exists()) {
            file0.mkdirs();
        }

        //返回值
        int returnNum = 0;

        log.info("===分层审核图片  controller userName==="+inspectPhoto.getUserName());


        if (inspectPhoto != null && inspectPhoto.getAuditPhotoList() != null && inspectPhoto.getAuditPhotoList().size() > 0 && inspectPhoto.getUserName() != null && inspectPhoto.getUserName().length() > 0) {
            //获得图片src的list
            List<String> tempList = inspectPhoto.getAuditPhotoList();

            //获得审核页面编号
            int page = inspectPhoto.getAuditPage();

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmms");//设置日期格式
            Date date = new Date();
            inspectPhoto.setUpdateTime(date);
            String picDate = df.format(date);

            System.out.println("=====controller  picDate==="+picDate);


            //图片数量信息放入数据库，并得到图片数量
            int numberData = inspectPhotoServiceImp.insertOrUpdateInspectPhoto(inspectPhoto);


            //获得审核者姓名
            String name = inspectPhoto.getUserName();

            //图片Base64解码，并存入服务器
            switch (numberData) {
                case 2://前端返回的图片数量为2
                    if (tempList != null && tempList.size() > 0) {
                        for (int i = 0; i < tempList.size(); i++) {
                            String fileName = picDate+"name" + name + "page" + page + "num" + i + ".jpg";//文件名
                            try {//Ba64解码
                                FileOutputStream fos = new FileOutputStream(new File(PATH + fileName));
                                byte[] dBytes = Base64.getDecoder().decode(tempList.get(i));
                                fos.write(dBytes);
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case 1://前端返回的图片数量为1
                    for (int i = 0; i < 2; i++) {//删除所有对应页码已有文件
                        String fileName = picDate+"name" + name + "page" + page + "num" + i + ".jpg";//文件名
                        File file = new File(PATH + fileName);

                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    try {//Ba64解码
                        String fileName =  picDate+"name" + name + "page" + page + "num" + 0 + ".jpg";
                        FileOutputStream fos = new FileOutputStream(new File(PATH + fileName));
                        byte[] dBytes = Base64.getDecoder().decode(tempList.get(0));
                        fos.write(dBytes);
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default://前端返回的图片数量为0
                    for (int i = 0; i < 2; i++) {//删除所有对应页码已有文件
                        String fileName = picDate+"name" + name + "page" + page + "num" + i + ".jpg";//文件名
                        File file = new File(PATH + fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    break;
            }
            returnNum = 200;
        } else {
            returnNum = 0;
        }

        return returnNum;
    }


    @PostMapping("/inspectExcel")
    public int insertAndExcel(@RequestBody InspectPhoto inspectPhoto) throws IOException, MessagingException, InterruptedException {

        //返回值
        int returnNum = 0;

        if (inspectPhoto!= null &&inspectPhoto.getUserName() != null && inspectPhoto.getUserName().length() > 0) {
            inspectInfoServiceImp.generateExcel(inspectPhoto);
            returnNum = 200;
        } else {
            returnNum = 0;
        }
        return returnNum;
    }
}
