package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.mapper.AuditItemMapper;
import com.faraway.auditall.mapper.RegisterInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditItemServiceImpTest {

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private AuditItemMapper auditItemMapper;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${selfDefination.emailSender}")
    private String sender;


    @Value("${selfDefination.validateAddress}")
    private String validateAddress;

    @Autowired
    private RegisterInfoMapper registerInfoMapper;

    @Test
    public int selectTotalAuditItemNum(int auditNum) {
        QueryWrapper<AuditItem> queryWrapper = new QueryWrapper<>();
        auditNum = 1;
        queryWrapper.eq("auditNum",auditNum);
        return auditItemMapper.selectList(queryWrapper).size();
    }

    @Test
    public void testRedis(){
        redisTemplate.opsForValue().set("cc","w是你大爷");
        System.out.println("-=====value==" + redisTemplate.opsForValue().get("cc"));
    }


    @Test
    public void sendEmailHyperLinks() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        mimeMessageHelper.setFrom("593931651@qq.com");
        mimeMessageHelper.setTo("13842618807@139.com");
        mimeMessageHelper.setSubject("测试HyperLinks");

        StringBuffer hyperLinks = new StringBuffer();

        hyperLinks.append("<html><body><p>请点击以下链接，至密码更改页面：</p>");
        hyperLinks.append("<a href=");
        hyperLinks.append(validateAddress);
        hyperLinks.append("/s?wd=测试");
        hyperLinks.append(">重置密码</a><p>如无法跳转，请复制以下地址至浏览器访问：</p>");
        hyperLinks.append("<p style=color:red;>");
        hyperLinks.append(validateAddress);
        hyperLinks.append("/s?wd=测试");
        hyperLinks.append("</p></body></html>");
        mimeMessageHelper.setText(hyperLinks.toString(),true);
        System.out.println("=====hyperlinks====="+hyperLinks);
        javaMailSender.send(mimeMessage);
    }

    @Test
    public void testSpel(){
        redisTemplate.delete("fff");
        System.out.println("=======delete is ok=======");
        System.out.println("====sender===="+sender);
    }

    @Test
    public void updateOrInsertRegisterInfo(){
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setUserName("fan");
        registerInfo.setPassword("jjjj");
        QueryWrapper<RegisterInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName","CCC");
        registerInfoMapper.update(registerInfo,queryWrapper);
        System.out.println("=======OK=========");
    }
}
