package com.faraway.auditall.service;

import com.faraway.auditall.entity.CheckInfo;

import javax.mail.MessagingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface CheckInfoService {
    public List<CheckInfo> findAllCheckInfo();
    public void deleteAllCheckInfo();
    public int insertOrUpdateCheckInfo(CheckInfo checkInfo);
    public void gererateExcel() throws IOException, MessagingException, InterruptedException;
}
