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
import java.util.Random;

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
    public int findRegisterByPhone(String phone) {
        QueryWrapper<RegisterInfo> registerInfoQueryWrapper = new QueryWrapper<>();
        registerInfoQueryWrapper.eq("userPhone",phone);
        if (registerInfoMapper.selectCount(registerInfoQueryWrapper)>0){
            System.out.println("register-手机号已存在");
            return -1;
        }else {
            return 100;
        }
    }

    @Override
    public int findRegisterByUserId(String userId) {
        QueryWrapper<RegisterInfo> registerInfoQueryWrapper = new QueryWrapper<>();
        registerInfoQueryWrapper.eq("userId",userId);
        if (registerInfoMapper.selectCount(registerInfoQueryWrapper)>0){
            System.out.println("register-身份证号已存在");
            return -1;
        }else {
            return 100;
        }
    }

    @Override
    public int deleteRegisterByName(String userName) {
        if (userName !=null ){
            QueryWrapper<RegisterInfo> registerInfoQueryWrapper = new QueryWrapper<>();
            registerInfoQueryWrapper.eq("userName",userName);
            registerInfoMapper.delete(registerInfoQueryWrapper);
            return 1;
        }else {
            return -1;
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
        String returnNum = getRandomString(6);
        registerInfo.setReturnNum(returnNum);

        QueryWrapper<RegisterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName",userName);
        registerInfoMapper.update(registerInfo,queryWrapper);

        helper.setFrom(sender);
        helper.setTo(reciever);
        helper.setSubject("密码重置");

        StringBuffer hyperLinks = new StringBuffer();

        hyperLinks.append("<html><body><p>请点击以下链接，至密码更改页面：</p>");
        hyperLinks.append("<a href=");

        hyperLinks.append(validateAddress);
        hyperLinks.append("/"+userName);
        hyperLinks.append(">重置密码</a>");
        hyperLinks.append("<p> </p>");
        hyperLinks.append("<p>并输入如下校验码：</p>");
        hyperLinks.append("<p>校验码:  ");
        hyperLinks.append(returnNum);
        hyperLinks.append("</p>");
        hyperLinks.append("<p> </p>");
        hyperLinks.append("<p>如无法跳转，请复制以下地址至浏览器访问：</p>");
        hyperLinks.append("<p style=color:red;>");
        hyperLinks.append(validateAddress);
        hyperLinks.append("/"+userName);
        hyperLinks.append("</p></body></html>");

        helper.setText(hyperLinks.toString(),true);
        mailSender.send(message);
    }

    /**
     * 用于用户忘记密码，重新更改密码
     * @param registerInfo
     * @return
     */

    @Override
    public int updateRegister(RegisterInfo registerInfo) {

        if (registerInfo!=null){
            //判断前端返回注册信息，用户名与校验码是否为空
            if (registerInfo.getUserName()!=null&&registerInfo.getUserName().length()>0
                &&registerInfo.getReturnNum()!=null && registerInfo.getReturnNum().length()>0){
                String userName = registerInfo.getUserName();
                RegisterInfo registerInfo1 = findOneRegisterByName(userName);
                if (registerInfo1.getReturnNum()!=null){

                    //判断前后端校验码是否一致
                    if (registerInfo.getReturnNum().equalsIgnoreCase(registerInfo1.getReturnNum())){
                        QueryWrapper<RegisterInfo> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("userName",userName);
                        registerInfoMapper.update(registerInfo,queryWrapper);
                        return 1;
                    }else {
                        return -1;
                    }
                }else {
                    return -1;
                }
            }else {
                return -1;
            }
        }else{
            return -1;
        }
    }

    public String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

}
