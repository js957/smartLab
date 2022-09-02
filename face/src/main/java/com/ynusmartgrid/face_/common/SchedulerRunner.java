package com.ynusmartgrid.face_.common;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.JobRecord;
import com.ynusmartgrid.face_.app.mapper.JobRecordMapper;
import com.ynusmartgrid.face_.app.service.IJobRecordService;
import com.ynusmartgrid.face_.app.service.impl.JobRecordServiceImpl;
import com.ynusmartgrid.face_.util.JobUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by wjs on 2022/04/16
 * 启动项目时加载定时任务
 */
@Service
public class SchedulerRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulerRunner.class);

    @Autowired
    JobUtil jobUtil;

    @Autowired
    IJobRecordService jobRecordServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        QueryWrapper<JobRecord> jobQuery = new QueryWrapper<>();
        jobQuery.apply("UNIX_TIMESTAMP(end_time) > UNIX_TIMESTAMP('" + LocalDateTime.now().format(df) + "')");
        List<JobRecord> jobRecordList = jobRecordServiceImpl.list(jobQuery);
        for (JobRecord jobRecord : jobRecordList) {
            if(jobRecord.getJobClassName().equals("StartTheCheckInTask")){
                continue;
            }
            String result = jobUtil.addJob(jobRecord, jobRecord.getCronExpression());
            if(StrUtil.isNotBlank(jobRecord.getCloseCronExpression())) {
                jobRecord.setJobName("Close-" + jobRecord.getJobName());
                jobRecord.setJobGroup("Close-" + jobRecord.getJobGroup());
                jobUtil.addJob(jobRecord, jobRecord.getCloseCronExpression());
            }
            if("success".equals(result)) {
                LOGGER.info(String.format("定时任务 %s.%s 已挂载", jobRecord.getJobGroup(), jobRecord.getJobName()));
            }else{
                LOGGER.info(String.format("定时任务 %s.%s 出现了点问题，错误信息："+result, jobRecord.getJobGroup(), jobRecord.getJobName()));
            }
        }
        LOGGER.info(String.format("所有定时任务已启动, 数量：%d",jobRecordList.size()));
    }
}
