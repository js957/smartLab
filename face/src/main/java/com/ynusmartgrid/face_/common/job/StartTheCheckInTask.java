package com.ynusmartgrid.face_.common.job;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.ynusmartgrid.face_.app.entity.FaceGroupBelong;
import com.ynusmartgrid.face_.app.entity.MemberGroup;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IFaceCaptureRecordService;
import com.ynusmartgrid.face_.app.service.IFaceGroupBelongService;
import com.ynusmartgrid.face_.app.service.IMemberGroupService;
import com.ynusmartgrid.face_.app.service.IStatisticRecodeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wjs on 2022/04/15
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StartTheCheckInTask implements Job {

    @Autowired
    IFaceCaptureRecordService faceCaptureRecordServiceImpl;

    @Autowired
    IStatisticRecodeService statisticRecodeServiceImpl;

    @Autowired
    IMemberGroupService memberGroupServiceImpl;
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss");
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        MemberGroup memberGroup = memberGroupServiceImpl.getById(jobDataMap.getLong("groupId"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> resultList = faceCaptureRecordServiceImpl.listMaps(
                new QueryWrapper<FaceCaptureRecord>()
                        .select("face_id as faceId", "face_name as faceName", "MIN(gmt_create) as appearTime")
                        .eq("is_stranger",false) //非陌生人
                        .inSql("face_id", "select face_id from face_group_belong where group_id="+memberGroup.getGroupId()) //组内成员
                        .apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" +  LocalDateTime.of(LocalDate.now(), LocalTime.MIN).format(df) + "')")
                        .apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + LocalDateTime.now().format(df) + "')") // 当天
                        .groupBy("face_id", "face_name"));
        statisticRecodeServiceImpl.save(new StatisticRecode(memberGroup.getGroupId(),memberGroup.getGroupName(), JSONUtil.toJsonStr(resultList), (int) jobDataMap.getLong("jobId")));
        log.info("=================定时签到触发，出勤签到表已生成===================");
    }
}
