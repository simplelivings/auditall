package com.faraway.auditall;

import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.mapper.AuditItemMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuditallApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("==========");
    }


    @Autowired
    private AuditItemMapper auditItemMapper;


    @Test
    private void insertAuditItem(){
        AuditItem auditItem =  new AuditItem();
        auditItem.setAuditItem("UI昂泡面哥").setAuditNum(1);

        int insert = auditItemMapper.insert(auditItem);
        System.out.println("========"+insert);
    }



}
