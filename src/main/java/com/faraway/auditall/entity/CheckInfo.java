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
@TableName(value = "checkinfo")
public class CheckInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userName;

    private Integer auditNum;

    private Integer checkPage;

    private Integer checkType;

    private String partNum;

    private Integer productNum;

    private Integer checkNum;

    private Integer checkStatu;

    private String checkNote;

    @TableField(fill = FieldFill.INSERT)
    private Date checkDate;

    private String produceTime;

    private String checkTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;
}
