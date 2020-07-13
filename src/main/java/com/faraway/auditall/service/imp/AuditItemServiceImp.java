package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.mapper.AuditItemMapper;
import com.faraway.auditall.service.AuditItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditItemServiceImp implements AuditItemService {

    @Autowired
    private AuditItemMapper auditItemMapper;

    @Override
    public void insertOrUpdateAuditItem(AuditItem auditItem) {
        if (auditItem!=null){
            QueryWrapper<AuditItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("auditNum",auditItem.getAuditNum());

            //删除数据库中已有数据
            if ((auditItem.getAuditItemList().get(3)!="")||(auditItem.getAuditItemList().get(3)!=null)){
                auditItemMapper.delete(queryWrapper);
            }

            List<String> auditItemList = new ArrayList<>();

            //去除空项目
            for (int i = 0; i < auditItem.getAuditItemList().size(); i++) {
                if ((auditItem.getAuditItemList().get(i)!="")&&(auditItem.getAuditItemList().get(i)!=null)){
                    auditItemList.add(auditItem.getAuditItemList().get(i));
                }
            }

            //数据写入数据库
            for (int i = 0; i < auditItemList.size(); i++) {
                AuditItem auditItemTemp = new AuditItem();
                auditItemTemp.setAuditNum(auditItem.getAuditNum());
                auditItemTemp.setPage(i+1);
                auditItemTemp.setAuditItem(auditItemList.get(i));
                auditItemMapper.insert(auditItemTemp);
            }
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
