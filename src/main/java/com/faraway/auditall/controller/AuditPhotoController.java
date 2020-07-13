package com.faraway.auditall.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.entity.AuditPhoto;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.mapper.AuditItemMapper;
import com.faraway.auditall.service.AuditInfoService;
import com.faraway.auditall.service.imp.AuditInfoServiceImp;
import com.faraway.auditall.service.imp.AuditPhotoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import javax.mail.MessagingException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/auditphoto")
@CrossOrigin  //解决跨域
public class AuditPhotoController {

    @Autowired
    private AuditPhotoServiceImp auditPhotoServiceImp;

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Autowired
    private AuditItemMapper auditItemMapper;

    @Autowired
    private AuditInfoServiceImp auditInfoServiceImp;

    @PostMapping("/insert")
    public int insertAuditPhoto(@RequestBody AuditPhoto auditPhoto) throws IOException, MessagingException, InterruptedException {

        //图片存放路径
        String PATH = "src/picture/";
        File file0 = new File(PATH);
        if (!file0.exists()) {
            file0.mkdirs();
        }

        //返回值
        int returnNum = 0;


        if (auditPhoto != null) {
            //获得图片src的list
            List<String> tempList = auditPhoto.getAuditPhotoList();

            //获得审核页面编号
            int page = auditPhoto.getAuditPage();

            //图片数量信息放入数据库，并得到图片数量
            int numberData = auditPhotoServiceImp.insertOrUpdateAuditPhoto(auditPhoto);


            //获得审核者姓名
            String name = auditPhoto.getUserName();

            System.out.println("++++++++++++++++AuditPhotoController++++++++++++++++");

            //图片Base64解码，并存入服务器
            switch (numberData) {
                case 2://前端返回的图片数量为2
                    for (int i = 0; i < tempList.size(); i++) {
                        String fileName = "name" + name + "page" + page + "num" + i + ".jpg";//文件名
                        try {//Ba64解码
                            FileOutputStream fos = new FileOutputStream(new File(PATH + fileName));
                            byte[] dBytes = Base64.getDecoder().decode(tempList.get(i));
                            fos.write(dBytes);
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1://前端返回的图片数量为1
                    for (int i = 0; i < 2; i++) {//删除所有对应页码已有文件
                        String fileName = "name" + name + "page" + page + "num" + i + ".jpg";
                        File file = new File(PATH + fileName);

                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    try {//Ba64解码
                        String fileName = "name" + name + "page" + page + "num" + 0 + ".jpg";
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
                        String fileName = "name" + name + "page" + page + "num" + i + ".jpg";
                        File file = new File(PATH + fileName);
                        System.out.println("=======controller====default==fileName=======" + file);
                        if (file.exists()) {
                            file.delete();
                            System.out.println("====controller====default=delete===" + i);
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

    @PostMapping("/insertandexcel")
    public int insertAndExcel(@RequestBody AuditPhoto auditPhoto) throws IOException, MessagingException, InterruptedException {

        //图片存放路径
        String PATH = "src/picture/";
        File file0 = new File(PATH);
        if (!file0.exists()) {
            file0.mkdirs();
        }

        //得到已审核的项目的数量
        QueryWrapper<AuditInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", auditPhoto.getUserName());
        int totalNum = auditInfoMapper.selectCount(queryWrapper);

        QueryWrapper<AuditItem> auditItemQueryWrapper = new QueryWrapper<>();
        auditItemQueryWrapper.eq("auditNum",auditPhoto.getAuditNum());
        int totalItemNum = auditItemMapper.selectCount(auditItemQueryWrapper);

        //返回值
        int returnNum = 0;

        System.out.println("+++++++++++++totalNum==========" + totalNum);
        System.out.println("+++++++++++++totalItemNum==========" + totalItemNum);

        if (auditPhoto != null) {
            //获得图片src的list
            List<String> tempList = auditPhoto.getAuditPhotoList();

            //获得审核页面编号
            int page = auditPhoto.getAuditPage();

            //图片数量信息放入数据库，并得到图片数量
            int numberData = auditPhotoServiceImp.insertOrUpdateAuditPhoto(auditPhoto);


            //获得审核者姓名
            String name = auditPhoto.getUserName();

            System.out.println("++++++++++++++++AuditPhotoController++++++++++++++++");

            //图片Base64解码，并存入服务器
            switch (numberData) {
                case 2://前端返回的图片数量为2
                    for (int i = 0; i < tempList.size(); i++) {
                        String fileName = "name" + name + "page" + page + "num" + i + ".jpg";//文件名
                        try {//Ba64解码
                            FileOutputStream fos = new FileOutputStream(new File(PATH + fileName));
                            byte[] dBytes = Base64.getDecoder().decode(tempList.get(i));
                            fos.write(dBytes);
                            fos.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1://前端返回的图片数量为1
                    for (int i = 0; i < 2; i++) {//删除所有对应页码已有文件
                        String fileName = "name" + name + "page" + page + "num" + i + ".jpg";
                        File file = new File(PATH + fileName);

                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    try {//Ba64解码
                        String fileName = "name" + name + "page" + page + "num" + 0 + ".jpg";
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
                        String fileName = "name" + name + "page" + page + "num" + i + ".jpg";
                        File file = new File(PATH + fileName);
                        System.out.println("=======controller====default==fileName=======" + file);
                        if (file.exists()) {
                            file.delete();
                            System.out.println("====controller====default=delete===" + i);
                        }
                    }
                    break;
            }
            //最后一页，数据插入excel
            if (totalNum == auditPhoto.getAuditPage()) {
                auditInfoServiceImp.generateExcel(auditPhoto);
            }
            returnNum = 200;
        } else {
            returnNum = 0;
        }
        return returnNum;
    }

}
