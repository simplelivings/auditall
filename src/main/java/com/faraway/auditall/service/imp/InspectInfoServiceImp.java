package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.*;
import com.faraway.auditall.mapper.BasicInfoMapper;
import com.faraway.auditall.mapper.InspectInfoMapper;
import com.faraway.auditall.mapper.InspectPhotoMapper;
import com.faraway.auditall.utils.BufferedImageBuilder;
import com.faraway.auditall.service.InspectInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * TODO
 *
 * @version: 1.0
 * @author: faraway
 * @date: 2020-11-21 11:35
 */

@Service
@Slf4j
public class InspectInfoServiceImp implements InspectInfoService {

    @Autowired
    private InspectInfoMapper inspectInfoMapper;

    @Autowired
    private InspectPhotoMapper inspectPhotoMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private AuditNameServiceImp auditNameServiceImp;

    @Autowired
    private RegisterInfoServiceImp registerInfoServiceImp;

    @Autowired
    private BasicInfoMapper basicInfoMapper;

    //邮件发送者地址，在配置文件中引用
    @Value("${selfDefination.emailSender}")
    private String sender;

    /**
     *
     * @param inspectInfo
     * @return
     * 插入或更新检查信息
     * 依据用户名和页面，2个关键字，插入检查信息
     */
    @Override
    public int insertOrUpdateInspectInfo(InspectInfo inspectInfo) {

        if (inspectInfo != null) {
            QueryWrapper<InspectInfo> queryWrapper = new QueryWrapper();
            queryWrapper.eq("userName", inspectInfo.getUserName()).eq("auditPage", inspectInfo.getAuditPage());
            InspectInfo inspectInfoTemp = inspectInfoMapper.selectOne(queryWrapper);
            if (inspectInfoTemp != null) {
                inspectInfoMapper.update(inspectInfo, queryWrapper);
                return 1;
            } else {
                inspectInfoMapper.insert(inspectInfo);
                return 2;
            }
        }else {
            return 0;
        }
    }

    /**
     * 删除所有审核信息
     */
    @Override
    public void deleteAllInspectInfo() {
        inspectInfoMapper.delete(null);
    }

    /**
     * 生成excel表
     * @param inspectPhoto
     * @throws IOException
     * @throws InterruptedException
     * @throws MessagingException
     */
    @Override
    public void generateExcel(InspectPhoto inspectPhoto) throws IOException, InterruptedException, MessagingException {
        String PATH = "src/picture/";  //EXCEL存放路径
        String FONTNAME = "楷体";
        String familyName = "";
        String userName = "";
        Short titleFontSize = 20;//excle标题字号大小
        Short contentFontSize = 11;//excel内容字号大小
        String picDate = "";

        //延迟5秒后，再写数据至excel
        Thread.sleep(5000);

        File file0 = new File(PATH);
        if (!file0.exists()) {
            file0.mkdirs();
        }



        //获取当前时间，用于文件命名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String shortDate = df1.format(new Date());


        //寻找图片用信息
        if (inspectPhoto.getUserName() != null) {
            userName = inspectPhoto.getUserName();//用户名
        }
        if (registerInfoServiceImp.findOneRegisterByName(userName) != null) {
            familyName = registerInfoServiceImp.findOneRegisterByName(userName).getFamilyName();//用户名
        }
        int pageNum = 1;//页码编号
        int photoNum = 0;//图片编号：0和1

        String excelPath = PATH + familyName + date + "audit.xlsx";

        long beginData = System.currentTimeMillis();

        //依据用户名，从数据库获得基本信息
        QueryWrapper<BasicInfo> queryWrapperBasicInfo = new QueryWrapper<>();
        queryWrapperBasicInfo.eq("userName", inspectPhoto.getUserName());
        BasicInfo basicInfo = new BasicInfo();
        if (basicInfoMapper.selectOne(queryWrapperBasicInfo) != null) {
            basicInfo = basicInfoMapper.selectOne(queryWrapperBasicInfo);
        }


        //依据用户名，从数据库获得审核信息，并按审核页码排序
        QueryWrapper<InspectInfo> queryWrapperAuditInfo = new QueryWrapper<>();
        queryWrapperAuditInfo.eq("userName", inspectPhoto.getUserName()).orderBy(true, true, "auditPage");
        List<InspectInfo> auditInfoList = new ArrayList<>();
        if (inspectInfoMapper.selectList(queryWrapperAuditInfo) != null) {
            auditInfoList = inspectInfoMapper.selectList(queryWrapperAuditInfo);
        }


        //依据用户名，从数据库获得审核图片的编号列表
        QueryWrapper<InspectPhoto> queryWrapperAuditPhoto = new QueryWrapper<>();
        queryWrapperAuditPhoto.eq("userName", inspectPhoto.getUserName()).orderBy(true, true, "auditPage");
        List<InspectPhoto> auditPhotoList = new ArrayList<>();
        if (inspectPhotoMapper.selectList(queryWrapperAuditPhoto) != null) {
            auditPhotoList = inspectPhotoMapper.selectList(queryWrapperAuditPhoto);
        }


        long endData = System.currentTimeMillis();
        System.out.println("=========data=time=======" + (double) (endData - beginData) / 1000);


        long beginExcel = System.currentTimeMillis();

        //创建工作簿
        Workbook workbook = new SXSSFWorkbook();

        //创建工作表
        Sheet sheet = workbook.createSheet();

        //设置列宽
        sheet.setColumnWidth(0, 256 * 9);
        sheet.setColumnWidth(3, 256 * 10);
        sheet.setColumnWidth(4, 256 * 25);
        sheet.setColumnWidth(5, 256 * 10);
        sheet.setColumnWidth(6, 256 * 15);
        sheet.setColumnWidth(7, 256 * 15);//设置列宽

        //设置标题格式
        CellStyle cellStyleTitle = workbook.createCellStyle();
        //标题居中
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        //标题背景色
        cellStyleTitle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyleTitle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //设置表头格式
        CellStyle cellStyleTitle1 = workbook.createCellStyle();
        //表头居中
        cellStyleTitle1.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle1.setVerticalAlignment(VerticalAlignment.CENTER);
        //表头背景色
        cellStyleTitle1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyleTitle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        //设置中间表头格式，第4行
        //设置正文格式
        CellStyle cellStyleTitle2 = workbook.createCellStyle();
        //正文居中
        cellStyleTitle2.setAlignment(HorizontalAlignment.CENTER);
        cellStyleTitle2.setVerticalAlignment(VerticalAlignment.CENTER);
        //自动换行
        cellStyleTitle2.setWrapText(true);
        //正文背景色
        cellStyleTitle2.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyleTitle2.setFillPattern(FillPatternType.SOLID_FOREGROUND);

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
        //正文边框
        cellStyleContent.setBorderRight(BorderStyle.THIN);
        cellStyleContent.setBorderLeft(BorderStyle.THIN);
        cellStyleContent.setBorderTop(BorderStyle.THIN);
        cellStyleContent.setBorderBottom(BorderStyle.THIN);
        cellStyleContent.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyleContent.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyleContent.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyleContent.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());


        //设置标题字体
        Font fontTile = workbook.createFont();
        fontTile.setFontName(FONTNAME);//字体
        fontTile.setFontHeightInPoints(titleFontSize);//字体大小
        fontTile.setBold(true);
        cellStyleTitle.setFont(fontTile);

        //设置表头字体
        Font fontTile1 = workbook.createFont();
        fontTile1.setFontName(FONTNAME);//字体
        fontTile1.setFontHeightInPoints(contentFontSize);//字体大小
        cellStyleTitle1.setFont(fontTile1);
        cellStyleContent.setFont(fontTile1);

        //设置空白部分格式
        CellStyle cellStyleWhite = workbook.createCellStyle();
        cellStyleWhite.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyleWhite.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        //1设置空白部分为白色背景,共8处；
        Row row0 = sheet.createRow(0);
        for (int i = 0; i < 50; i++) {
            Cell cell = row0.createCell(i);
            cell.setCellStyle(cellStyleWhite);
        }

        //2设置空白部分为白色背景
        for (int i = 6; i < 66; i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < 50; j++) {
                Cell cell = row.createCell(j);
                cell.setCellStyle(cellStyleWhite);
            }
        }

        //EXCEL标题部分
        Row rowTitle = sheet.createRow(1);
        Cell cellTitle = rowTitle.createCell(0);
        cellTitle.setCellValue("检查问题");

        //合并单元格
        CellRangeAddress region1 = new CellRangeAddress(1, 1, 0, 7);
        sheet.addMergedRegion(region1);
        CellRangeAddress region2 = new CellRangeAddress(5, 5, 6, 7);
        sheet.addMergedRegion(region2);
        cellTitle.setCellStyle(cellStyleTitle);

        //3设置空白部分为白色背景
        for (int i = 6; i < 50; i++) {
            Cell cell = rowTitle.createCell(i);
            cell.setCellStyle(cellStyleWhite);
        }

        //4设置空白部分为白色背景
        Row row2 = sheet.createRow(2);
        for (int i = 0; i < 50; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellStyle(cellStyleWhite);
        }


        //EXCEL表头部分,第4行
        Row rowTitle3 = sheet.createRow(3);
        rowTitle3.setHeight((short) (20 * 20));
        String[] tempTitleList3 = {"检查人", "检查时间"};
        String[] tempContentList3 = {familyName, shortDate};
        for (int i = 0; i < tempTitleList3.length; i++) {
            Cell cellTitle3 = rowTitle3.createCell(i * 5);
            cellTitle3.setCellValue(tempTitleList3[i]);
            cellTitle3.setCellStyle(cellStyleTitle1);

            Cell cellContent3 = rowTitle3.createCell(i * 5 + 1);
            cellContent3.setCellValue(tempContentList3[i]);
            cellContent3.setCellStyle(cellStyleTitle2);
            sheet.setColumnWidth(i * 3 + 1, 256 * 15);//设置列宽
        }
        //5设置空白部分为白色背景
        for (int i = 7; i < 50; i++) {
            Cell cell = rowTitle3.createCell(i);
            cell.setCellStyle(cellStyleWhite);
        }
        for (int i = 2; i < 5; i++) {
            Cell cell = rowTitle3.createCell(i);
            cell.setCellStyle(cellStyleWhite);
        }

        //6设置空白部分为白色背景
        Row row4 = sheet.createRow(4);
        for (int i = 0; i < 50; i++) {
            Cell cell = row4.createCell(i);
            cell.setCellStyle(cellStyleWhite);
        }

        //EXCEL表头部分,第6行
        Row rowTitle6 = sheet.createRow(5);
        rowTitle6.setHeight((short) (20 * 20));

        String[] tempTitleList6 = {"序号", "检查时间", "检查人", "问题属性", "发现问题", "责任单位"};
        for (int i = 0; i < tempTitleList6.length; i++) {
            Cell cellTitle6 = rowTitle6.createCell(i);
            cellTitle6.setCellValue(tempTitleList6[i]);
            cellTitle6.setCellStyle(cellStyleTitle1);
        }
        Cell cellTitle61 = rowTitle6.createCell(6);
        cellTitle61.setCellValue("检查图片");
        cellTitle61.setCellStyle(cellStyleTitle1);
        //7设置空白部分为白色背景
        for (int i = 7; i < 50; i++) {
            Cell cell = rowTitle6.createCell(i);
            cell.setCellStyle(cellStyleWhite);
        }


        //按行写入数据至Excel，每行先写入文字信息，然后写入图片
        if (auditInfoList != null && auditInfoList.size() > 0) {

            for (int i = 0; i < auditInfoList.size(); i++) {

                //处理问题性质，1-批评 2-表扬 3-展示
                String auditConformTemp = "";
                if (auditInfoList.get(i).getAuditCon() != null) {
                    if (auditInfoList.get(i).getAuditCon() == 1) {
                        auditConformTemp = "批评";
                    } else if (auditInfoList.get(i).getAuditCon() == 2) {
                        auditConformTemp = "表扬";
                    } else if (auditInfoList.get(i).getAuditCon() == 3) {
                        auditConformTemp = "展示";
                    } else {
                        auditConformTemp = "";
                    }
                }

                //创建行
                Row row = sheet.createRow(i + 6);
                row.setHeight((short) (20 * 60));


                //创建数组，存放每行信息，方便写入
                String[] tempList = {
                        auditInfoList.get(i).getAuditPage().toString(),
                        shortDate,
                        familyName,
                        auditConformTemp,
                        auditInfoList.get(i).getAuditFind(),
                        auditInfoList.get(i).getAuditCharger()
                };

                //文字信息写入excel
                for (int j = 0; j < tempList.length; j++) {
                    //根据行，创建单元格
                    Cell cell = row.createCell(j);
                    cell.setCellValue(tempList[j]);
                    cell.setCellStyle(cellStyleContent);
                }

                //图片信息写入excel
                if (auditPhotoList != null && auditPhotoList.size() > 0) {

                    for (int j = 0; j < auditPhotoList.size(); j++) {

                        if (auditPhotoList.get(j).getUpdateTime() != null) {
                            picDate = df.format(auditPhotoList.get(j).getUpdateTime());
                        }

                        if ((auditPhotoList.get(j).getAuditPage() == (i + 1))) {

                            if (auditPhotoList.get(j).getPhotoNumber() == 0) {
                                String pathPictrue = "src/picture/" + picDate + "name" + userName + "page" + (i + 1) + "num" + 0 + ".jpg";
                                File file = new File(pathPictrue);
                                if (file.exists()) {
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    Image src = Toolkit.getDefaultToolkit().getImage(file.getPath());
                                    BufferedImage bufferedImage = BufferedImageBuilder.toBufferedImage(src);
                                    ImageIO.write(bufferedImage, "jpg", bos);
                                    Drawing patriarch = sheet.createDrawingPatriarch();
                                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1024, 255, 6, i + 6, 7, i + 7);
                                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                                    patriarch.createPicture(anchor, workbook.addPicture(bos.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
                                    if (bos != null) {
                                        bos.close();
                                    }
                                }
                            } else if (auditPhotoList.get(j).getPhotoNumber() == 1) {
                                String pathPictrue = "src/picture/" + picDate + "name" + userName + "page" + (i + 1) + "num" + 1 + ".jpg";
                                File file = new File(pathPictrue);
                                if (file.exists()) {
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    Image src = Toolkit.getDefaultToolkit().getImage(file.getPath());
                                    BufferedImage bufferedImage = BufferedImageBuilder.toBufferedImage(src);
                                    ImageIO.write(bufferedImage, "jpg", bos);
                                    Drawing patriarch = sheet.createDrawingPatriarch();

                                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1024, 255, 7, i + 6, 8, i + 7);
                                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                                    patriarch.createPicture(anchor, workbook.addPicture(bos.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
                                    if (bos != null) {
                                        bos.close();
                                    }
                                }
                            }
                        }
                    }

                }

                //8设置空白部分为白色背景
                for (int k = 7; k < 50; k++) {
                    Cell cell = row.createCell(k);
                    cell.setCellStyle(cellStyleWhite);
                }

            }

            log.info("===检查信息excel写入完成===");

            //数据写入excel
            FileOutputStream fos = new FileOutputStream(excelPath);
            workbook.write(fos);
            if (fos != null) {
                fos.flush();
                fos.getFD().sync();
                fos.close();
            }

            //清除临时文件
            ((SXSSFWorkbook) workbook).dispose();

            long endExcel = System.currentTimeMillis();
            //发送邮件
            emailFile("检查问题" + shortDate + familyName, excelPath, inspectPhoto.getUserName(), inspectPhoto.getLoginNum());

            System.out.println("=========excel=time=======" + (double) (endExcel - beginExcel) / 1000);

            // 延时5s后，删除所有文件
            Thread.sleep(5000);
            if (auditPhotoList != null && auditPhotoList.size() > 0) {
                for (int i = 0; i < auditPhotoList.size(); i++) {
                    if (auditPhotoList.get(i).getUpdateTime() != null) {
                        picDate = df.format(auditPhotoList.get(i).getUpdateTime());
                    }
                    for (int j = 0; j < 2; j++) {
                        String fileName = PATH + picDate + "name" + userName + "page" + i + "num" + j + ".jpg";
                        File file = new File(fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }

            File file = new File(excelPath);
            if (file.exists()) {
                file.delete();
            }

            basicInfoMapper.delete(queryWrapperBasicInfo);
            inspectInfoMapper.delete(queryWrapperAuditInfo);
            inspectPhotoMapper.delete(queryWrapperAuditPhoto);

            log.info("===检查信息 删除完成===");

        }
    }

    //发送邮件的方法
    public void emailFile(String fileName, String path, String userName, int loginNum) throws MessagingException, UnsupportedEncodingException {

        //1、创建一个复杂的邮件
        System.setProperty("mail.mime.charset", "UTF-8");
        System.setProperty("mail.mime.splitlongparameters", "false");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //邮件主题
        helper.setSubject(fileName);


        //邮件接收人的临时list集合
        List<String> tempReceiverList = new ArrayList<>();

        //邮件接收人，添加嘉翔接收人列表
        if (loginNum == 1) {
            List<AuditName> auditNameList = auditNameServiceImp.findAllSenderAndReceiver();
            for (int i = 0; i < auditNameList.size(); i++) {
                if (auditNameList.get(i).getReceiver() != null && auditNameList.get(i).getReceiver().length() > 0) {
                    tempReceiverList.add(auditNameList.get(i).getReceiver());
                }
            }
        }

        //邮件接收人，添加用户邮箱和主送邮箱
        String tempReciever = null;
        String tempRecEmail = null;
        if (registerInfoServiceImp.findOneRegisterByName(userName) != null) {
            RegisterInfo registerInfo = registerInfoServiceImp.findOneRegisterByName(userName);
            if (registerInfo.getUserRight() > 0) {
                tempReciever = registerInfo.getEmailAddress();
                tempRecEmail = registerInfo.getRecEmail();
            }

        }
        //邮件接收人，添加用户邮箱
        if (tempReciever != null && tempReciever.length() > 0) {
            tempReceiverList.add(tempReciever);
        }
        //邮件接收人，添加主送邮箱
        if (tempRecEmail != null && tempRecEmail.length() > 0) {
            tempReceiverList.add(tempRecEmail);
        }

        for (String s : tempReceiverList) {
            System.out.println("=======receiverList=======" + s);
        }

        //邮件接收人的数组
        if (tempReceiverList!=null && tempReceiverList.size()>0){

            String[] receiverList = tempReceiverList.toArray(new String[0]);
            System.out.println("receiverList=========" + Arrays.toString(receiverList));
            String nick = "";
            nick = javax.mail.internet.MimeUtility.encodeText("表格花");
            //邮件内容
            helper.setText("检查内容见附件，请查收！", true);
            if (receiverList != null) {
                helper.setTo(receiverList);
            }
            if (sender != null) {
                helper.setFrom(nick + " <"+sender+">");
            }

            File file = new File(path);
            if (file.exists()) {
                //附件添加word文档
                helper.addAttachment(MimeUtility.encodeText(fileName) + ".xlsx", file);
            }
            javaMailSender.send(mimeMessage);

            log.info("===检查信息 邮件发送完成===");
            System.out.println("===检查信息 邮件发送完成===");
        }
    }
}
