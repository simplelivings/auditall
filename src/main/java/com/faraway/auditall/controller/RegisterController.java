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

    //查找用户权限，存在用户返回对应权限，否则返回-1
    @GetMapping("/getRight")
    public int findRegisterRight(@RequestParam("userName") String userName){
        if (userName!=null && userName.length() >0){
            RegisterInfo registerInfo = registerServiceImp.findOneRegisterByName(userName);
            if (registerInfo != null){
                    return registerInfo.getUserRight();
            }else {
                return -1;
            }
        }else {
            return -1;
        }
    }


    //查找用户名，用户名不存在，则返回100，否则返回-1
    @GetMapping("/findName")
    public int findRegisterByName(@RequestParam("userName") String userName){
        if (userName!=null){
            return registerServiceImp.findRegisterInfoByName(userName);
        }else {
            return -1;
        }
    }

    //查找手机号，手机号不存在，则返回100，否则返回-1
    @GetMapping("/findPhone")
    public int findRegisterByPhone(@RequestParam("userPhone") String userPhone){
        if (userPhone!=null){
            return registerServiceImp.findRegisterByPhone(userPhone);
        }else {
            return -1;
        }
    }

    //查找身份证号，身份证不存在，则返回100，否则返回-1
    @GetMapping("/findUserId")
    public int findRegisterByUserId(@RequestParam("userId") String userId){
        if (userId!=null){
            return registerServiceImp.findRegisterByUserId(userId);
        }else {
            return -1;
        }
    }



    @GetMapping("/emailVal")
    public int validateEmailAddress(@RequestParam("userName") String userName, @RequestParam("emailAddress") String emailAddress) throws MessagingException {
        if (userName!=null && userName.length()>0){
            RegisterInfo registerInfo = registerServiceImp.findOneRegisterByName(userName);
            if (registerInfo!=null && registerInfo.getEmailAddress().equals(emailAddress)){
                registerServiceImp.sendEmailHyperLinks(userName);
                return 1;
            }else {
                return -1;
            }
        }else {
            return -1;
        }
    }

    @PostMapping("/insert")
    public int insertRegister(@RequestBody RegisterInfo registerInfo){
        return registerServiceImp.insertRegister(registerInfo);
    }

    @PostMapping("/update")
    public int updateTegister(@RequestBody RegisterInfo registerInfo){
        if (registerInfo!=null){
            return registerServiceImp.updateRegister(registerInfo);
        }else{
            return -1;
        }
    }

}
