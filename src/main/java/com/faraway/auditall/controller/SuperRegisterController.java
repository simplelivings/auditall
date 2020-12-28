package com.faraway.auditall.controller;

import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.service.imp.AuditNameServiceImp;
import com.faraway.auditall.service.imp.RegisterInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * TODO
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-12-07 15:28
 */

@RestController
@RequestMapping("/superRegister")
@CrossOrigin  //解决跨域
public class SuperRegisterController {
    @Autowired
    private RegisterInfoServiceImp registerServiceImp;

    @GetMapping("/findRegister")
    public RegisterInfo findRegisterByName(@RequestParam("checkName") String checkName) {
        RegisterInfo registerInfo = new RegisterInfo();
        if (checkName != null) {
            registerInfo = registerServiceImp.findOneRegisterByName(checkName);
            return registerInfo;
        } else {
//            registerInfo.setReturnNum("wrong");
            return registerInfo;
        }
    }

    @GetMapping("/changeRegister")
    public int changeRegisterByName(@RequestParam("checkName") String checkName, @RequestParam("userRight") String userRight) {

        if (checkName != null && checkName.length()>0 && userRight != null && userRight.length() > 0) {

            int tempRight = 6;

            switch (userRight) {
                case "普通付费":
                    tempRight = 7;
                    break;
                case "高级付费":
                    tempRight = 8;
                    break;
                case "超级付费":
                    tempRight = 9;
                    break;
                case "删除用户":
                    tempRight = -1;
                    break;
                default:
                    break;
            }

            String[] tempNames = checkName.split(",");
            if (tempRight > 0) {
                for (String tempName : tempNames) {
                    RegisterInfo registerInfo = new RegisterInfo();
                    registerInfo = registerServiceImp.findOneRegisterByName(tempName);
                    if (registerInfo!=null){
                        registerInfo.setUserRight(tempRight);
                        registerServiceImp.updateRegister(registerInfo);
                        return 2;
                    }else {
                        return -1;
                    }
                }
                return -1;
            } else {
                for (String tempName : tempNames) {
                    registerServiceImp.deleteRegisterByName(tempName);
                }
                return 1;
            }

        } else {
            return -1;
        }

    }


}
