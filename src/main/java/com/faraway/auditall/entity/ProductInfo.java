package com.faraway.auditall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {

    private String partNum;

    private String firstCheck;
    private String middleCheck;
    private String lastCheck;

    private Integer spotCheckNum;
    private Integer spotCheckOkNum;
    private String spotCheckNoNum;

    private String checkNote;

}
