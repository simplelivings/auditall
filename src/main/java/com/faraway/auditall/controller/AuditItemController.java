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
        auditItemServiceImp.insertOrUpdateAuditItem(auditItem);
    }

    //接收AuditPage页面请求数据，返回auditItem
    @GetMapping("/showone")
    public AuditItem selectOneAuditItem(@RequestParam("page") int page,@RequestParam("num") int num) {
        AuditItem auditItem = new AuditItem();
        if (page >=0 && num >=0){
            if (auditItemServiceImp.selectOneAuditItem(page, num)!=null){
                auditItem = auditItemServiceImp.selectOneAuditItem(page,num);
                auditItem.setTotalNum(auditItemServiceImp.selectTotalAuditItemNum(num));
                return auditItem;
            }else {
                return auditItem;
            }

        }else{
            return auditItem;
        }
    }

}
