package com.faraway.auditall.controller;

import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.service.imp.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 定时任务处理器
 */

@RestController
@Slf4j
@CrossOrigin  //解决跨域
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

    @Autowired
    private InspectInfoServiceImp inspectInfoServiceImp;

    @Autowired
    private InspectPhotoServiceImp inspectPhotoServiceImp;

    @Autowired
    private RegisterInfoServiceImp registerInfoServiceImp;


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
        inspectInfoServiceImp.deleteAllInspectInfo();
        inspectPhotoServiceImp.deleteAllInspectPhoto();
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

    //更新时间超过12个月，权限变为普通用户。
    @Scheduled(cron = "${dap.dateschedules}")
    private void checkRegisterStatue(){
        List<RegisterInfo> registerInfoList = registerInfoServiceImp.findAllRegister();
        for (RegisterInfo registerInfo : registerInfoList) {
            if (registerInfo.getUserRight() != null && registerInfo.getUpdateTime() != null){
                Long registerTime = Math.round((new Date().getTime() - registerInfo.getUpdateTime().getTime()) /86400000/30+0d);
                if (registerTime > 12){
                    registerInfo.setUserRight(6);
                    registerInfoServiceImp.updateRegister(registerInfo);
                }
            }
        }

    }
}
