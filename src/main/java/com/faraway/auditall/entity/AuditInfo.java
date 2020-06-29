package com.faraway.auditall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "auditinfo")
public class AuditInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userName;

    private String auditFind;

    private Integer auditCon;

    private Integer auditPage;

    private Integer auditNum;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;



}
