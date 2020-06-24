package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.mapper.AuditItemMapper;
import com.faraway.auditall.service.AuditItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditItemServiceImp implements AuditItemService {

    @Autowired
    private AuditItemMapper auditItemMapper;

    @Override
    public void insertAuditItem(AuditItem auditItem) {
        if (auditItem!=null){
            auditItemMapper.insert(auditItem);
        }
    }

    @Override
    public AuditItem selectOneAuditItem(int page, int auditNum) {
        QueryWrapper<AuditItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("page",page).eq("auditNum",auditNum);
        AuditItem auditItem = auditItemMapper.selectOne(queryWrapper);
        return auditItem;
    }

    @Override
    public int selectTotalAuditItemNum(int auditNum) {
        QueryWrapper<AuditItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("auditNum",auditNum);
        return auditItemMapper.selectList(queryWrapper).size();
    }

}
