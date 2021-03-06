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
@TableName(value = "registerinfo")
public class RegisterInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userName;

    private String password;

    private String userPhone;

    private String userId;

    private String familyName;

    private String emailAddress;

    private String recEmail;

    private Integer userRight;

    private Integer registerStatue;

    private String returnNum;

    private String corpName;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;
}
