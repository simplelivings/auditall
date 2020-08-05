package com.faraway.auditall.controller;

import com.faraway.auditall.entity.CheckInfo;
import com.faraway.auditall.entity.CheckInfoReturn;
import com.faraway.auditall.entity.ProductInfo;
import com.faraway.auditall.service.imp.CheckInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/check")
@CrossOrigin  //解决跨域
public class CheckInfoController {


    @Autowired
    private CheckInfoServiceImp checkInfoServiceImp;


    @PostMapping("/insert")
    public int insertOrupddateCheckInfo(@RequestBody CheckInfo checkInfo){
        return checkInfoServiceImp.insertOrUpdateCheckInfo(checkInfo);
    }

    @PostMapping("/testExcel")
    @Scheduled(cron = "${dap.checkschedules}")
    public int testExcel(){
        try {
            checkInfoServiceImp.gererateExcel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("检验信息生成成功");
        return 1;
    }
}
