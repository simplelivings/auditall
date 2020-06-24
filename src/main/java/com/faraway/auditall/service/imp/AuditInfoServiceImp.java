package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.service.AuditInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class AuditInfoServiceImp implements AuditInfoService {

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Override
    public AuditNum insertOrUpdateAuditInfo(AuditInfo auditInfo) {

        AuditNum auditNum = new AuditNum();
        int conformNum=0,unconformNum=0,finishNum=0;

        if (auditInfo!=null){
            QueryWrapper<AuditInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("auditPage", auditInfo.getAuditPage()).eq("userName",auditInfo.getUserName());
            AuditInfo auditInfoExist = auditInfoMapper.selectOne(queryWrapper);

            QueryWrapper<AuditInfo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("userName",auditInfo.getUserName());
            List<AuditInfo> auditInfoList = auditInfoMapper.selectList(queryWrapper1);
            for (int i = 0; i < auditInfoList.size(); i++) {
                if (auditInfoList.get(i).getAuditCon()<2){
                    conformNum += auditInfoList.get(i).getAuditCon();
                }
            }
            auditNum.setConformNum(conformNum)
                    .setFinishNum(auditInfoList.size())
                    .setUnconformNum(auditInfoList.size()-conformNum);

            if (auditInfoExist != null) {
                auditInfoMapper.update(auditInfo, queryWrapper);
                System.out.println("==AuditInfoServiceImp==已存在，并更新成功=====");
                return auditNum;
            } else {
                auditInfoMapper.insert(auditInfo);
                System.out.println("==AuditInfoServiceImp==插入成功=====");
                return auditNum;
            }
        }else{
            return auditNum;
        }
    }

}
