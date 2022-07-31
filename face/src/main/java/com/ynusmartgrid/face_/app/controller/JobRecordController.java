package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ynusmartgrid.face_.app.entity.Equipment;
import com.ynusmartgrid.face_.app.entity.JobRecord;
import com.ynusmartgrid.face_.app.entity.RecognizableThings;
import com.ynusmartgrid.face_.app.service.impl.JobRecordServiceImpl;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import com.ynusmartgrid.face_.util.JobUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-04-15
 */
@RestController
@RequestMapping("/app/jobRecord")
public class JobRecordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRecordController.class);

    @Autowired
    JobUtil jobUtil;

    @Autowired
    JobRecordServiceImpl jobRecordService;

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:31
     * 获取任务，可根据id(id)获取指定任务,也可获取所有任务,也可根据条件(jobName,jobGroup,jobClassName,groupId,endTime)获取任务
    */
    @PostMapping("/getJobRecord")
    public CommonObjReturn getJobRecord(@RequestBody HashMap<String,Object> objMap){
        if(StrUtil.isNotBlank(objMap.get("id").toString())){
            return new CommonObjReturn(jobRecordService.getById(objMap.get("id").toString()));
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<JobRecord> iPage = new Page<>(pageIndex,pageSize);
        QueryWrapper<JobRecord> jobQuery = new QueryWrapper<>();
        if(StrUtil.isNotBlank(objMap.get("jobName").toString())){
            jobQuery.like("job_name",objMap.get("jobName").toString());
        }
        if(StrUtil.isNotBlank(objMap.get("jobGroup").toString())){
            jobQuery.like("job_group",objMap.get("jobGroup").toString());
        }
        if(StrUtil.isNotBlank(objMap.get("jobClassName").toString())){
            jobQuery.like("job_class_name",objMap.get("jobClassName").toString());
        }
        if(StrUtil.isNotBlank(objMap.get("groupId").toString())){
            jobQuery.eq("group_id",objMap.get("groupId").toString());
        }
        if(StrUtil.isNotBlank(objMap.get("endTime").toString())){
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime endTime = LocalDateTime.parse(objMap.get("endTime").toString(), df);
            jobQuery.apply("UNIX_TIMESTAMP(end_time) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(jobRecordService.page(iPage, jobQuery));

    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:23
     * 传入一个jobRecord对象，生成一个定时任务
    */
    @PostMapping("/addJob")
    public CommonObjReturn addJob(@Validated(value = {JobRecord.Add.class}) @RequestBody JobRecord jobRecord) throws Exception {
        if(StrUtil.isBlank(jobRecord.getJobName())){
            jobRecord.setJobName(jobRecord.getJobClassName() + "_" + jobRecordService.count());
        }
        if(StrUtil.isBlank(jobRecord.getJobGroup())){
            jobRecord.setJobGroup(jobRecord.getJobClassName() + "_" + jobRecord.getGroupId());
        }
        jobRecordService.saveOrUpdate(jobRecord);
        String result = jobUtil.addJob(jobRecord, jobRecord.getCronExpression());
        if (StrUtil.isNotBlank(jobRecord.getCloseCronExpression())) {
            jobRecord.setJobName("Close-" + jobRecord.getJobName());
            jobRecord.setJobGroup("Close-" + jobRecord.getJobGroup());
            jobUtil.addJob(jobRecord, jobRecord.getCloseCronExpression());
        }
        LOGGER.info(String.format("任务：%s.%s 启动",jobRecord.getJobGroup(), jobRecord.getJobName()));
        return new CommonObjReturn("start:" + result);
    }


    /**
    *@Param:
    *@Author: wjs
    *@date: 21:23
     * 暂停任务，批量，传入为一组任务的id
    */
    @PostMapping(value = "/pauseJob")
    public CommonObjReturn pausejob(@RequestBody Integer[] quartzIds) throws Exception {
        JobRecord jobRecord = null;
        if (quartzIds.length > 0) {
            for (Integer quartzId : quartzIds) {
                jobRecord = jobRecordService.getById(quartzId);
                jobUtil.pauseJob(jobRecord.getJobName(), jobRecord.getJobGroup());
            }
            LOGGER.info(String.format("任务：%s.%s 暂停",jobRecord.getJobGroup(), jobRecord.getJobName()));
            return new CommonObjReturn("success pauseJob");
        } else {
            return new CommonObjReturn("fail pauseJob", Constant.RS_SYSTEM_ERROR);
        }
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:27
     * 恢复被暂停的任务，批量，传入一批任务id
    */
    @PostMapping(value = "/resumeJob")
    public CommonObjReturn resumejob(@RequestBody Integer[] quartzIds) throws Exception {
        JobRecord jobRecord = null;
        if (quartzIds.length > 0) {
            for (Integer quartzId : quartzIds) {
                jobRecord = jobRecordService.getById(quartzId);
                jobUtil.resumeJob(jobRecord.getJobName(), jobRecord.getJobGroup());
            }
            LOGGER.info(String.format("任务：%s.%s 恢复",jobRecord.getJobGroup(), jobRecord.getJobName()));
            return new CommonObjReturn("success resumeJob");
        } else {
            return new CommonObjReturn("fail resumeJob", Constant.RS_SYSTEM_ERROR);
        }
    }


    /**
    *@Param:
    *@Author: wjs
    *@date: 21:28
     * 批量删除任务
    */
    @PostMapping(value = "/deleteJob")
    public CommonObjReturn deleteJob(@RequestBody Integer[] quartzIds) throws Exception {
        JobRecord jobRecord = null;
        for (Integer quartzId : quartzIds) {
            jobRecord = jobRecordService.getById(quartzId);
            String ret = jobUtil.deleteJob(jobRecord);
            if ("success".equals(ret)) {
                jobRecordService.removeById(quartzId);
                LOGGER.info(String.format("任务：%s.%s 删除",jobRecord.getJobGroup(), jobRecord.getJobName()));
            }
        }
        return new CommonObjReturn("success deleteJob");
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:28
     * 修改某指定任务
    */
    @PostMapping(value = "/updateJob")
    public CommonObjReturn modifyJob(@RequestBody JobRecord jobRecord) throws Exception {
        String ret = jobUtil.modifyJob(jobRecord);
        if ("success".equals(ret)) {
            jobRecordService.updateById(jobRecord);
            LOGGER.info(String.format("任务：%s.%s 被修改",jobRecord.getJobGroup(), jobRecord.getJobName()));
            return new CommonObjReturn("success updateJob", ret);
        } else {
            return new CommonObjReturn(ret, Constant.RS_SYSTEM_ERROR);
        }
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:28
     * 暂停所有任务
    */
    @GetMapping(value = "/pauseAll")
    public CommonObjReturn pauseAllJob() throws Exception {
        jobUtil.pauseAllJob();
        LOGGER.info("所有任务已被暂停");
        return new CommonObjReturn("success pauseAll");
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:29
     * 恢复所有任务
    */
    @GetMapping(value = "/rePauseAll")
    public CommonObjReturn rePauseAllJob() throws Exception {
        jobUtil.resumeAllJob();
        LOGGER.info("所有任务已被恢复");
        return new CommonObjReturn("success rePauseAll");
    }

    @GetMapping("/test")
    public CommonObjReturn test() throws Exception {
        JobRecord jobRecord = new JobRecord();
        jobRecord.setJobClassName("StartTheCheckInTask");
        jobRecord.setJobId(1L);
        jobRecord.setCreateTime(LocalDateTime.now());
        jobRecord.setJobName("checkIn1");
        jobRecord.setJobGroup("checkInGroup");
        jobRecord.setStartTime(LocalDateTime.now());
        jobRecord.setEndTime(LocalDateTime.now().plusMinutes(2L));
        jobRecord.setCronExpression("*/10 * * * * ?");
        jobRecord.setCloseCronExpression("*/15 * * * * ?");
        jobRecord.setInvokeParam("{data:{data1:3}}");
        String result = jobUtil.addJob(jobRecord, jobRecord.getCronExpression());
        jobRecord.setJobGroup("closeCheckInGroup");
        jobRecord.setJobClassName("CloseTheCheckInTask");
        result += jobUtil.addJob(jobRecord, jobRecord.getCloseCronExpression());
        return new CommonObjReturn(result);
    }
}
