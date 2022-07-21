package com.ynusmartgrid.face_.common.job;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IMemberGroupService;
import com.ynusmartgrid.face_.app.service.IStatisticRecodeService;
import com.ynusmartgrid.face_.util.JobUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Created by wjs on 2022/05/11
 */
@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatisticNumOfRoomByMorY implements Job{

    @Autowired
    IStatisticRecodeService statisticRecodeServiceImpl;

    @Autowired
    IMemberGroupService memberGroupServiceImpl;

    @Autowired
    JobUtil jobUtil;

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        // 获取必要参数
        HashMap<String,Object> dataMap = JSON.parseObject(JSON.toJSONString(jobDataMap.getString("invokeParam")),HashMap.class);
        String groupId = dataMap.get("groupId").toString();
        String groupName = memberGroupServiceImpl.getById(groupId).getGroupName();
        if(StrUtil.isBlankIfStr(groupName)){
            log.error("============未能获取到组============");
            return;
        }
        char timeSpan = dataMap.get("timeSpan").toString().charAt(0);
        int recodeType = 2;
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(1L);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<StatisticRecode> staQuery = new QueryWrapper<>();
        switch (timeSpan) {
            case 'M':
                recodeType = 2;
                startDate = endDate.minusMonths(1L);
                break;
            // 若时间跨度为年，记录类型为3
            case 'Y':
                recodeType = 3;
                startDate = endDate.minusYears(1L);
                break;
            default:
                log.error("============插入统计任务未能获得时间跨度============");
                return;
        }
        staQuery.select("SUM(statistic_info_recode) as statistic_info_recode")
                .eq("recode_type", recodeType - 1)
                .eq("group_id", groupId)
                .apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startDate.format(df) + "')")
                .apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endDate.format(df) + "')")
                .groupBy("recode_type");
        int count = statisticRecodeServiceImpl.count(staQuery);
        if(count<1){
            log.error("============定时任务获未能取到统计数据============");
            return;
        }
        StatisticRecode result = new StatisticRecode(null,Long.parseLong(groupId),groupName,count,endDate,recodeType);
        statisticRecodeServiceImpl.save(result);
        log.info(String.format("============插入时间跨度%c的数据============", timeSpan));
        log.info(String.format("存储数据为：%s", result.toString()));
    }


}
