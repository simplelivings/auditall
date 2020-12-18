package com.faraway.auditall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * TODO
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-21 11:29
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "inspectinfo")
public class InspectInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userName;

    private String auditFind;

    private String auditCharger;

    private Integer auditCon;

    private Integer auditPage;

    private Integer auditPicStatue;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;
}
