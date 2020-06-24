package com.faraway.auditall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AuditNum {

    private int conformNum;
    private int unconformNum;
    private int finishNum;

}

