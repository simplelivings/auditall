package com.faraway.auditall.controller;

import com.faraway.auditall.entity.CheckInfo;
import com.faraway.auditall.entity.CheckInfoReturn;
import com.faraway.auditall.entity.ProductInfo;
import com.faraway.auditall.service.imp.CheckInfoServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CheckInfoControllerTest {

    @Autowired
    private CheckInfoServiceImp checkInfoServiceImp;

    @Test
    void returnCheckInfo() {
        List<CheckInfo> checkInfoList = new ArrayList<>();
        if (checkInfoServiceImp.findAllCheckInfo() != null) {
            checkInfoList = checkInfoServiceImp.findAllCheckInfo();
        }

        CheckInfoReturn checkInfoReturn = new CheckInfoReturn();
        List<String> tempList = new ArrayList<>();
        List<ProductInfo> productInfoTempList = new ArrayList<>();
        int spotCheckNumTemp = 0;
        int spotCheckOkNumTemp = 0;
        int spotCheckNoNumTemp = 0;
        String checkStatuTemp = null;
        String partNum = null;

        if (checkInfoList != null && checkInfoList.size() > 0) {
            for (int i = 0; i < checkInfoList.size(); i++) {
                if (!tempList.contains(checkInfoList.get(i).getPartNum())) {
                    tempList.add(checkInfoList.get(i).getPartNum());
                }
            }
        }

        if (tempList != null && tempList.size() > 0) {
            for (int i = 0; i < tempList.size(); i++) {
                ProductInfo productInfo = new ProductInfo();
                partNum = tempList.get(i);
                spotCheckNumTemp = 0;
                spotCheckOkNumTemp = 0;
                spotCheckNoNumTemp = 0;
                checkStatuTemp = "";
                for (int j = 0; j < checkInfoList.size(); j++) {
                    if (checkInfoList.get(j).getPartNum().equals(partNum)) {
                        switch (checkInfoList.get(j).getCheckStatu()) {
                            case 1:
                                checkStatuTemp = "Ok";
                                break;
                            case 2:
                                checkStatuTemp = "NO";
                                break;
                            default:
                                checkStatuTemp = "";
                                break;
                        }

                        switch (checkInfoList.get(j).getCheckType()) {
                            case 1:
                                if (checkStatuTemp.equals("OK")) {
                                    spotCheckOkNumTemp++;
                                } else if (checkStatuTemp.equals("NO")) {
                                    spotCheckNoNumTemp++;
                                }
                                spotCheckNumTemp = spotCheckOkNumTemp + spotCheckNoNumTemp;
                                productInfo.setSpotCheckNum(spotCheckNumTemp);
                                productInfo.setSpotCheckOkNum(spotCheckOkNumTemp);
                                productInfo.setSpotCheckNoNum("N" + spotCheckNoNumTemp);
                                break;
                            case 2:
                                productInfo.setFirstCheck(checkStatuTemp);
                                break;
                            case 3:
                                productInfo.setMiddleCheck(checkStatuTemp);
                                break;
                            case 4:
                                productInfo.setLastCheck(checkStatuTemp);
                                break;
                            default:
                                break;
                        }
                        productInfo.setCheckNote(checkInfoList.get(j).getCheckNote());
                        productInfo.setPartNum(partNum);
                    }
                }
                productInfoTempList.add(productInfo);
            }
        }

        Calendar calendar = Calendar.getInstance();
        checkInfoReturn.setCheckDate((calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日");

        checkInfoReturn.setProductInfoList(productInfoTempList);

    }

    @Test
    void selectDate() {
        File file = new File("src/picture");
        File[] files = file.listFiles();
        if (files!=null){
            for (File f : files) {
                if (f.isFile()){
                    f.delete();
                }
            }
        }

        System.out.println("文件删除成功");
    }
}
