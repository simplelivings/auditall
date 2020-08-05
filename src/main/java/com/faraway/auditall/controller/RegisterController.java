package com.faraway.auditall.controller;


import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.service.imp.CheckInfoServiceImp;
import com.faraway.auditall.service.imp.RegisterInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;

@RestController
@RequestMapping("/register")
@CrossOrigin  //解决跨域
public class RegisterController {

    @Autowired
    private RegisterInfoServiceImp registerServiceImp;

    @Autowired
    private CheckInfoServiceImp checkInfoServiceImp;

    @GetMapping("/find")
    public int findRegisterByName(@RequestParam("userName") String userName){
        if (userName!=null){
            return registerServiceImp.findRegisterInfoByName(userName);
        }else {
            return -1;
        }
    };

    @PostMapping("/insert")
    public int insertRegister(@RequestBody RegisterInfo registerInfo){
        return registerServiceImp.insertRegister(registerInfo);
    }

    //发送检验记录
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

    //清空数据库

}
