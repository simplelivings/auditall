package com.faraway.auditall.service;

import com.faraway.auditall.entity.AuditName;

import java.util.List;

public interface AuditNameService {
    public AuditName findPassword(String username);
    public List<AuditName> findAllSenderAndReceiver();
}
