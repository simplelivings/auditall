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


    @PostMapping(value = "/insert",produces ="text/html;charset=utf-8")
    public void insertAuditItem(@RequestBody AuditItem auditItem) {
        System.out.println("======auditItem========="+auditItem);
        auditItemServiceImp.insertOrUpdateAuditItem(auditItem);
        System.out.println("==AuditItemController==插入成功====");
    }

    //接收AuditPage页面请求数据，返回auditItem
    @GetMapping("/showone")
    public AuditItem selectOneAuditItem(@RequestParam("page") int page,@RequestParam("num") int num) {
        AuditItem auditItem = new AuditItem();
        System.out.println("=======page========"+page);
        System.out.println("=======num========"+num);
        if (page >=0 && num >=0){
            auditItem = auditItemServiceImp.selectOneAuditItem(page,num);
            auditItem.setTotalNum(auditItemServiceImp.selectTotalAuditItemNum(num));
            return auditItem;
        }else{
            return auditItem;
        }
    }

}
