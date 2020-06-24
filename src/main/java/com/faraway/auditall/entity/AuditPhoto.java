package com.faraway.auditall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "auditphoto")
public class AuditPhoto {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String userName;

    private String auditPhoto;

    private Integer auditPage;

    private Integer photoNumber;

    @TableField(exist = false)
    private List<String> auditPhotoList;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @Version
    @TableField(fill = FieldFill.INSERT)
    private Integer version;
}
