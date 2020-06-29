package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditInfo;
import com.faraway.auditall.entity.AuditItem;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.mapper.AuditItemMapper;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuditInfoServiceImpTest {

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Autowired
    private AuditItemMapper auditItemMapper;


    @Test
    void generateExcel() throws IOException {

        String FONTNAME = "黑体";
        Short titleFontSize = 20;
        Short contentFontSize = 11;
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setUserName("fan").setAuditNum(7);



        QueryWrapper<AuditInfo> queryWrapperAuditInfo = new QueryWrapper<>();
        queryWrapperAuditInfo.eq("userName",auditInfo.getUserName()).orderBy(true,true,"auditPage");
        List<AuditInfo> auditInfoList = auditInfoMapper.selectList(queryWrapperAuditInfo);


        QueryWrapper<AuditItem> queryWrapperAuditItem = new QueryWrapper<>();
        queryWrapperAuditItem.eq("auditNum",auditInfo.getAuditNum()).orderBy(true,true,"id");
        List<AuditItem> auditItemList = auditItemMapper.selectList(queryWrapperAuditItem);



        //EXCEL存放路径
        String PATH = "src/picture/";

        //获取当前时间，用于文件命名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());

        SimpleDateFormat df1= new SimpleDateFormat("yyyy-MM-dd");
        String shortDate = df1.format(new Date());

        //创建工作簿
        Workbook workbook = new SXSSFWorkbook();

        //创建工作表
        Sheet sheet = workbook.createSheet();

        //设置第4-5列列宽
        sheet.setColumnWidth(3,256*20);
        sheet.setColumnWidth(5,256*20);

        //设置标题格式
        CellStyle cellStyleTitle = workbook.createCellStyle();
        //标题居中
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        //标题背景色
        cellStyleTitle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        cellStyleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //设置表头格式
        CellStyle cellStyleTitle1 = workbook.createCellStyle();
        //表头居中
        cellStyleTitle1.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle1.setVerticalAlignment(VerticalAlignment.CENTER);
        //表头背景色
        cellStyleTitle1.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        cellStyleTitle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //设置正文格式
        CellStyle cellStyleContent = workbook.createCellStyle();
        //正文居中
        cellStyleContent.setAlignment(HorizontalAlignment.CENTER);
        cellStyleContent.setVerticalAlignment(VerticalAlignment.CENTER);

        //自动换行
        cellStyleContent.setWrapText(true);
        //正文背景色
        cellStyleContent.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyleContent.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        //设置标题字体
        Font fontTile = workbook.createFont();
        fontTile.setFontName(FONTNAME);//字体
        fontTile.setFontHeightInPoints(titleFontSize);//字体大小
        cellStyleTitle.setFont(fontTile);

        //设置表头字体
        Font fontTile1 = workbook.createFont();
        fontTile1.setFontName(FONTNAME);//字体颜
        fontTile1.setFontHeightInPoints(contentFontSize);//字体大小
        cellStyleTitle1.setFont(fontTile1);
        cellStyleContent.setFont(fontTile1);



        //EXCEL标题部分
        Row rowTitle = sheet.createRow(1);
        Cell cellTitle =rowTitle.createCell(0);
        cellTitle.setCellValue("分层审核");
        //合并单元格
        CellRangeAddress region = new CellRangeAddress(1,1,0,4);
        sheet.addMergedRegion(region);
        cellTitle.setCellStyle(cellStyleTitle);


        //EXCEL表头部分,第4行
        Row rowTitle3 = sheet.createRow(3);
        String[] tempTitleList3 = {"审核人","审核时间"};
        String[] tempContentList3 = {auditInfo.getUserName(),shortDate};
        for (int i = 0; i < tempTitleList3.length; i++) {
            Cell cellTitle3 = rowTitle3.createCell(i*3);
            cellTitle3.setCellValue(tempTitleList3[i]);
            cellTitle3.setCellStyle(cellStyleTitle1);

            Cell cellContent3 = rowTitle3.createCell(i*3+1);
            cellContent3.setCellValue(tempContentList3[i]);
            cellContent3.setCellStyle(cellStyleContent);
            sheet.setColumnWidth(i*3+1,256*15);//设置列宽
        }

        //EXCEL表头部分,第5行
        Row rowTitle4 = sheet.createRow(4);
        String[] tempTitleList4 = {"审核对象","审核层级"};
        String[] tempContentList4 = {"三班","班组级"};
        for (int i = 0; i < tempTitleList4.length; i++) {
            Cell cellTitle4 = rowTitle4.createCell(i*3);
            cellTitle4.setCellValue(tempTitleList4[i]);
            cellTitle4.setCellStyle(cellStyleTitle1);

            Cell cellContent4 = rowTitle4.createCell(i*3+1);
            cellContent4.setCellValue(tempContentList4[i]);
            cellContent4.setCellStyle(cellStyleContent);
        }

        //EXCEL表头部分,第6行
        Row rowTitle5 = sheet.createRow(5);
        Cell cellTitle5 = rowTitle5.createCell(0);
        cellTitle5.setCellValue("符合率");
        cellTitle5.setCellStyle(cellStyleTitle1);
        Cell cellContent5 = rowTitle5.createCell(1);
        cellContent5.setCellValue("30%");
        cellContent5.setCellStyle(cellStyleContent);


        //EXCEL表头部分,第7行
        Row rowTitle6 = sheet.createRow(7);
        String[] tempTitleList6 = {"序号","审核项目","审核结论","说明","审核图片"};
        for (int i = 0; i < tempTitleList6.length; i++) {
            Cell cellTitle6 = rowTitle6.createCell(i);
            cellTitle6.setCellValue(tempTitleList6[i]);
            cellTitle6.setCellStyle(cellStyleTitle1);
        }




        //设置数据表格式

        //读取数据库数据










        //写入数据至Excel
        for (int i = 0; i < auditInfoList.size(); i++) {

            String auditConformTemp;
            if (auditInfoList.get(i).getAuditCon()==1){
                auditConformTemp = "符合";
            }else if(auditInfoList.get(i).getAuditCon()==2){
                auditConformTemp="不符合";
            }else{
                auditConformTemp = "";
            }

            //创建数组，存放每行信息，方便写入
            String[] tempList = {
                    auditInfoList.get(i).getAuditPage().toString(),
                    auditItemList.get(i).getAuditItem(),
                    auditConformTemp,
                    auditInfoList.get(i).getAuditFind()
            };

            //创建行
            Row row = sheet.createRow(i+8);
            row.setHeight((short) (20*60));
            for (int j = 0; j < tempList.length; j++) {
                //根据行，创建单元格
                Cell cell = row.createCell(j);
                cell.setCellValue(tempList[j]);
                cell.setCellStyle(cellStyleContent);
            }
        }
        System.out.println("excel写完了");

        //数据写入excel
        FileOutputStream fos = new FileOutputStream(PATH +date +"audit.xlsx");
        workbook.write(fos);
        fos.close();

        //清除临时文件
        ((SXSSFWorkbook) workbook).dispose();

    }
}


