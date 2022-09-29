package com.ynusmartgrid.face_.common.job;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.BehaviorRecognitionRecord;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IBehaviorRecognitionRecordService;
import com.ynusmartgrid.face_.app.service.IMemberGroupService;
import com.ynusmartgrid.face_.app.service.IStatisticRecodeService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wjs on 2022/09/02
 */
@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatisticNumsOfDangerousThingsByDay implements Job {

    @Autowired
    IMemberGroupService memberGroupServiceImpl;
    @Autowired
    IBehaviorRecognitionRecordService behaviorRecognitionRecordServiceImpl;

    @Autowired
    IStatisticRecodeService statisticRecodeServiceImpl;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        // 获取必要参数
        Long groupId = jobDataMap.getLong("groupId");
        Long jobId = jobDataMap.getLong("jobId");
        String groupName = memberGroupServiceImpl.getById(groupId).getGroupName();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        LocalDateTime endTime = startTime.plusDays(1L);
        QueryWrapper<BehaviorRecognitionRecord> brrQuery = new QueryWrapper<>();
        brrQuery.in("type", Stream.of(0,1,2,3).collect(Collectors.toList()))
                .apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')")
                .apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        int dangerousThingCount = behaviorRecognitionRecordServiceImpl.count(brrQuery);
        statisticRecodeServiceImpl.save(new StatisticRecode(groupId,groupName,String.valueOf(dangerousThingCount),jobId.intValue()));
        log.info("统计当日危险任务已完成，危险物品出现次数为："+dangerousThingCount);

    }
}
