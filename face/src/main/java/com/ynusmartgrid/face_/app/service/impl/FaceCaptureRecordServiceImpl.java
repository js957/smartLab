package com.ynusmartgrid.face_.app.service.impl;

import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.ynusmartgrid.face_.app.mapper.FaceCaptureRecordMapper;
import com.ynusmartgrid.face_.app.service.IFaceCaptureRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ynusmartgrid.face_.pojo.CheckInInfoByDay;
import com.ynusmartgrid.face_.pojo.CheckInInfoByTime;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@Service
public class FaceCaptureRecordServiceImpl extends ServiceImpl<FaceCaptureRecordMapper, FaceCaptureRecord> implements IFaceCaptureRecordService {

    @Override
    public void exportExcelByToDay(HttpServletResponse response, HashMap<String, Object> resultMap) {

        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        //创建工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();//这里也可以设置sheet的Name
        //创建工作表对象
        HSSFSheet sheet = workbook.createSheet("当日考勤表");

        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置列宽
        for (int w = 0; w < 10; w++) {
            sheet.setColumnWidth(w, 3766);
        }
        sheet.setColumnWidth(2, 5000);
        for (int w = 0; w < 10; w++) {
            sheet.setDefaultColumnStyle(w, style);
        }
        int i;
        //创建第一行表头
        HSSFRow headRow = sheet.createRow(0);
        headRow.createCell(0).setCellValue("应到人员");
        headRow.createCell(1).setCellValue("实到人员");
        headRow.createCell(2).setCellValue("实到人员出现时间");
        headRow.createCell(3).setCellValue("是否迟到");
        headRow.createCell(5).setCellValue("未到人员");
        headRow.createCell(6).setCellValue("迟到人员");
        headRow.createCell(8).setCellValue("应到总数");
        headRow.createCell(9).setCellValue(resultMap.get("total").toString());
        List<CheckInInfoByDay> checkInInfoByDayList = (List<CheckInInfoByDay>) resultMap.get("data");
        // 应到人员
        for (i = 1; i <= Integer.parseInt(resultMap.get("total").toString()); i++) {
            sheet.createRow(i).createCell(0).setCellValue(checkInInfoByDayList.get(i - 1).getName());
        }
        // 实到人员
        i = 1;
        for (CheckInInfoByDay checkInInfoByDay : checkInInfoByDayList.stream().filter(f -> f.isAttend()).collect(Collectors.toList())) {
            sheet.getRow(i).createCell(1).setCellValue(checkInInfoByDay.getName());
            sheet.getRow(i).createCell(2).setCellValue(checkInInfoByDay.getAppearTime());
            sheet.getRow(i).createCell(3).setCellValue(checkInInfoByDay.isBelated() ? "是" : "否");
            i++;
        }
        // 未到人员
        i = 1;
        for (CheckInInfoByDay checkInInfoByDay : checkInInfoByDayList.stream().filter(f -> f.isAttend() == false).collect(Collectors.toList())) {
            sheet.getRow(i).createCell(5).setCellValue(checkInInfoByDay.getName());
            i++;
        }
        // 迟到人员
        i = 1;
        for (CheckInInfoByDay checkInInfoByDay : checkInInfoByDayList.stream().filter(f -> f.isBelated()).collect(Collectors.toList())) {
            sheet.getRow(i).createCell(6).setCellValue(checkInInfoByDay.getName());
            i++;
        }
        sheet.getRow(1).createCell(8).setCellValue("实到总数");
        sheet.getRow(1).createCell(9).setCellValue(resultMap.get("attend").toString());
        sheet.getRow(2).createCell(8).setCellValue("出勤率");
        sheet.getRow(2).createCell(9).setCellValue(decimalFormat.format(Double.parseDouble(resultMap.get("attendance").toString())));
        sheet.getRow(3).createCell(8).setCellValue("迟到人数");
        sheet.getRow(3).createCell(9).setCellValue(resultMap.get("belated").toString());
        sheet.getRow(4).createCell(8).setCellValue("迟到率");
        sheet.getRow(4).createCell(9).setCellValue(decimalFormat.format(Double.parseDouble(resultMap.get("latenessRate").toString())));
        sheet.getRow(5).createCell(8).setCellValue("人员组别");
        sheet.getRow(5).createCell(9).setCellValue(resultMap.get("groupName").toString());
        sheet.getRow(6).createCell(8).setCellValue("日期");
        sheet.getRow(6).createCell(9).setCellValue(resultMap.get("day").toString());
        //准备将Excel的输出流通过response输出到页面下载
        //八进制输出流
        response.setContentType("application/octet-stream");
        //这后面可以设置导出Excel的名称，此例中名为student.xls
        response.setHeader("Content-disposition", "attachment;filename=" + resultMap.get("day").toString() + ".xls");
        try {
            //刷新缓冲
            response.flushBuffer();
            //workbook将Excel写入到response的输出流中，供页面下载
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exportExcelByPeriod(HttpServletResponse response, List<CheckInInfoByTime> checkInInfoByTimes, List<String> dayPeriod) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        //创建工作薄对象
        HSSFWorkbook workbook = new HSSFWorkbook();//这里也可以设置sheet的Name
        //创建工作表对象
        HSSFSheet sheet = workbook.createSheet("每日考勤表");
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置列宽
        for (int w = 0; w < 13; w++) {
            sheet.setColumnWidth(w, 3766);
        }
        for (int w = 0; w < 13; w++) {
            sheet.setDefaultColumnStyle(w, style);
        }


        int r, c;
        //创建第一行表头
        HSSFRow headRow = sheet.createRow(0);
        headRow.createCell(0).setCellValue("姓名");
        headRow.createCell(1).setCellValue("按时出勤数");
        headRow.createCell(2).setCellValue("按时出勤率");
        headRow.createCell(3).setCellValue("出勤数");
        headRow.createCell(4).setCellValue("出勤率");
        headRow.createCell(5).setCellValue("迟到数");
        headRow.createCell(6).setCellValue("迟到率");

        c = 7;
        for (String day : dayPeriod) {
            headRow.createCell(c).setCellValue(day);
            c++;
        }
        r = 1;
        for (CheckInInfoByTime checkInInfoByTime : checkInInfoByTimes) {
            HSSFRow row = sheet.createRow(r);
            row.createCell(0).setCellValue(checkInInfoByTime.getName());
            row.createCell(1).setCellValue(checkInInfoByTime.getAttend() - checkInInfoByTime.getBelated());
            row.createCell(2).setCellValue(decimalFormat.format(checkInInfoByTime.getAttendanceOnTime()));
            row.createCell(3).setCellValue(checkInInfoByTime.getAttend());
            row.createCell(4).setCellValue(decimalFormat.format(checkInInfoByTime.getAttendance()));
            row.createCell(5).setCellValue(checkInInfoByTime.getBelated());
            row.createCell(6).setCellValue(decimalFormat.format(checkInInfoByTime.getLatenessRate()));

            c = 7;
            for (CheckInInfoByDay checkInInfoByDay : checkInInfoByTime.getDayCheckInInfo()) {
                HSSFCell hssfCell = row.createCell(c);
                if (checkInInfoByDay.isAttend() && checkInInfoByDay.isBelated()) {
                    hssfCell.setCellValue("迟到");
                } else if (checkInInfoByDay.isAttend()) {
                    hssfCell.setCellValue("出勤");
                } else {
                    hssfCell.setCellValue("缺勤");
                }
                c++;
            }
            r++;
        }
        //准备将Excel的输出流通过response输出到页面下载
        //八进制输出流
        response.setContentType("application/octet-stream");
        //这后面可以设置导出Excel的名称，此例中名为student.xls
        response.setHeader("Content-disposition", "attachment;filename=" + dayPeriod.get(0) + "~" + dayPeriod.get(dayPeriod.size() - 1) + ".xls");
        try {
            //刷新缓冲
            response.flushBuffer();
            //workbook将Excel写入到response的输出流中，供页面下载
            workbook.write(response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
