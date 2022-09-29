package com.ynusmartgrid.face_.app;

import com.ynusmartgrid.face_.FaceApplication;
import com.ynusmartgrid.face_.app.controller.JobRecordController;
import com.ynusmartgrid.face_.app.entity.JobRecord;
import com.ynusmartgrid.face_.util.JobUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * Created by wjs on 2022/04/16
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FaceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestScheduler {

    @Autowired
    JobUtil jobUtil;

    @Test
    public void startTask() throws Exception {
//        JobRecord jobRecord = new JobRecord();
//        jobRecord.setJobClassName("StartTheCheckInTask");
//        jobRecord.setJobId(1L);
//        jobRecord.setCreateTime(LocalDateTime.now());
//        jobRecord.setJobName("checkIn1");
//        jobRecord.setJobGroup("checkInGroup");
//        jobRecord.setStartTime(LocalDateTime.now());
//        jobRecord.setEndTime(LocalDateTime.now().plusMinutes(2L));
//        jobRecord.setCronExpression("*/10 * * * * ?");
//        jobRecord.setCloseCronExpression("*/15 * * * * ?");
//        jobRecord.setInvokeParam("{data:{data1:3}}");
//        jobUtil.addJob(jobRecord, jobRecord.getCronExpression());
//        jobRecord.setJobGroup("closeCheckInGroup");
//        jobUtil.addJob(jobRecord, jobRecord.getCloseCronExpression());
        System.out.println(LocalDateTime.now());

    }
}
