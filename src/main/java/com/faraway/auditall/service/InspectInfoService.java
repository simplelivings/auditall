package com.faraway.auditall.service;

import com.faraway.auditall.entity.InspectInfo;
import com.faraway.auditall.entity.InspectPhoto;

import javax.mail.MessagingException;
import java.io.IOException;

public interface InspectInfoService {

    public int insertOrUpdateInspectInfo(InspectInfo inspectInfo);
    public void deleteAllInspectInfo();
    public void generateExcel(InspectPhoto inspectPhoto) throws IOException, InterruptedException, MessagingException;

}
