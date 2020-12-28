package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.faraway.auditall.entity.*;
import com.faraway.auditall.mapper.AuditInfoMapper;
import com.faraway.auditall.mapper.AuditItemMapper;
import com.faraway.auditall.mapper.AuditPhotoMapper;
import com.faraway.auditall.mapper.BasicInfoMapper;
import com.faraway.auditall.service.AuditInfoService;
import com.faraway.auditall.service.AuditNameService;
import com.faraway.auditall.utils.BufferedImageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class AuditInfoServiceImp implements AuditInfoService {

    @Autowired
    private BasicInfoMapper basicInfoMapper;

    @Autowired
    private AuditInfoMapper auditInfoMapper;

    @Autowired
    private AuditItemMapper auditItemMapper;

    @Autowired
    private AuditPhotoMapper auditPhotoMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private AuditNameServiceImp auditNameServiceImp;

    @Autowired
    private RegisterInfoServiceImp registerInfoServiceImp;

    @Override
    public AuditNum getAuditNum(String userName) {
        //返回客户端的页码对象
        AuditNum auditNum = new AuditNum();
        int conformNum = 0, unconformNum = 0, finishNum = 0;
        if (userName != null) {
            QueryWrapper<AuditInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userName", userName);
            List<AuditInfo> auditInfoList = new ArrayList<>();
            if (auditInfoMapper.selectList(queryWrapper) != null) {
                auditInfoList = auditInfoMapper.selectList(queryWrapper);
            }

            //处理符合与不符合数量
            if (auditInfoList != null && auditInfoList.size() > 0) {
                for (int i = 0; i < auditInfoList.size(); i++) {
                    if (auditInfoList.get(i).getAuditCon() < 2) {
                        conformNum += auditInfoList.get(i).getAuditCon();
                    }
                }
            }
            auditNum.setConformNum(conformNum)
                    .setFinishNum(auditInfoList.size())
                    .setUnconformNum(auditInfoList.size() - conformNum);

        }
        return auditNum;
    }

    @Override
    public AuditNum insertOrUpdateAuditInfo(AuditInfo auditInfo) {

        //返回客户端的页码对象
        AuditNum auditNum = new AuditNum();
        int conformNum = 0, unconformNum = 0, finishNum = 0;

        if (auditInfo != null) {
            QueryWrapper<AuditInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("auditPage", auditInfo.getAuditPage()).eq("userName", auditInfo.getUserName());
            AuditInfo auditInfoExist = null;
            if (auditInfoMapper.selectOne(queryWrapper) != null) {
                auditInfoExist = auditInfoMapper.selectOne(queryWrapper);
            }

            QueryWrapper<AuditInfo> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("userName", auditInfo.getUserName());

            if (auditInfoExist != null) {
                auditInfoMapper.update(auditInfo, queryWrapper);
                System.out.println("==AuditInfoServiceImp==已存在，并更新成功=====");

            } else {
                auditInfoMapper.insert(auditInfo);
                System.out.println("==AuditInfoServiceImp==插入成功=====");
            }

            List<AuditInfo> auditInfoList = new ArrayList<>();
            if (auditInfoMapper.selectList(queryWrapper1) != null) {
                auditInfoList = auditInfoMapper.selectList(queryWrapper1);
            }

            //处理符合与不符合数量
            if (auditInfoList != null && auditInfoList.size() > 0) {
                for (int i = 0; i < auditInfoList.size(); i++) {
                    if (auditInfoList.get(i).getAuditCon() < 2) {
                        conformNum += auditInfoList.get(i).getAuditCon();
                    }
                }
            }
            auditNum.setConformNum(conformNum)
                    .setFinishNum(auditInfoList.size())
                    .setUnconformNum(auditInfoList.size() - conformNum);

            return auditNum;
        } else {
            log.error("===插入分层审核信息失败===");
            return auditNum;
        }
    }

    @Override
    public void generateExcel(AuditPhoto auditPhoto) throws IOException, MessagingException, InterruptedException {

        String PATH = "src/picture/";  //EXCEL存放路径
        String FONTNAME = "黑体";
        String familyName = "";
        String picDate = "";

        File file0 = new File(PATH);
        if (!file0.exists()) {
            file0.mkdirs();
        }

        Short titleFontSize = 20;//excle标题字号大小
        Short contentFontSize = 11;//excel内容字号大小

        //获取当前时间，用于文件命名
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());

        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
        String shortDate = df1.format(new Date());


        //寻找图片用信息
        String userName = "";
        if (auditPhoto.getUserName() != null) {
            userName = auditPhoto.getUserName();//用户名
        }
        if (registerInfoServiceImp.findOneRegisterByName(userName) != null) {
            familyName = registerInfoServiceImp.findOneRegisterByName(userName).getFamilyName();//用户名
        }
        int pageNum = 1;//页码编号
        int photoNum = 0;//图片编号：0和1

        String excelPath = PATH + familyName + date + "audit.xlsx";

        String conformRatio = "";//符合率
        double conformNum = 0;//符合的数量
        double unconformNum = 0;//不符合的数量

        long beginData = System.currentTimeMillis();


        //依据用户名，从数据库获得基本信息
        QueryWrapper<BasicInfo> queryWrapperBasicInfo = new QueryWrapper<>();
        queryWrapperBasicInfo.eq("userName", auditPhoto.getUserName());
        BasicInfo basicInfo = new BasicInfo();
        if (basicInfoMapper.selectOne(queryWrapperBasicInfo) != null) {
            basicInfo = basicInfoMapper.selectOne(queryWrapperBasicInfo);
        }

        //依据审核编号，从数据库获得审核项目，并按主键排序
        QueryWrapper<AuditItem> queryWrapperAuditItem = new QueryWrapper<>();
        queryWrapperAuditItem.eq("auditNum", auditPhoto.getAuditNum()).orderBy(true, true, "id");
        List<AuditItem> auditItemList = new ArrayList<>();
        if (auditItemMapper.selectList(queryWrapperAuditItem) != null) {
            auditItemList = auditItemMapper.selectList(queryWrapperAuditItem);
        }

        //依据用户名，从数据库获得审核信息，并按审核页码排序
        QueryWrapper<AuditInfo> queryWrapperAuditInfo = new QueryWrapper<>();
        queryWrapperAuditInfo.eq("userName", auditPhoto.getUserName()).orderBy(true, true, "auditPage");
        List<AuditInfo> auditInfoList = new ArrayList<>();
        if (auditInfoMapper.selectList(queryWrapperAuditInfo) != null) {
            auditInfoList = auditInfoMapper.selectList(queryWrapperAuditInfo);
        }
        //计算审核符合率
        if (auditInfoList != null && auditInfoList.size() > 0) {
            for (int i = 0; i < auditInfoList.size(); i++) {
                if (auditInfoList.get(i).getAuditCon() == 1) {
                    conformNum++;
                } else if (auditInfoList.get(i).getAuditCon() >= 2) {
                    unconformNum++;
                }
            }
        }
        if (conformNum > 0 || unconformNum > 0) {
            conformRatio = (int) ((conformNum / (conformNum + unconformNum)) * 100) + "%";
        }


        //依据用户名，从数据库获得审核图片的编号列表
        QueryWrapper<AuditPhoto> queryWrapperAuditPhoto = new QueryWrapper<>();
        queryWrapperAuditPhoto.eq("userName", auditPhoto.getUserName()).orderBy(true, true, "auditPage");
        List<AuditPhoto> auditPhotoList = new ArrayList<>();
        if (auditPhotoMapper.selectList(queryWrapperAuditPhoto) != null) {
            auditPhotoList = auditPhotoMapper.selectList(queryWrapperAuditPhoto);
        }

        long endData = System.currentTimeMillis();
        System.out.println("=========data=time=======" + (double) (endData - beginData) / 1000);


        long beginExcel = System.currentTimeMillis();

        //创建工作簿
        Workbook workbook = new SXSSFWorkbook();

        //创建工作表
        Sheet sheet = workbook.createSheet();

        //设置第4-5列列宽
        sheet.setColumnWidth(3, 256 * 20);
        sheet.setColumnWidth(5, 256 * 20);

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
        fontTile1.setFontName(FONTNAME);//字体颜色
        fontTile1.setFontHeightInPoints(contentFontSize);//字体大小
        cellStyleTitle1.setFont(fontTile1);
        cellStyleContent.setFont(fontTile1);


        //EXCEL标题部分
        Row rowTitle = sheet.createRow(1);
        Cell cellTitle = rowTitle.createCell(0);
        cellTitle.setCellValue("分层审核");
        //合并单元格
        CellRangeAddress region1 = new CellRangeAddress(1, 1, 0, 5);
        sheet.addMergedRegion(region1);
        CellRangeAddress region2 = new CellRangeAddress(7, 7, 4, 5);
        sheet.addMergedRegion(region2);
        cellTitle.setCellStyle(cellStyleTitle);
        sheet.setColumnWidth(5, 256 * 15);//设置列宽
        sheet.setColumnWidth(0, 256 * 9);


        //EXCEL表头部分,第4行
        Row rowTitle3 = sheet.createRow(3);
        String[] tempTitleList3 = {"审核人", "审核时间"};
        String[] tempContentList3 = {familyName, shortDate};
        for (int i = 0; i < tempTitleList3.length; i++) {
            Cell cellTitle3 = rowTitle3.createCell(i * 3);
            cellTitle3.setCellValue(tempTitleList3[i]);
            cellTitle3.setCellStyle(cellStyleTitle1);

            Cell cellContent3 = rowTitle3.createCell(i * 3 + 1);
            cellContent3.setCellValue(tempContentList3[i]);
            cellContent3.setCellStyle(cellStyleContent);
            sheet.setColumnWidth(i * 3 + 1, 256 * 15);//设置列宽
        }

        //EXCEL表头部分,第5行
        Row rowTitle4 = sheet.createRow(4);
        String[] tempTitleList4 = {"审核对象", "审核层级"};
        String[] tempContentList4 = {basicInfo.getAuditObj(), basicInfo.getAuditRen()};
        for (int i = 0; i < tempTitleList4.length; i++) {
            Cell cellTitle4 = rowTitle4.createCell(i * 3);
            cellTitle4.setCellValue(tempTitleList4[i]);
            cellTitle4.setCellStyle(cellStyleTitle1);

            System.out.println("++++++++======tempContentList4=========+++++++++" + i + "===" + tempContentList4[i]);
            Cell cellContent4 = rowTitle4.createCell(i * 3 + 1);
            cellContent4.setCellValue(tempContentList4[i]);
            cellContent4.setCellStyle(cellStyleContent);
        }

        //EXCEL表头部分,第6行
        Row rowTitle5 = sheet.createRow(5);
        Cell cellTitle5 = rowTitle5.createCell(0);
        cellTitle5.setCellValue("符合率");
        cellTitle5.setCellStyle(cellStyleTitle1);
        Cell cellContent5 = rowTitle5.createCell(1);
        cellContent5.setCellValue(conformRatio);
        cellContent5.setCellStyle(cellStyleContent);


        //EXCEL表头部分,第8行
        Row rowTitle6 = sheet.createRow(7);
        String[] tempTitleList6 = {"序号", "审核项目", "审核结论", "说明"};
        for (int i = 0; i < tempTitleList6.length; i++) {
            Cell cellTitle6 = rowTitle6.createCell(i);
            cellTitle6.setCellValue(tempTitleList6[i]);
            cellTitle6.setCellStyle(cellStyleTitle1);
        }
        Cell cellTitle61 = rowTitle6.createCell(4);
        cellTitle61.setCellValue("审核图片");
        cellTitle61.setCellStyle(cellStyleTitle1);

        //写入数据至Excel
        if (auditInfoList != null && auditInfoList.size() > 0) {

            for (int i = 0; i < auditInfoList.size(); i++) {

                String auditConformTemp;
                if (auditInfoList.get(i).getAuditCon() == 1) {
                    auditConformTemp = "Y";
                } else if (auditInfoList.get(i).getAuditCon() == 2) {
                    auditConformTemp = "N";
                } else if (auditInfoList.get(i).getAuditCon() == 3) {
                    auditConformTemp = "NC";
                } else {
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
                Row row = sheet.createRow(i + 8);
                row.setHeight((short) (20 * 60));
                for (int j = 0; j < tempList.length; j++) {
                    //根据行，创建单元格
                    Cell cell = row.createCell(j);
                    cell.setCellValue(tempList[j]);
                    cell.setCellStyle(cellStyleContent);
                }

                if (auditPhotoList != null && auditPhotoList.size() > 0) {
                    for (int j = 0; j < auditPhotoList.size(); j++) {
                        if (auditPhotoList.get(j).getUpdateTime() != null) {
                            picDate = df.format(auditPhotoList.get(j).getUpdateTime());
                        }

                        if (auditPhotoList.get(j).getAuditPage() == (i + 1)) {
                            if (auditPhotoList.get(j).getPhotoNumber() == 0) {
                                String pathPictrue = "src/picture/" + picDate + "name" + userName + "page" + (i + 1) + "num" + 0 + ".jpg";
                                File file = new File(pathPictrue);
                                if (file.exists()) {
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    Image src = Toolkit.getDefaultToolkit().getImage(file.getPath());
//                                BufferedImage  bufferedImage = ImageIO.read(new File(pathPictrue));
                                    BufferedImage bufferedImage = BufferedImageBuilder.toBufferedImage(src);
                                    ImageIO.write(bufferedImage, "jpg", bos);
                                    Drawing patriarch = sheet.createDrawingPatriarch();
                                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1024, 255, 4, i + 8, 5, i + 9);
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

                                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1024, 255, 5, i + 8, 6, i + 9);
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
            }

            log.info("===分层审核信息Excel写入完成===");

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
            emailFile("分层审核" + shortDate + familyName, excelPath, auditPhoto.getUserName());

            System.out.println("=========excel=time=======" + (double) (endExcel - beginExcel) / 1000);


            Thread.sleep(5000);

            // 删除所有文件
            if (auditPhotoList != null && auditPhotoList.size() > 0) {
                for (int i = 0; i < auditPhotoList.size(); i++) {

                    if (auditPhotoList.get(i).getUpdateTime() != null) {
                        picDate = df.format(auditPhotoList.get(i).getUpdateTime());
                    }
                    for (int j = 0; j < 2; j++) {
                        String fileName = PATH + picDate+"name" + userName + "page" + i + "num" + j + ".jpg";
                        File file = new File(fileName);
                        System.out.println("*******fileName***"+fileName+"**i**"+i);
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
            auditInfoMapper.delete(queryWrapperAuditInfo);
            auditPhotoMapper.delete(queryWrapperAuditPhoto);

            log.info("===分层审核信息 删除完成===");

        }

    }

    @Override
    public void deleteAllAuditInfo() {
        auditInfoMapper.delete(null);
    }


    //发送邮件的方法
    public void emailFile(String fileName, String path, String userName) throws MessagingException, UnsupportedEncodingException {

        //1、创建一个复杂的邮件
//        tefileName = new String(fileName.getBytes(),"iso8859-1");
        System.setProperty("mail.mime.charset", "UTF-8");
        System.setProperty("mail.mime.splitlongparameters", "false");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        //邮件主题
        helper.setSubject(fileName);

        String sender = "";

        //邮件接收人的临时list集合
        List<String> tempReceiverList = new ArrayList<>();

        List<AuditName> auditNameList = auditNameServiceImp.findAllSenderAndReceiver();
        for (int i = 0; i < auditNameList.size(); i++) {
            if (auditNameList.get(i).getReceiver() != null && auditNameList.get(i).getReceiver().length() > 0) {
                tempReceiverList.add(auditNameList.get(i).getReceiver());
            }
            if (auditNameList.get(i).getSender() != null) {
                sender = auditNameList.get(i).getSender();
            }
        }

        String tempReciever = null;
        if (registerInfoServiceImp.findOneRegisterByName(userName) != null) {
            if (registerInfoServiceImp.findOneRegisterByName(userName).getUserRight() > 0) {
                tempReciever = registerInfoServiceImp.findOneRegisterByName(userName).getEmailAddress();
            }
        }

        if (tempReciever != null && tempReciever.length() > 0) {
            tempReceiverList.add(tempReciever);
        }

        System.out.println("tempReciever=========" + tempReciever);

        //邮件接收人的数组
        String[] receiverList = tempReceiverList.toArray(new String[0]);
        System.out.println("receiverList=========" + Arrays.toString(receiverList));

        //邮件内容
        helper.setText("分层审核内容见附件，请查收！", true);
        if (receiverList != null) {
            helper.setTo(receiverList);
        }
        if (sender != null) {
            helper.setFrom(sender);
        }

        File file = new File(path);
        if (file.exists()) {
            //附件添加word文档
            helper.addAttachment(MimeUtility.encodeText(fileName) + ".xlsx", file);
        }
        javaMailSender.send(mimeMessage);

        log.info("===分层审核信息 邮件发送完成===");
        System.out.println("===分层审核信息 邮件发送完成===");
    }
}
