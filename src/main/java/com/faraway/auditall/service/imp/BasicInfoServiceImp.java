package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.*;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.mapper.AuditPhotoMapper;
import com.faraway.auditall.mapper.BasicInfoMapper;
import com.faraway.auditall.service.AuditNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.faraway.auditall.service.BasicInfoService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BasicInfoServiceImp implements BasicInfoService {

    String[] auditIteList = {"分层审核", "安全检查", "现场检查", "质量检验", "工艺检查", "综合检查"};
    String[] auditRenList = {"班组级", "工段级", "科室级", "部门级", "公司级"};
    String[] auditObjList = {"stamp", "weld", "cut", "metal", "equipment", "logic", "quality"};
    int auditIteNum = 0;
    int auditObjNum = 0;
    String auditObjTemp;

    @Autowired
    private BasicInfoMapper basicInfoMapper;

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Autowired
    private AuditPhotoMapper auditPhotoMapper;

    @Autowired
    private AuditNameServiceImp auditNameServiceImp;

    @Autowired
    private RegisterInfoServiceImp registerInfoServiceImp;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public int findAuditNum(BasicInfo basicInfo) {
        RegisterInfo registerInfo = null;
        String password = "";

        QueryWrapper<BasicInfo> queryWrapperBasicInfo = new QueryWrapper<>();
        QueryWrapper<AuditInfo> queryWrapperAuditInfo = new QueryWrapper<>();
        QueryWrapper<AuditPhoto> queryWrapperAuditPhoto = new QueryWrapper<>();
        if (basicInfo != null) {


            //依据用户名，从数据库获得基本信息
            queryWrapperBasicInfo.eq("userName", basicInfo.getUserName());

            //依据审核编号，从数据库获得审核项目，并按主键排序
            queryWrapperAuditInfo.eq("userName", basicInfo.getUserName());

            //依据用户名，从数据库获得审核信息，并按审核页码排序
            queryWrapperAuditPhoto.eq("userName", basicInfo.getUserName());


            //判断是否已有用户登录，如有返回101
            if (basicInfoMapper.selectList(queryWrapperBasicInfo).size() > 0) {
                return 101;
            } else {

                //得到审核项目的编号
                for (int i = 0; i < auditIteList.length; i++) {
                    if (basicInfo.getAuditIte().equals(auditIteList[i])) {
                        if (basicInfo.getAuditIte().equals("分层审核")) {
                            for (int j = 0; j < auditRenList.length; j++) {
                                if (basicInfo.getAuditRen().equals(auditRenList[j])) {
                                    auditIteNum = j;
                                }
                            }
                        } else {
                            auditIteNum = i + 4;
                        }
                    }
                }

                if (basicInfo.getAuditIte().equals("分层审核")) {
                    if ((basicInfo.getAuditObj().toLowerCase().contains("d")) ||
                            (basicInfo.getAuditObj().contains("冲"))) {
                        auditObjTemp = "stamp";
                    } else if ((basicInfo.getAuditObj().toLowerCase().contains("w")) ||
                            (basicInfo.getAuditObj().contains("焊"))) {
                        auditObjTemp = "weld";
                    } else if ((basicInfo.getAuditObj().toLowerCase().contains("c")) ||
                            (basicInfo.getAuditObj().contains("剪"))) {
                        auditObjTemp = "cut";
                    } else if ((basicInfo.getAuditObj().toLowerCase().contains("m")) ||
                            (basicInfo.getAuditObj().contains("钣"))) {
                        auditObjTemp = "metal";
                    } else if ((basicInfo.getAuditObj().toLowerCase().contains("e")) ||
                            (basicInfo.getAuditObj().contains("设备")) || (
                            basicInfo.getAuditObj().contains("工装"))) {
                        auditObjTemp = "equipment";
                    } else if ((basicInfo.getAuditObj().toLowerCase().contains("l")) ||
                            (basicInfo.getAuditObj().contains("物流"))) {
                        auditObjTemp = "logic";
                    } else if ((basicInfo.getAuditObj().toLowerCase().contains("q")) ||
                            (basicInfo.getAuditObj().contains("质量"))) {
                        auditObjTemp = "quality";
                    }

                    //获得审核对象编号
                    if (auditObjTemp != null) {
                        for (int i = 0; i < auditObjList.length; i++) {
                            if (auditObjTemp.equals(auditObjList[i])) {
                                auditObjNum = i;
                            }
                        }
                    }
                }

                if (registerInfoServiceImp.findOneRegisterByName(basicInfo.getUserName()) != null) {
                    registerInfo = registerInfoServiceImp.findOneRegisterByName(basicInfo.getUserName());
                    password = registerInfo.getPassword();
                    //判断密码是否正确，用户是否登录，如未登录设置登录状态为1，并返回审核表编号
                    if (basicInfo.getPassword().equals(password)) {
                        auditInfoMapper.delete(queryWrapperAuditInfo);//清空之前审核信息
                        auditPhotoMapper.delete(queryWrapperAuditPhoto);//清空之前审核图片
                        return (auditIteNum * 7 + auditObjNum);
                    } else {
                        String keyStatue = basicInfo.getUserName() + "statue";
                        redisTemplate.opsForList().leftPush(keyStatue,Integer.toString(basicInfo.getUserStatu()));
                        redisTemplate.expire(keyStatue,10,TimeUnit.MINUTES);
                        if (redisTemplate.opsForList().size(keyStatue) > 3) {
                            redisTemplate.opsForList().trim(keyStatue,0,1);
                            return 103;
                        }

                        return -1;
                    }
                } else {
                    return 102;//用户名不存在
                }

            }

        } else {
            return -1;
        }
    }

    @Override
    public int findAuditNumSuper(BasicInfo basicInfo) {
        if (basicInfo != null) {
            //得到审核项目的编号
            for (int i = 0; i < auditIteList.length; i++) {
                if (basicInfo.getAuditIte().equals(auditIteList[i])) {
                    if (basicInfo.getAuditIte().equals("分层审核")) {
                        for (int j = 0; j < auditRenList.length; j++) {
                            if (basicInfo.getAuditRen().equals(auditRenList[j])) {
                                auditIteNum = j;
                            }
                        }
                    } else if (basicInfo.getAuditIte().equals("质量检验")) {
                        auditIteNum = i + 4;
                    }
                }
            }


            if ((basicInfo.getAuditObj().toLowerCase().contains("d")) ||
                    (basicInfo.getAuditObj().contains("冲"))) {
                auditObjTemp = "stamp";
            } else if ((basicInfo.getAuditObj().toLowerCase().contains("w")) ||
                    (basicInfo.getAuditObj().contains("焊"))) {
                auditObjTemp = "weld";
            } else if ((basicInfo.getAuditObj().toLowerCase().contains("c")) ||
                    (basicInfo.getAuditObj().contains("剪"))) {
                auditObjTemp = "cut";
            } else if ((basicInfo.getAuditObj().toLowerCase().contains("m")) ||
                    (basicInfo.getAuditObj().contains("钣"))) {
                auditObjTemp = "metal";
            } else if ((basicInfo.getAuditObj().toLowerCase().contains("e")) ||
                    (basicInfo.getAuditObj().contains("设备")) || (
                    basicInfo.getAuditObj().contains("工装"))) {
                auditObjTemp = "equipment";
            } else if ((basicInfo.getAuditObj().toLowerCase().contains("l")) ||
                    (basicInfo.getAuditObj().contains("物流"))) {
                auditObjTemp = "logic";
            } else if ((basicInfo.getAuditObj().toLowerCase().contains("q")) ||
                    (basicInfo.getAuditObj().contains("质量"))) {
                auditObjTemp = "quality";
            }
            //获得审核对象编号
            if (auditObjTemp != null) {
                for (int i = 0; i < auditObjList.length; i++) {
                    if (auditObjTemp.equals(auditObjList[i])) {
                        auditObjNum = i;
                    }
                }

            }

        }


        String password = auditNameServiceImp.findPassword("super").getPassword();
        //判断密码是否正确，并返回审核表编号
        if (basicInfo.getPassword().equals(password)) {
            return (auditIteNum * 7 + auditObjNum);
        } else {
            return -1;
        }
    }

    @Override
    public int insertOrUpdateBasicInfo(BasicInfo basicInfo) {

        QueryWrapper<BasicInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", basicInfo.getUserName());
        BasicInfo basicInfoExist = null;
        if (basicInfoMapper.selectOne(queryWrapper) != null) {
            basicInfoExist = basicInfoMapper.selectOne(queryWrapper);
        }

        if (basicInfoExist != null) {
            basicInfoMapper.update(basicInfo, queryWrapper);
        } else {
            basicInfoMapper.insert(basicInfo);
        }
        return 1;
    }

    @Override
    public void deleteAllBasicInfo() {
        basicInfoMapper.delete(null);
    }

    @Override
    public BasicInfo findBasicInfoByName(String name) {
        QueryWrapper<BasicInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName", name);
        BasicInfo basicInfo = null;
        if (basicInfoMapper.selectOne(queryWrapper) != null) {
            basicInfo = basicInfoMapper.selectOne(queryWrapper);
        }
        if (basicInfo != null) {
            return basicInfo;
        } else {
            return null;
        }
    }

    @Override
    public int deleteTempData(String userName) {
        if (userName!=null && userName.length() > 0){
            QueryWrapper<BasicInfo> basicInfoQueryWrapper = new QueryWrapper<>();
            basicInfoQueryWrapper.eq("userName",userName);
            QueryWrapper<AuditInfo> auditInfoQueryWrapper = new QueryWrapper<>();
            auditInfoQueryWrapper.eq("userName",userName);
            QueryWrapper<AuditPhoto> auditPhotoQueryWrapper = new QueryWrapper<>();
            auditPhotoQueryWrapper.eq("userName",userName);

            basicInfoMapper.delete(basicInfoQueryWrapper);
            auditInfoMapper.delete(auditInfoQueryWrapper);
            auditPhotoMapper.delete(auditPhotoQueryWrapper);
            return 1;
        }else{
            return -1;
        }
    }

}
