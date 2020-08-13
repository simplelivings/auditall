package com.faraway.auditall.controller;

import com.faraway.auditall.service.imp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;

@RestController
@Slf4j
public class CleanDataController {

    @Autowired
    private AuditInfoServiceImp auditInfoServiceImp;

    @Autowired
    private AuditPhotoServiceImp auditPhotoServiceImp;

    @Autowired
    private BasicInfoServiceImp basicInfoServiceImp;

    @Autowired
    private CheckInfoServiceImp checkInfoServiceImp;

    @Autowired
    private CheckPhotoServiceImp checkPhotoServiceImp;

    @Scheduled(cron = "${dap.checkschedules}")
    private void generateAndEmailCheckInfo() throws InterruptedException, MessagingException, IOException {
        checkInfoServiceImp.gererateExcel();
        log.info("===检验信息 定时发送邮件完成===");
    }


    @Scheduled(cron = "${dap.dateschedules}")
    private void cleanDataBase(){
        auditInfoServiceImp.deleteAllAuditInfo();
        auditPhotoServiceImp.deleteAllAuditPhoto();
        basicInfoServiceImp.deleteAllBasicInfo();
        checkInfoServiceImp.deleteAllCheckInfo();
        checkPhotoServiceImp.deleteAllCheckPhoto();
        log.info("===数据库 定时清理完成===");
    }

    @Scheduled(cron = "${dap.dateschedules}")
    private void deleteAllfiles(){
        File file = new File("src/picture");
        File[] files = file.listFiles();
        if (files!=null){
            for (File f : files) {
                if (f.isFile()){
                    f.delete();
                }
            }
        }
        log.info("===文件 定时删除完成===");
    }
}
