package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.InspectInfo;
import com.faraway.auditall.mapper.InspectInfoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InspectInfoServiceImpTest {

    @Autowired
    public InspectInfoMapper inspectInfoMapper;

    @Test
    public void insertOrUpdateInspectInfo() {

        InspectInfo inspectInfo = new InspectInfo();
        inspectInfo.setUserName("fan").setAuditFind("sssss").setAuditPage(2);

        if (inspectInfo != null){
            QueryWrapper<InspectInfo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("userName",inspectInfo.getUserName()).eq("auditPage",inspectInfo.getAuditPage());
            InspectInfo inspectInfoTemp = inspectInfoMapper.selectOne(queryWrapper);
            if (inspectInfoTemp !=null ){
                inspectInfoMapper.update(inspectInfo,queryWrapper);
                System.out.println("======Inspect Service update  OK=======");
            }else {
                inspectInfoMapper.insert(inspectInfo);
                System.out.println("======Inspect Service insert  OK=======");

            }
        }
    }

}
