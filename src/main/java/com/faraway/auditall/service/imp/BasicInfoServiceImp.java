package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.*;
import com.faraway.auditall.mapper.*;
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
import java.util.Date;
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
    private final int INSPECTNUM = 35;

    @Autowired
    private BasicInfoMapper basicInfoMapper;

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Autowired
    private AuditPhotoMapper auditPhotoMapper;

    @Autowired
    private InspectInfoMapper inspectInfoMapper;

    @Autowired
    private InspectPhotoMapper inspectPhotoMapper;

    @Autowired
    private AuditNameServiceImp auditNameServiceImp;

    @Autowired
    private RegisterInfoServiceImp registerInfoServiceImp;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public int findAuditNum(BasicInfo basicInfo) {
        RegisterInfo registerInfo = null;
        //注册时，用户密码，存于数据库中
        String password = "";

        //用户名，前端传过来
        String userName = "";

        //用户是单日或多日，才可登录指定次数；
        int checkDay = 0;

        //单日最多登录次数
        int maxLoginTimes = 0;


        QueryWrapper<BasicInfo> queryWrapperBasicInfo = new QueryWrapper<>();
        QueryWrapper<InspectInfo> queryWrapperAuditInfo = new QueryWrapper<>();
        QueryWrapper<InspectPhoto> queryWrapperAuditPhoto = new QueryWrapper<>();
        if (basicInfo != null && basicInfo.getUserName() != null) {
            userName = basicInfo.getUserName();

            //判断用户权限，1-免费用户7天/次，2-普通付费用户2次/天，3-重大付费用户4次/天，4-超级付费用户，不限次

            //依据用户名，从数据库获得基本信息
            queryWrapperBasicInfo.eq("userName", userName);

            //依据审核编号，从数据库获得审核项目，并按主键排序
            queryWrapperAuditInfo.eq("userName", userName);

            //依据用户名，从数据库获得审核信息，并按审核页码排序
            queryWrapperAuditPhoto.eq("userName", userName);


            //判断是否已有用户登录，如有返回101
            if (basicInfoMapper.selectList(queryWrapperBasicInfo).size() > 0) {
                return 101;//已有用户登录
            } else {
                if (registerInfoServiceImp.findOneRegisterByName(userName) != null) {
                    registerInfo = registerInfoServiceImp.findOneRegisterByName(userName);
                    password = registerInfo.getPassword();
                    //判断密码是否正确，用户是否登录，如未登录设置登录状态为1，并返回审核表编号
                    if (basicInfo.getPassword().equals(password)) {
                        inspectInfoMapper.delete(queryWrapperAuditInfo);//清空之前审核信息
                        inspectPhotoMapper.delete(queryWrapperAuditPhoto);//清空之前审核图片

                        System.out.println("==========times++++"+checkRegisterRight(userName));
                        switch (checkRegisterRight(userName)) {
                            case 6://普通免费老用户，每周一次
                                checkDay = 7;
                                maxLoginTimes = 1;
                                return checkLoginTime(maxLoginTimes,checkDay,userName);
                            case 7://普通付费或新用户，每天2次
                                checkDay = 1;
                                maxLoginTimes = 2;
                                return checkLoginTime(maxLoginTimes,checkDay,userName);
                            case 8://高级付费用户，每天4次
                                checkDay = 1;
                                maxLoginTimes = 4;
                                return checkLoginTime(maxLoginTimes,checkDay,userName);
                            case 9://超级付费用户，每天20次
                                checkDay = 1;
                                maxLoginTimes = 200;
                                return checkLoginTime(maxLoginTimes,checkDay,userName);
                            default:
                                return -1;
                        }

                    } else {
                        //密码输入次数过多，则冻结账户，利用redis实现
                        String keyStatue = basicInfo.getUserName() + "statue";
                        redisTemplate.opsForList().leftPush(keyStatue, Integer.toString(basicInfo.getUserStatu()));
                        redisTemplate.expire(keyStatue, 60, TimeUnit.MINUTES);
                        if (redisTemplate.opsForList().size(keyStatue) > 3) {
                            redisTemplate.opsForList().trim(keyStatue, 0, 1);
                            return 103;//密码输入次数过多
                        }
                        return -1;
                    }
                } else {
                    return 102;//用户名不存在
                }

            }

        } else {
            return -1;//用户信息有问题
        }
    }

    //嘉翔登录校验，不验证用户权限
    @Override
    public int findJxAuditNum(BasicInfo basicInfo) {
        RegisterInfo registerInfo = null;
        String password = "";

        QueryWrapper<BasicInfo> queryWrapperBasicInfo = new QueryWrapper<>();
        QueryWrapper<AuditInfo> queryWrapperAuditInfo = new QueryWrapper<>();
        QueryWrapper<AuditPhoto> queryWrapperAuditPhoto = new QueryWrapper<>();

        QueryWrapper<InspectInfo> inspectInfoQueryWrapper = new QueryWrapper<>();
        QueryWrapper<InspectPhoto> inspectPhotoQueryWrapper = new QueryWrapper<>();


        if (basicInfo != null) {
            //依据用户名，从数据库获得基本信息
            queryWrapperBasicInfo.eq("userName", basicInfo.getUserName());

            //依据用户名，从数据库获得审核项目，并按主键排序
            queryWrapperAuditInfo.eq("userName", basicInfo.getUserName());

            //依据用户名，从数据库获得审核信息，并按审核页码排序
            queryWrapperAuditPhoto.eq("userName", basicInfo.getUserName());

            inspectInfoQueryWrapper.eq("userName", basicInfo.getUserName());

            inspectPhotoQueryWrapper.eq("userName", basicInfo.getUserName());

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
                        int auditNum = auditIteNum * 7 + auditObjNum;
                        if (auditNum >= 0 && auditNum <= 34) {
                            auditInfoMapper.delete(queryWrapperAuditInfo);//清空之前审核信息
                            auditPhotoMapper.delete(queryWrapperAuditPhoto);//清空之前审核图片
                        } else if (auditNum == 35) {
                            inspectInfoMapper.delete(inspectInfoQueryWrapper);
                            inspectPhotoMapper.delete(inspectPhotoQueryWrapper);
                        }
                        return auditNum;
                    } else {
                        String keyStatue = basicInfo.getUserName() + "statue";
                        redisTemplate.opsForList().leftPush(keyStatue, Integer.toString(basicInfo.getUserStatu()));
                        redisTemplate.expire(keyStatue, 60, TimeUnit.MINUTES);
                        if (redisTemplate.opsForList().size(keyStatue) > 3) {
                            redisTemplate.opsForList().trim(keyStatue, 0, 1);
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
        if (userName != null && userName.length() > 0) {
            QueryWrapper<BasicInfo> basicInfoQueryWrapper = new QueryWrapper<>();
            basicInfoQueryWrapper.eq("userName", userName);
            QueryWrapper<AuditInfo> auditInfoQueryWrapper = new QueryWrapper<>();
            auditInfoQueryWrapper.eq("userName", userName);
            QueryWrapper<AuditPhoto> auditPhotoQueryWrapper = new QueryWrapper<>();
            auditPhotoQueryWrapper.eq("userName", userName);

            basicInfoMapper.delete(basicInfoQueryWrapper);
            auditInfoMapper.delete(auditInfoQueryWrapper);
            auditPhotoMapper.delete(auditPhotoQueryWrapper);
            return 1;
        } else {
            return -1;
        }
    }

    //检查用户权限，默认普通用户注册时，统一为-1；1个月后不交费后，设置为6；JX用户注册时，统一为0或2
    @Override
    public int checkRegisterRight(String userName) {
        RegisterInfo registerInfo = registerInfoServiceImp.findOneRegisterByName(userName);
        if (registerInfo!=null && registerInfo.getUpdateTime()!=null){
            Long registerTime = Math.round((new Date().getTime() - registerInfo.getUpdateTime().getTime()) / 86400000 / 30 + 0d);
            if (registerInfo.getUserRight() > 0) {
                return registerInfo.getUserRight();
            } else if (registerTime < 1) {   //第一个月免费,每天使用2次
                return 7;
            } else {
                return -1;
            }
        }else {
            return -1;
        }
    }

    //检查用户登录次数
    @Override
    public int checkLoginTime(int maxLoginTimes, int checkDay,String userName) {
        int loginCount = 0;

        if (redisTemplate.opsForValue().get(userName + "count") != null) {
            loginCount = Integer.parseInt(redisTemplate.opsForValue().get(userName + "count").toString());
        }

        if (redisTemplate.opsForValue().get(userName + "count") == null) {
            redisTemplate.opsForValue().set(userName + "count", "1", 24*checkDay, TimeUnit.HOURS);
            return INSPECTNUM;
        } else if (loginCount >= maxLoginTimes) {
            return 106;//用户超过使用次数
        } else {
            redisTemplate.opsForValue().increment(userName + "count", 1);
            return INSPECTNUM;
        }
    }


}
