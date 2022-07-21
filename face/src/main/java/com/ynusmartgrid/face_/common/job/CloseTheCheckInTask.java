package com.ynusmartgrid.face_.common.job;

import org.quartz.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wjs on 2022/04/16
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CloseTheCheckInTask implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss");
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        System.out.println("关闭签到任务"+ jobDataMap.getString("invokeParam") + dateFormat.format(new Date()));
    }
}
