package com.ynusmartgrid.face_.util;

import cn.hutool.core.util.StrUtil;
import com.ynusmartgrid.face_.app.entity.JobRecord;
import com.ynusmartgrid.face_.common.job.*;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

/**
 * Created by wjs on 2022/04/16
 */
@Service
public class JobUtil {
    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;


    /**
     * 新建一个任务
     */
    public String addJob(JobRecord jobRecord, String cronExpression) throws Exception {
        Date now = new Date();
        Date OneYearLater = new Date(now.getYear() + 1, now.getMonth(), now.getDate());

        Date start = jobRecord.getStartTime() == null ? now : Date.from(jobRecord.getStartTime().atZone(ZoneId.systemDefault()).toInstant());
        Date end = jobRecord.getEndTime() == null ? OneYearLater : Date.from(jobRecord.getEndTime().atZone(ZoneId.systemDefault()).toInstant());


        if (StrUtil.isBlank(jobRecord.getCronExpression()) || !CronExpression.isValidExpression(jobRecord.getCronExpression())) {
            return "Illegal cron expression";   //表达式格式不正确
        }
        JobDetail jobDetail = null;
        //构建job信息
        if (StrUtil.isBlankIfStr(jobRecord.getJobClassName())) {
            return "jobClassName is not null";
        }
        switch (jobRecord.getJobClassName()) {
            case "StartTheCheckInTask":
                return "StartTheCheckInTask skip";
//                jobDetail = JobBuilder.newJob(StartTheCheckInTask.class).withIdentity(jobRecord.getJobName(), jobRecord.getJobGroup()).build();
//                break;
            case "CloseTheCheckInTask":
                jobDetail = JobBuilder.newJob(CloseTheCheckInTask.class).withIdentity(jobRecord.getJobName(), jobRecord.getJobGroup()).build();
                break;
            case "StatisticNumsOfRoom":
                jobDetail = JobBuilder.newJob(StatisticNumsOfRoom.class).withIdentity(jobRecord.getJobName(), jobRecord.getJobGroup()).build();
                break;
            case "StatisticNumOfRoomByDay":
                jobDetail = JobBuilder.newJob(StatisticNumOfRoomByDay.class).withIdentity(jobRecord.getJobName(), jobRecord.getJobGroup()).build();
                break;
            case "StatisticNumOfRoomByMorY":
                jobDetail = JobBuilder.newJob(StatisticNumOfRoomByMorY.class).withIdentity(jobRecord.getJobName(), jobRecord.getJobGroup()).build();
                break;
            case "StatisticAttendanceByDay":
                jobDetail = JobBuilder.newJob(StatisticAttendanceByDay.class).withIdentity(jobRecord.getJobName(), jobRecord.getJobGroup()).build();
                break;
            default:
                return "Failed to get calling function!";
        }
        //表达式调度构建器(即任务执行的时间,不立即执行)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();

        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobRecord.getJobName(), jobRecord.getJobGroup()).startAt(start).endAt(end)
                .withSchedule(scheduleBuilder).build();

        //传递参数
        if (StrUtil.isNotBlank(jobRecord.getInvokeParam())) {
            trigger.getJobDataMap().put("invokeParam", jobRecord.getInvokeParam());
        }
        if(StrUtil.isNotBlank(jobRecord.getGroupId().toString())){
            trigger.getJobDataMap().put("groupId", jobRecord.getGroupId());
        }
        if(StrUtil.isNotBlank(jobRecord.getJobId().toString())){
            trigger.getJobDataMap().put("jobId", jobRecord.getJobId());
        }
        scheduler.scheduleJob(jobDetail, trigger);
        // pauseJob(appQuartz.getJobName(),appQuartz.getJobGroup());
        return "success";
    }

    /**
     * 获取Job状态
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public String getJobState(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(jobName, jobGroup);
        return scheduler.getTriggerState(triggerKey).name();
    }

    //暂停所有任务
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    //暂停任务
    public String pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return "fail";
        } else {
            scheduler.pauseJob(jobKey);
            return "success";
        }

    }

    //恢复所有任务
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    // 恢复某个任务
    public String resumeJob(String jobName, String jobGroup) throws SchedulerException {

        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return "fail";
        } else {
            scheduler.resumeJob(jobKey);
            return "success";
        }
    }

    //删除某个任务
    public String deleteJob(JobRecord jobRecord) throws SchedulerException {
        JobKey jobKey = new JobKey(jobRecord.getJobName(), jobRecord.getJobGroup());
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return "jobDetail is null";
        } else if (!scheduler.checkExists(jobKey)) {
            return "jobKey is not exists";
        } else {
            scheduler.deleteJob(jobKey);
            return "success";
        }

    }

    //修改任务
    public String modifyJob(JobRecord jobRecord) throws SchedulerException {
        if (!CronExpression.isValidExpression(jobRecord.getCronExpression())) {
            return "Illegal cron expression";
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(jobRecord.getJobName(), jobRecord.getJobGroup());
        JobKey jobKey = new JobKey(jobRecord.getJobName(), jobRecord.getJobGroup());
        if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //表达式调度构建器,不立即执行
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobRecord.getCronExpression()).withMisfireHandlingInstructionDoNothing();
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder).build();
            //修改参数
            if (!trigger.getJobDataMap().get("invokeParam").equals(jobRecord.getInvokeParam())) {
                trigger.getJobDataMap().put("invokeParam", jobRecord.getInvokeParam());
            }
            //按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            return "success";
        } else {
            return "job or trigger not exists";
        }

    }
}
