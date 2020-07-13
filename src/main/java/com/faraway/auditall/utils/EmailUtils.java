package com.faraway.auditall.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

public class EmailUtils {

    private static JavaMailSenderImpl javaMailSender;

    @Autowired
    public void EmailUtils(JavaMailSenderImpl javaMailSender){
        EmailUtils.javaMailSender = javaMailSender;
    }


    public static void emailFile(String fileName,String path) throws MessagingException {

        //1、创建一个复杂的邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //邮件主题
        helper.setSubject("Audit");

        //邮件内容
        helper.setText("审核内容", true);
        helper.setTo("13842618807@139.com");
        helper.setFrom("593931651@qq.com");

        File file = new File(path);
        if (file.exists()) {
            //附件添加word文档
            helper.addAttachment(fileName+".xlsx", file);
        }
        javaMailSender.send(mimeMessage);

        System.out.println("Email success!!!!!!!!!!!!!!!");
    }
}
