package com.faraway.auditall.controller;


import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.service.imp.RegisterInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@CrossOrigin  //解决跨域
public class RegisterController {

    @Autowired
    private RegisterInfoServiceImp registerServiceImp;

    @GetMapping("/find")
    public int findRegisterByName(@RequestParam("userName") String userName){
        System.out.println("userName========"+userName);
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

}
