package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditName;
import com.faraway.auditall.mapper.AuditNameMapper;
import com.faraway.auditall.service.AuditNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditNameServiceImp implements AuditNameService {

    @Autowired
    private AuditNameMapper auditNameMapper;

    @Override
    public AuditName findPassword(String username) {
        QueryWrapper<AuditName> auditNameQueryWrapper = new QueryWrapper<>();
        auditNameQueryWrapper.eq("userName",username);
        AuditName auditName = auditNameMapper.selectOne(auditNameQueryWrapper);
        return auditName;
    }

    @Override
    public List<AuditName> findAllSenderAndReceiver() {
        List<AuditName> auditNameList = auditNameMapper.selectList(null);

        return auditNameList;
    }


}
