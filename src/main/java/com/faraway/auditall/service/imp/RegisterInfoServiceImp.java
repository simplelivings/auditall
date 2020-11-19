package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.mapper.RegisterInfoMapper;
import com.faraway.auditall.service.RegisterInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@Service
public class RegisterInfoServiceImp implements RegisterInfoService {

    @Autowired
    private RegisterInfoMapper registerInfoMapper;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${selfDefination.emailSender}")
    private String sender;


    @Value("${selfDefination.validateAddress}")
    private String validateAddress;

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

    @Override
    public void sendEmailHyperLinks(String userName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        RegisterInfo registerInfo = findOneRegisterByName(userName);
        String reciever = registerInfo.getEmailAddress();

        helper.setFrom(sender);
        helper.setTo(reciever);
        helper.setSubject("密码重置");

        StringBuffer hyperLinks = new StringBuffer();

        hyperLinks.append("<html><body><p>请点击以下链接，至密码更改页面：</p>");
        hyperLinks.append("<a href=");

        hyperLinks.append(validateAddress);
        hyperLinks.append("/"+userName);
        hyperLinks.append(">重置密码</a><p>如无法跳转，请复制以下地址至浏览器访问：</p>");
        hyperLinks.append("<p style=color:red;>");
        hyperLinks.append(validateAddress);
        hyperLinks.append("/"+userName);
        hyperLinks.append("</p></body></html>");

        helper.setText(hyperLinks.toString(),true);
        mailSender.send(message);
    }

    @Override
    public int updateRegister(RegisterInfo registerInfo) {

        if (registerInfo!=null){
            QueryWrapper<RegisterInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userName",registerInfo.getUserName());
            registerInfoMapper.update(registerInfo,queryWrapper);
            return 1;
        }else{
            return -1;
        }
    }

}
