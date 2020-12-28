package com.faraway.auditall.controller;

import com.faraway.auditall.entity.InspectInfo;
import com.faraway.auditall.service.imp.InspectInfoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * TODO
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-21 11:38
 */

@RestController
@RequestMapping("/inspectInfo")
@CrossOrigin  //解决跨域
public class InspectInfoController {

    @Autowired
    private InspectInfoServiceImp inspectInfoServiceImp;

    @PostMapping(value = "/insert")
    public int issertOrUpdateInspectInfo(@RequestBody InspectInfo inspectInfo) {
        if (inspectInfo != null) {
            //检查发现不为空，则把数据写入数据库中
            if ((inspectInfo.getAuditFind() != null && inspectInfo.getAuditFind().length() > 0)
                    || (inspectInfo.getAuditCon() != null && inspectInfo.getAuditCon() > 0)
                    || (inspectInfo.getAuditCharger() != null && inspectInfo.getAuditCharger().length() > 0)
            ) {
                inspectInfoServiceImp.insertOrUpdateInspectInfo(inspectInfo);
                return 1;
            }else return -1;
        } else {
            return -1;
        }
    }
}
