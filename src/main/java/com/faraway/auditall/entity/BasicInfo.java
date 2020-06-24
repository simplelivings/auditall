package com.faraway.auditall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "basicinfo")
public class BasicInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userName;

    private String password;

    private String auditObj;

    private String auditRen;

    private String auditIte;
}
