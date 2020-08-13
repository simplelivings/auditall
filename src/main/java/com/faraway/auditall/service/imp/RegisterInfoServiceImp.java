package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.mapper.RegisterInfoMapper;
import com.faraway.auditall.service.RegisterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterInfoServiceImp implements RegisterInfoService {

    @Autowired
    private RegisterInfoMapper registerInfoMapper;

    @Override
    public int findRegisterInfoByName(String userName) {
        QueryWrapper<RegisterInfo> registerInfoQueryWrapper = new QueryWrapper<>();
        registerInfoQueryWrapper.eq("userName",userName);
        if (registerInfoMapper.selectCount(registerInfoQueryWrapper)>0){
            System.out.println("register-用户名已存在");
            return -1;
        }else {
            return 100;
        }
    }

    @Override
    public int insertRegister(RegisterInfo registerInfo) {
        if (registerInfo!=null){
            registerInfoMapper.insert(registerInfo);
            return 200;
        }else {
            return -1;
        }
    }


    @Override
    public RegisterInfo findOneRegisterByName(String userName) {
        if (userName!=null){
            QueryWrapper<RegisterInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userName",userName);
            RegisterInfo registerInfo = registerInfoMapper.selectOne(queryWrapper);
            return registerInfo;
        }else {
            return null;
        }
    }

    @Override
    public List<RegisterInfo> findAllRegister() {
        List<RegisterInfo> registerInfoList = new ArrayList<>();
        if (registerInfoMapper.selectList(null)!=null){
            registerInfoList = registerInfoMapper.selectList(null);
        }
        return registerInfoList;
    }

}
