package com.ynusmartgrid.face_.common.job;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.sql.visitor.functions.Char;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IMemberGroupService;
import com.ynusmartgrid.face_.app.service.IStatisticRecodeService;
import com.ynusmartgrid.face_.common.MaxNumBerOfRoomList;
import com.ynusmartgrid.face_.util.JobUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wjs on 2022/05/11
 */
@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatisticNumOfRoomByDay implements Job {

    @Autowired
    IStatisticRecodeService statisticRecodeServiceImpl;

    @Autowired
    IMemberGroupService memberGroupServiceImpl;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        // 获取必要参数
        JSONObject dataMap = (JSONObject) JSON.parse(jobDataMap.getString("invokeParam"));
        String[] groupIds = dataMap.get("groupIds").toString().split(",");
        String sumUpGroupId = dataMap.get("sumUpGroupId").toString();
        String sumUpGroupName = memberGroupServiceImpl.getById(sumUpGroupId).getGroupName();
        if(StrUtil.isBlankIfStr(sumUpGroupName)){
            log.error("============未能获取到组============");
            return;
        }
        int maxCount;
        LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        LocalDateTime endTime = startTime.plusDays(1L);
        QueryWrapper<StatisticRecode> staQuery = new QueryWrapper<>();
        staQuery.select("MAX(statistic_info_recode) as max")
                .eq("recode_type", 0)
                .in("group_id", groupIds)
                .groupBy("group_id");
        List<Map<String , Object>> maxList = statisticRecodeServiceImpl.listMaps(staQuery);
        if(maxList.size() < 1){
            log.error("============每日统计，未能获取到出勤数据============");
            return;
        }
        maxCount = maxList.stream().mapToInt(n -> Integer.parseInt(n.get("max").toString())).sum();
        StatisticRecode result = new StatisticRecode(null, Long.parseLong(sumUpGroupId), sumUpGroupName,String.valueOf(maxCount),LocalDateTime.now(),1);
        statisticRecodeServiceImpl.save(result);
        log.info("============每日更新前一日出勤总人数============");
        log.info(String.format("存储数据为：%s", result.toString()));
    }
}
