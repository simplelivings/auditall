package com.faraway.auditall.controller;

import com.faraway.auditall.entity.CheckPhoto;
import com.faraway.auditall.service.imp.CheckPhotoServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/checkphoto")
@CrossOrigin  //解决跨域
@Slf4j
public class CheckPhotoController {

    @Autowired
    private CheckPhotoServiceImp checkPhotoServiceImp;

    @PostMapping("/insert")
    public int insertCheckPhoto(@RequestBody CheckPhoto checkPhoto){

        //图片存放路径
        String PATH = "src/picture/";
        File file0 = new File(PATH);
        if (!file0.exists()) {
            file0.mkdirs();
        }

        //返回值
        int returnNum = 0;
        if (checkPhoto!=null && checkPhoto.getUserName()!=null && checkPhoto.getUserName().length()>0 && checkPhoto.getCheckPhotoList()!=null && checkPhoto.getCheckPhotoList().size()>0){
            //获得图片src的list
            List<String> tempList = checkPhoto.getCheckPhotoList();

            int numberDate = checkPhotoServiceImp.insertOrUpdateCheckPhoto(checkPhoto);

            String partNum = "";
            if (checkPhoto.getPartNum()!=null){
                partNum = checkPhoto.getPartNum();
            }
            String produceTime = "";
            if (checkPhoto.getProduceTime()!=null){
                produceTime = checkPhoto.getProduceTime();
            }

            int checkType = 0;
            if (checkPhoto.getCheckType()!=null){
                checkType = checkPhoto.getCheckType();
            }

            //图片Base64解码，并存入服务器
            switch (numberDate) {
                case 2://前端返回的图片数量为2
                    if (tempList!=null && tempList.size()>0){
                        for (int i = 0; i < tempList.size(); i++) {
                            String fileName = partNum + "type" + checkType + "num" + i + "p"+produceTime+".jpg";//文件名
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
                        String fileName = partNum + "type" + checkType + "num" + i + "p"+produceTime+".jpg";
                        File file = new File(PATH + fileName);

                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    try {//Ba64解码
                        String fileName = partNum + "type" + checkType + "num" + 0 + "p"+produceTime+".jpg";
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
                        String fileName = partNum + "type" + checkType + "num" + i + "p"+produceTime+".jpg";
                        File file = new File(PATH + fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    break;
            }
            returnNum = 200;
            log.info("===检验图片  生成文件成功===");

        } else {
            returnNum = 0;
            log.info("===检验图片  生成文件失败===");
        }

        return returnNum;
    }
}
