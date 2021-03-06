package com.faraway.auditall.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.faraway.auditall.entity.AuditName;
import com.faraway.auditall.entity.CheckInfo;
import com.faraway.auditall.entity.CheckPhoto;
import com.faraway.auditall.entity.RegisterInfo;
import com.faraway.auditall.mapper.CheckInfoMapper;
import com.faraway.auditall.service.AuditNameService;
import com.faraway.auditall.service.CheckInfoService;
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
import org.springframework.scheduling.annotation.Scheduled;
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
public class CheckInfoServiceImp implements CheckInfoService {

    @Autowired
    private CheckInfoMapper checkInfoMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private AuditNameServiceImp auditNameServiceImp;

    @Autowired
    private RegisterInfoServiceImp registerInfoServiceImp;

    @Autowired
    private CheckPhotoServiceImp checkPhotoServiceImp;

    @Override
    public List<CheckInfo> findAllCheckInfo() {
        List<CheckInfo> checkInfoList = checkInfoMapper.selectList(null);
        return checkInfoList;

    }


    @Override
    public void deleteAllCheckInfo() {
        checkInfoMapper.delete(null);
    }

    /*
     * 判断思路：
     * 根据用户名、零件号、检验类型、生产数量、生产时间，来判断数据库中是否有数据；
     * -生产时间，由小时和分钟的十位组成，如此可在短期内更新错误输入状态；
     *  并防止一个产品在不同时间段生产时，将前面数据覆盖；
     * -生产日期，为自动生成，用于插入excel时用；
     * */

    @Override
    public int insertOrUpdateCheckInfo(CheckInfo checkInfo) {
        if (checkInfo != null) {
            checkInfo.setCreateTime(new Date());
            QueryWrapper<CheckInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userName", checkInfo.getUserName())
                    .eq("partNum", checkInfo.getPartNum())
                    .eq("checkType", checkInfo.getCheckType())
                    .eq("productNum", checkInfo.getProductNum())
                    .eq("produceTime", checkInfo.getProduceTime());
            CheckInfo checkInfoExist = null;
            if (checkInfoMapper.selectOne(queryWrapper) != null) {
                checkInfoExist = checkInfoMapper.selectOne(queryWrapper);
            }

            if (checkInfoExist != null) {
                checkInfoMapper.update(checkInfo, queryWrapper);
                return 1;
            } else {
                checkInfoMapper.insert(checkInfo);
                return 2;
            }
        } else {
            log.error("===插入检验信息失败===");
            return 0;
        }
    }

    @Override
    public void gererateExcel() throws IOException, MessagingException, InterruptedException {
        String PATH = "src/picture/";  //EXCEL存放路径
        String FONTNAME = "黑体";

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
        String fileType = "检验";

        List<CheckInfo> checkInfoList = new ArrayList<>();
        List<CheckPhoto> checkPhotoList = new ArrayList<>();

        if (findAllCheckInfo() != null) {
            checkInfoList = findAllCheckInfo();
        }

        if (checkPhotoServiceImp.findAllCheckPhoto() != null) {
            checkPhotoList = checkPhotoServiceImp.findAllCheckPhoto();
        }

        //寻找图片用信息
        int photoNum = 0;//图片编号：0和1

        String excelPath = PATH + fileType + date + "audit.xlsx";

        long beginExcel = System.currentTimeMillis();

        //创建工作簿
        Workbook workbook = new SXSSFWorkbook();

        //创建工作表
        Sheet sheet = workbook.createSheet();

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
        cellTitle.setCellValue("检验记录");
        //合并单元格
        CellRangeAddress region1 = new CellRangeAddress(1, 1, 0, 9);
        sheet.addMergedRegion(region1);
        cellTitle.setCellStyle(cellStyleTitle);

        //EXCEL表头部分,第4行
        Row rowTitle3 = sheet.createRow(3);
        Cell cellTitle3 = rowTitle3.createCell(0);
        cellTitle3.setCellValue("导出日期");
        cellTitle3.setCellStyle(cellStyleTitle1);

        Cell cellContent3 = rowTitle3.createCell(1);
        cellContent3.setCellValue(shortDate);
        cellContent3.setCellStyle(cellStyleContent);


        //EXCEL表头部分,第6行
        Row rowTitle6 = sheet.createRow(5);
        String[] tempTitleList6 = {"序号", "零件号", "检验类型", "生产数量", "检验数量", "检验时间", "检验人", "检验结果", "检验图片", "备注"};
        for (int i = 0; i < tempTitleList6.length; i++) {
            Cell cellTitle6 = rowTitle6.createCell(i);
            cellTitle6.setCellValue(tempTitleList6[i]);
            cellTitle6.setCellStyle(cellStyleTitle1);
        }

        sheet.setColumnWidth(1, 256 * 15);//设置列宽
        sheet.setColumnWidth(8, 256 * 15);

        if (checkInfoList != null && checkInfoList.size() > 0) {

            String checkTypeTemp = null;
            String checkStatuTemp = null;
            String familyName = null;

            for (int i = 0; i < checkInfoList.size(); i++) {

                //处理检验类型与检验状态
                //判断检验类型
                switch (checkInfoList.get(i).getCheckType()) {
                    case 1:
                        checkTypeTemp = "抽检";
                        break;
                    case 2:
                        checkTypeTemp = "首检";
                        break;
                    case 3:
                        checkTypeTemp = "中检";
                        break;
                    case 4:
                        checkTypeTemp = "末检";
                        break;
                    default:
                        checkTypeTemp = "";
                        break;
                }

                //判断检验结果
                switch (checkInfoList.get(i).getCheckStatu()) {
                    case 1:
                        checkStatuTemp = "合格";
                        break;
                    case 2:
                        checkStatuTemp = "不合格";
                        break;
                    default:
                        checkStatuTemp = "";
                        break;
                }

                //获得人员名字
                if (registerInfoServiceImp.findOneRegisterByName(checkInfoList.get(i).getUserName()) != null) {
                    familyName = registerInfoServiceImp.findOneRegisterByName(checkInfoList.get(i).getUserName()).getFamilyName();
                }


                //创建数组，存放每行信息，方便写入
                String[] tempList = {
                        String.valueOf(i + 1),
                        checkInfoList.get(i).getPartNum(),
                        checkTypeTemp,
                        String.valueOf(checkInfoList.get(i).getProductNum()),
                        String.valueOf(checkInfoList.get(i).getCheckNum()),
                        checkInfoList.get(i).getCheckTime(),
                        familyName,
                        checkStatuTemp,
                        "",
                        checkInfoList.get(i).getCheckNote()
                };

                //创建行
                Row row = sheet.createRow(i + 6);
                row.setHeight((short) (20 * 60));
                for (int j = 0; j < tempList.length; j++) {
                    //根据行，创建单元格
                    Cell cell = row.createCell(j);
                    cell.setCellValue(tempList[j]);
                    cell.setCellStyle(cellStyleContent);
                }


                if (checkPhotoList != null && checkPhotoList.size() > 0) {
                    for (int j = 0; j < checkPhotoList.size(); j++) {
                        //依据零件号 生产时间 检验类型来插入图片
                        if ((checkPhotoList.get(j).getPartNum().equals(checkInfoList.get(i).getPartNum()))
                                && (checkPhotoList.get(j).getProduceTime().equals(checkInfoList.get(i).getProduceTime()))
                                && (checkPhotoList.get(j).getCheckType().equals(checkInfoList.get(i).getCheckType()))) {

                            //插入第一张照片
                            if (checkPhotoList.get(j).getPhotoNumber() == 0) {
                                String pathPictrue = "src/picture/" +checkPhotoList.get(j).getPartNum() + "type" + checkPhotoList.get(j).getCheckType() + "num" + 0 + "p" + checkPhotoList.get(j).getProduceTime() + ".jpg";
                                File file = new File(pathPictrue);
                                if (file.exists()) {
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    Image src = Toolkit.getDefaultToolkit().getImage(file.getPath());
                                    BufferedImage bufferedImage = BufferedImageBuilder.toBufferedImage(src);
                                    ImageIO.write(bufferedImage, "jpg", bos);
                                    Drawing patriarch = sheet.createDrawingPatriarch();
                                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1024, 255, 8, i + 6, 9, i + 7);
                                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                                    patriarch.createPicture(anchor, workbook.addPicture(bos.toByteArray(), HSSFWorkbook.PICTURE_TYPE_JPEG));
                                    if (bos != null) {
                                        bos.close();
                                    }
                                }
                            } else if (checkPhotoList.get(j).getPhotoNumber() == 1) {//插入第二张照片（实际并未使用）
                                String pathPictrue = "src/picture/" +checkPhotoList.get(j).getPartNum() + "type" + checkPhotoList.get(j).getCheckType() + "num" + 1 + "p" + checkPhotoList.get(j).getProduceTime() + ".jpg";
                                File file = new File(pathPictrue);
                                if (file.exists()) {
                                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                                    Image src = Toolkit.getDefaultToolkit().getImage(file.getPath());
                                    BufferedImage bufferedImage = BufferedImageBuilder.toBufferedImage(src);
                                    ImageIO.write(bufferedImage, "jpg", bos);
                                    Drawing patriarch = sheet.createDrawingPatriarch();

                                    XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 1024, 255, 9, i + 6, 10, i + 7);
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

            log.info("===检验 Excel写入完成===");

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
            emailFile("检验记录" + shortDate, excelPath);

            log.info("===excel=time===" + (double) (endExcel - beginExcel) / 1000);


            Thread.sleep(10000);

            // 删除所有文件
            if (checkPhotoList != null && checkPhotoList.size() > 0) {
                for (int i = 0; i < checkPhotoList.size(); i++) {
                    for (int j = 0; j < 2; j++) {
                        String fileName = PATH +checkPhotoList.get(i).getPartNum() + "type" + checkPhotoList.get(i).getCheckType() + "num" + j + "p" + checkPhotoList.get(i).getProduceTime() + ".jpg";
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

            log.info("===删除检验信息完成===");

        }

    }


    //发送邮件的方法
    public void emailFile(String fileName, String path) throws MessagingException, UnsupportedEncodingException {

        //1、创建一个复杂的邮件
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
            if (auditNameList.get(i).getReceiver() != null) {
                tempReceiverList.add(auditNameList.get(i).getReceiver());
            }
            if (auditNameList.get(i).getSender() != null) {
                sender = auditNameList.get(i).getSender();
            }
        }

        List<RegisterInfo> registerInfoList = new ArrayList<>();
        if (registerInfoServiceImp.findAllRegister()!=null){
          registerInfoList = registerInfoServiceImp.findAllRegister();
        }

        if (registerInfoList!=null && registerInfoList.size()>0){
            for (int i = 0; i < registerInfoList.size(); i++) {
                if (registerInfoList.get(i).getUserRight()==1&&registerInfoList.get(i).getEmailAddress()!=null && registerInfoList.get(i).getEmailAddress().length()>0){
                    tempReceiverList.add(registerInfoList.get(i).getEmailAddress());
                }
            }
        }


        //邮件接收人的数组
        String[] receiverList = tempReceiverList.toArray(new String[0]);
        for (String s : receiverList) {
            System.out.println("ss====="+s);
        }

        //邮件内容
        helper.setText("检验记录", true);
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

        log.info("===检验信息邮件，发送完成===");

    }
}
