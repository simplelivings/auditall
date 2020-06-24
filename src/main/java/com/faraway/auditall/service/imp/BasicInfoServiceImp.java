package com.faraway.auditall.service.imp;

import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.service.BasicInfoService;
import org.springframework.stereotype.Service;

@Service
public class BasicInfoServiceImp implements BasicInfoService {

    String[] auditIteList = {"分层审核", "安全检查", "现场检查", "质量检验", "工艺检查", "综合检查"};
    String[] auditRenList = {"班组级", "工段级", "科室级", "部门级", "公司级"};
    String[] auditObjList = {"stamp", "weld", "cut", "metal", "equipment", "logic", "quality"};
    int auditIteNum = 0;
    int auditObjNum = 0;
    String auditObjTemp;

    @Override
    public int findAuditNum(BasicInfo basicInfo) {
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
                    } else {
                        auditIteNum = i + 4;
                    }
                }
            }

            if ((basicInfo.getAuditObj().toLowerCase().contains("d")) ||
                    (basicInfo.getAuditObj().contains("冲"))) {
                auditObjTemp = "stamp";
            } else if ((basicInfo.getAuditObj().toLowerCase().contains("w")) ||
                    (basicInfo.getAuditObj().contains("焊"))) {
                auditObjTemp = "welb";
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
            }else if ((basicInfo.getAuditObj().toLowerCase().contains("l"))||
                    (basicInfo.getAuditObj().contains("物流"))){
                auditObjTemp = "logical";
            }else if ((basicInfo.getAuditObj().toLowerCase().contains("q"))||
                    (basicInfo.getAuditObj().contains("质量"))){
                auditObjTemp = "quality";
            }
        }

        //获得审核对象编号
        if (auditObjTemp!=null){
            for (int i = 0; i < auditObjList.length; i++) {
                if (auditObjTemp.equals(auditObjList[i])){
                    auditObjNum = i;
                }
            }
        }

        //判断密码是否正确，并返回审核表编号
        if (basicInfo.getPassword().equals("269868")) {
            return (auditIteNum*7+auditObjNum);
        } else {
            return -1;
        }
    }
}
