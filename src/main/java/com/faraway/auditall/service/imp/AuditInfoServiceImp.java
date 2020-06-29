package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditNum;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.service.AuditInfoService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class AuditInfoServiceImp implements AuditInfoService {

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Override
    public AuditNum insertOrUpdateAuditInfo(AuditInfo auditInfo) {

        //返回客户端的页码对象
        AuditNum auditNum = new AuditNum();
        int conformNum=0,unconformNum=0,finishNum=0;

        if (auditInfo!=null){
            QueryWrapper<AuditInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("auditPage", auditInfo.getAuditPage()).eq("userName",auditInfo.getUserName());
            AuditInfo auditInfoExist = auditInfoMapper.selectOne(queryWrapper);

            QueryWrapper<AuditInfo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("userName",auditInfo.getUserName());
            List<AuditInfo> auditInfoList = auditInfoMapper.selectList(queryWrapper1);
            for (int i = 0; i < auditInfoList.size(); i++) {
                if (auditInfoList.get(i).getAuditCon()<2){
                    conformNum += auditInfoList.get(i).getAuditCon();
                }
            }
            auditNum.setConformNum(conformNum)
                    .setFinishNum(auditInfoList.size())
                    .setUnconformNum(auditInfoList.size()-conformNum);

            if (auditInfoExist != null) {
                auditInfoMapper.update(auditInfo, queryWrapper);
                System.out.println("==AuditInfoServiceImp==已存在，并更新成功=====");
                return auditNum;
            } else {
                auditInfoMapper.insert(auditInfo);
                System.out.println("==AuditInfoServiceImp==插入成功=====");
                return auditNum;
            }
        }else{
            return auditNum;
        }
    }

    @Override
    public int generateExcel(AuditInfo auditInfo) throws IOException {

        //EXCEL存放路径
        String PATH = "src/picture/";

        if (auditInfo!=null){
            //创建工作簿
            Workbook workbook = new SXSSFWorkbook();

            //创建工作表
            Sheet sheet = workbook.createSheet();

            //设置数据表格式

            //读取数据库数据

            //写入数据至Excel
            for (int i = 0; i < 500; i++) {
                //创建行
                Row row = sheet.createRow(i);
                for (int j = 0; j <10 ; j++) {
                    //根据行，创建单元格
                    Cell cell = row.createCell(j);
                    cell.setCellValue(j);
                }
            }
            System.out.println("excel写完了");

            //数据写入excel
            FileOutputStream fos = new FileOutputStream(PATH+auditInfo.getUpdateTime()+auditInfo.getUserName()+"audit.xlsx");
            workbook.write(fos);
            fos.close();

            //清除临时文件
            ((SXSSFWorkbook)workbook).dispose();

        }
        return 0;
    }

}
