package com.faraway.auditall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "audititem")
public class AuditItem {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer page;

    private String auditItem;

    private Integer auditNum;

    private String userName;

    @TableField(exist = false)
    private List<String> auditItemList;

    @TableField(exist = false)
    private Integer totalNum;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;



}
