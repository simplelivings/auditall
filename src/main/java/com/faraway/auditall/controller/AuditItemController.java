package com.faraway.auditall.controller;


import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.entity.BasicInfo;
import com.faraway.auditall.service.imp.AuditItemServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audititem")
@CrossOrigin  //解决跨域
public class AuditItemController {
    @Autowired
    private AuditItemServiceImp auditItemServiceImp;


    @GetMapping("/insert")
    public void insertAuditItem(AuditItem auditItem) {
        auditItemServiceImp.insertAuditItem(auditItem);
        System.out.println("==AuditItemController==插入成功====");
    }

    //接收AuditPage页面请求数据，返回auditItem
    @GetMapping("/showone")
    public AuditItem selectOneAuditItem(@RequestParam("page") int page,@RequestParam("num") int num) {
        AuditItem auditItem = new AuditItem();
        if (page >=0 && num >=0){
            auditItem = auditItemServiceImp.selectOneAuditItem(page,num);
            auditItem.setTotalNum(auditItemServiceImp.selectTotalAuditItemNum(num));
            System.out.println("=======page========"+page);
            return auditItem;
        }else{
            return auditItem;
        }
    }

}
