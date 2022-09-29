package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ynusmartgrid.face_.app.entity.*;
import com.ynusmartgrid.face_.app.service.*;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CheckInInfoByDay;
import com.ynusmartgrid.face_.pojo.CheckInInfoByTime;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import com.ynusmartgrid.face_.util.DateUtil;
import com.ynusmartgrid.face_.util.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@Slf4j
@RestController
@RequestMapping("/app/faceCaptureRecord")
public class FaceCaptureRecordController {

    @Autowired
    IFaceCaptureRecordService faceCaptureRecordServiceImpl;

    @Autowired
    IFaceInterfaceService faceInterfaceServiceImpl;

    @Autowired
    IJobRecordService jobRecordServiceImpl;

    @Autowired
    IFaceGroupBelongService faceGroupBelongServiceImpl;

    @Autowired
    IMemberGroupService memberGroupServiceImpl;
    /**
     * @Param:
     * @Author: wjs
     * @date: 21:28
     * 根据id查询特定记录
     * 根据分页以及(人脸faceId，设备equipmentId，置信度confidence，描述description，时间gmtCreate)查找记录
     */
    @PostMapping("/getFaceCaptureRecord")
    public CommonObjReturn getFaceCaptureRecord(@RequestBody HashMap<String, Object> objMap) {
        if (StrUtil.isNotBlank(objMap.get("id").toString())) {
            return new CommonObjReturn(faceCaptureRecordServiceImpl.getById(objMap.get("id").toString()));
        }
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<FaceCaptureRecord> iPage = new Page<>(pageIndex, pageSize);
        QueryWrapper<FaceCaptureRecord> fcrQuery = new QueryWrapper<>();
        fcrQuery.orderByDesc("id");
        if (StrUtil.isNotBlank(objMap.get("faceId").toString())) {
            fcrQuery.eq("face_id", objMap.get("faceId"));
        }
        if (StrUtil.isNotBlank(objMap.get("faceName").toString())) {
            fcrQuery.eq("face_name", objMap.get("faceName"));
        }
        if (StrUtil.isNotBlank(objMap.get("equipmentIp").toString())) {
            fcrQuery.eq("equipment_ip", objMap.get("equipmentIp"));
        }
        if (StrUtil.isNotBlank(objMap.get("confidence").toString())) {
            fcrQuery.ge("confidence", objMap.get("confidence"));
        }
        if (StrUtil.isNotBlank(objMap.get("description").toString())) {
            fcrQuery.like("description", objMap.get("description"));
        }
        if (StrUtil.isNotBlank(objMap.get("isStranger").toString())) {
            fcrQuery.eq("is_stranger", objMap.get("isStranger"));
        }
        if (StrUtil.isNotBlank(objMap.get("address").toString())) {
            fcrQuery.eq("address", objMap.get("address"));
        }
        if (StrUtil.isNotBlank(objMap.get("gmtCreate").toString())) {
            // 查当天
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("gmtCreate").toString(), df);
            LocalDateTime endTime = startTime.plusDays(1L);
            fcrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            fcrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(faceCaptureRecordServiceImpl.page(iPage, fcrQuery));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:37
     * 根据传入的设备信息创建设备
     */
    @PostMapping("/saveFaceCaptureRecord")
    public CommonObjReturn addFaceCaptureRecord(@Validated(value = {FaceCaptureRecord.Add.class}) @RequestBody FaceCaptureRecord faceCaptureRecord) {

        // 如果是陌生人直接存
        String facePath;
        if (faceCaptureRecord.getIsStranger()) {
            facePath = "stranger" + FileUtil.FILE_SEPARATOR + faceCaptureRecord.getFaceId();
        } else {
            // 查询上一条数据
            QueryWrapper<FaceCaptureRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("face_id", faceCaptureRecord.getFaceId());
            queryWrapper.orderByDesc("gmt_create").last("limit 1");
            FaceCaptureRecord theLastRecord = faceCaptureRecordServiceImpl.getOne(queryWrapper);
            // 在同一个房间并且时间相隔小于一小时不存
            // 在同一个房间并且时间相隔大于一小时存
            // 不在同一个房间 存
            if (theLastRecord != null && theLastRecord.getAddress().equals(faceCaptureRecord.getAddress())
                    && theLastRecord.getGmtCreate().getHour() == faceCaptureRecord.getGmtCreate().getHour()) {
                return new CommonObjReturn(true);
            }
            facePath = faceCaptureRecord.getFaceId();
        }
        StringBuffer parentDirPath = new StringBuffer();
        parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                .append("faceCapture")
                .append(FileUtil.FILE_SEPARATOR)
                .append(facePath)
                .append(FileUtil.FILE_SEPARATOR);
        faceCaptureRecord.setFace(IoUtil.copyToLocal(faceCaptureRecord.getFace(), parentDirPath.toString()));
        // log.info("杨鹏面部识别检测提交参数"+ faceCaptureRecord);
        return new CommonObjReturn(faceCaptureRecordServiceImpl.saveOrUpdate(faceCaptureRecord));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:37
     * 根据id删除人脸记录
     */
    @PostMapping("/removeFaceCaptureRecord")
    public CommonObjReturn deleteFaceCaptureRecord(@RequestBody Long id) {
        return new CommonObjReturn(faceCaptureRecordServiceImpl.removeById(id));
    }


    /**
     * @Param:
     * @Author: wjs
     * @date: 20:52
     * 获取人员轨迹
     * 页号pageIndex，页大小pageSize
     */
    @PostMapping("/getPersonnelTrack")
    public CommonObjReturn getPersonnelTrack(@RequestBody HashMap<String, Object> objMap) {
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<Face> page = new Page<>(pageIndex, pageSize);
        List<Long> faceIds = faceInterfaceServiceImpl.page(page).getRecords().stream().map(Face::getId).collect(Collectors.toList());
        QueryWrapper<FaceCaptureRecord> brrQuery = new QueryWrapper<>();
        brrQuery.orderByAsc("gmt_create");
        brrQuery.in("face_id", faceIds);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        brrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
        brrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        List<FaceCaptureRecord> faceCaptureRecordList = faceCaptureRecordServiceImpl.list(brrQuery);
        Map<String, List<FaceCaptureRecord>> fcrListMap = faceCaptureRecordList.stream().collect(Collectors.groupingBy(FaceCaptureRecord::getFaceName));
        return new CommonObjReturn(fcrListMap);
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:26
     * 获取单人的历史轨迹
     * faceId,startTime,endTime
     */
    @PostMapping("/getPersonnelTrackByFace")
    public CommonObjReturn getPersonnelTrackByFace(@RequestBody HashMap<String, Object> objMap) {
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        if (StrUtil.isBlankIfStr(objMap.get("faceId"))) {
            return new CommonObjReturn<Object>("查询对象faceId不允许为空不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<FaceCaptureRecord> page = new Page<>(pageIndex, pageSize);
        QueryWrapper<FaceCaptureRecord> brrQuery = new QueryWrapper<>();
        brrQuery.orderByAsc("gmt_create");
        brrQuery.eq("face_id", objMap.get("faceId"));
        if (StrUtil.isNotBlank(objMap.get("startTime").toString()) && StrUtil.isNotBlank(objMap.get("endTime").toString())) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("startTime").toString(), df);
            LocalDateTime endTime = LocalDateTime.parse(objMap.get("endTime").toString(), df);
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        } else {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(faceCaptureRecordServiceImpl.page(page, brrQuery));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:55
     * 传入值为，jobId(必填)，startTime,endTime
     */
    @PostMapping("/getCheckInMemberByJob")
    public void getCheckInMemberByJob(@RequestBody HashMap<String, Object> objMap, HttpServletResponse response) throws Exception {
        if (StrUtil.isBlankIfStr(objMap.get("jobId").toString())) {
            throw new Exception("jobId不允许为空");
        }
        // 判断是否查询多天
        boolean timeSelect = false;
        JobRecord jobRecord = jobRecordServiceImpl.getById(objMap.get("jobId").toString());
        MemberGroup memberGroup = memberGroupServiceImpl.getById(jobRecord.getGroupId());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 默认为当天的数据
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        if (StrUtil.isNotBlank(objMap.get("startTime").toString()) && StrUtil.isNotBlank(objMap.get("endTime").toString())) {
            // 若有时间要求则为限定时间
            startTime = LocalDateTime.of(LocalDate.parse(objMap.get("startTime").toString(), dayFormatter),LocalTime.MIN);
            endTime = LocalDateTime.of(LocalDate.parse(objMap.get("endTime").toString(), dayFormatter),LocalTime.MAX);
            // 传入的开始时间比任务定的时间还早，不合理，强行矫正任务开始时间
            startTime = startTime.compareTo(jobRecord.getStartTime())<0?jobRecord.getStartTime():startTime;
            // 传入的结束时间比任务结束的时间还晚，不合理，强行矫正为任务结束时间
            endTime = endTime.compareTo(jobRecord.getEndTime())>0?jobRecord.getEndTime():endTime;
            timeSelect = true;

        }else if(StrUtil.isNotBlank(objMap.get("startTime").toString())){
            // 选择某一天获取考勤信息
            LocalDate date = LocalDate.parse(objMap.get("startTime").toString(), dayFormatter);
            startTime = LocalDateTime.of(date,LocalTime.MIN);
            endTime = LocalDateTime.of(date,LocalTime.MAX);
        }
        List<Map<String, Object>> resultList = faceCaptureRecordServiceImpl.listMaps(
                new QueryWrapper<FaceCaptureRecord>()
                        .select("face_id as faceId", "face_name as faceName", "MIN(gmt_create) as appearTime", "day")
                        .eq("is_stranger", false) //非陌生人
                        .inSql("face_id", "select face_id from face_group_belong where group_id=" + jobRecord.getGroupId()) //组内成员
                        .apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')")
                        .apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')") // 当天
                        .groupBy("face_id", "face_name", "day"));
        /**
        *   face_id                         face_name   appearTime      day
         *0DCD2BBDAFA94FEFA5A4A4323A055519	王耀威	2022-04-23 20:22:42	2022-04-23
         * E2CA939C3D744A14AC22667D405FA322	杨鹏	2022-04-23 20:23:14	2022-04-23
         * C1A273F092CA45AEA9A59BA1B73388DB	刘博	2022-04-23 20:23:58	2022-04-23
         * D023E8B4E8B34DD78B013B521929F239	南峰涛	2022-04-23 20:35:28	2022-04-23
         * ABDA9FB740C74580996B78193B0765E8	常相超	2022-04-24 16:01:07	2022-04-24
         * EC405EF22AD24037B17B326A1A3A93CC	王罕文	2022-04-24 16:01:15	2022-04-24
         * 1EA0AE3F9B3647E781CB8E8B23186158	王佳舜	2022-04-24 16:01:31	2022-04-24
         * E99ADC5AB0484DD2BFA4C6A510EB4DF4	张炎	2022-04-24 16:03:22	2022-04-24
         * E2CA939C3D744A14AC22667D405FA322	杨鹏	2022-04-24 16:04:05	2022-04-24
         * E7592E4FEEFF47FE8BE6EAD9E284771F	汪佩	2022-04-24 16:04:57	2022-04-24
         * C706CDC6803D4BAAA2DDEAA8691B8213	郭春雪	2022-04-24 16:13:13	2022-04-24
         * 0DCD2BBDAFA94FEFA5A4A4323A055519	王耀威	2022-04-24 16:14:15	2022-04-24
         * 170BED5C39294FC3B56B4E7D097C4E32	杨云	2022-04-24 16:15:43	2022-04-24
         *
         * 比如查询时间为2022-04-23 时间仅有一天 转换成
         * faceId                           faceName    appearTime          day         attend    belated
         * 0DCD2BBDAFA94FEFA5A4A4323A055519| 王耀威|	2022-04-23 20:22:42|	2022-04-23 |1       |0
         * 5444D358AAC0471098CF28123F1A36F9| 于明浩| null               |   2022-04-23  |0       |1
         * ...
         *
         * 查询时间为2022-04-23 到 2022-24-24 两天(多天) 转换成
         * faceId                           faceName    day    attend    belated
         * 0DCD2BBDAFA94FEFA5A4A4323A055519| 王耀威|
         *                                           2022-04-23  1    0
         *                                           2022-04-24  1    1
         * C1A273F092CA45AEA9A59BA1B73388DB| 刘博|   2022-04-23   1    1
         *                                          2022-04-24   0    1
         *
        */
        List<FaceGroupBelong> fgbList = faceGroupBelongServiceImpl.list(new QueryWrapper<FaceGroupBelong>().eq("group_id",jobRecord.getGroupId()));
        if(timeSelect){
            List<String> dayPeriod = CheckInInfoByTime.getRangeDayList(startTime.format(df),endTime.format(df));
            List<CheckInInfoByTime> checkInInfoByTimes = CheckInInfoByTime.transform2TimeDurationInfo(fgbList, resultList,jobRecord, dayPeriod);
            faceCaptureRecordServiceImpl.exportExcelByPeriod(response, checkInInfoByTimes, dayPeriod);
        }else{
            List<CheckInInfoByDay> checkInInfoByDayList = CheckInInfoByDay.transform2DayInfo(fgbList,resultList,jobRecord);
            HashMap<String,Object> resultMap = new HashMap<>();
            resultMap.put("total", checkInInfoByDayList.size());
            double attend = 0.0;
            double belated = 0.0;
            for(CheckInInfoByDay checkInInfoByDay: checkInInfoByDayList){
                if(checkInInfoByDay.isAttend()){
                    attend ++;
                }
                if(checkInInfoByDay.isBelated()){
                    belated ++;
                }
            }
            resultMap.put("day", objMap.get("startTime").toString().substring(0,10));
            resultMap.put("total", fgbList.size());
            resultMap.put("attend", attend);
            resultMap.put("belated", belated);
            resultMap.put("attendance", attend/fgbList.size());
            resultMap.put("latenessRate", belated/fgbList.size());
            resultMap.put("groupName",memberGroup.getGroupName());
            resultMap.put("data",checkInInfoByDayList);
            faceCaptureRecordServiceImpl.exportExcelByToDay(response, resultMap);
        }
    }
}
