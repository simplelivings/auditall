package com.faraway.auditall.service;

import com.faraway.auditall.entity.AuditItem;

public interface AuditItemService {
    public void insertAuditItem(AuditItem auditItem);
    public AuditItem selectOneAuditItem(int page, int auditNum);
    public int selectTotalAuditItemNum(int auditNum);
}
