package com.ynusmartgrid.face_.common.job;

import com.ynusmartgrid.face_.app.entity.PersonNumOfRoom;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IPersonNumOfRoomService;
import com.ynusmartgrid.face_.app.service.IStatisticRecodeService;
import com.ynusmartgrid.face_.common.MaxNumBerOfRoomList;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by wjs on 2022/04/24
 */

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatisticNumsOfRoom implements Job {


    @Autowired
    IStatisticRecodeService statisticRecodeServiceImpl;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LocalDateTime now = LocalDateTime.now();
        List<StatisticRecode> numberOfRoomList = MaxNumBerOfRoomList.getMaxNumBerOfRoomList();
        log.info("============插入统计人数信息============");
        if(numberOfRoomList.size()<1) return;
        numberOfRoomList.stream().forEach(n->n.setGmtCreate(now));
        statisticRecodeServiceImpl.saveBatch(numberOfRoomList);
        log.info("插入数据为:"+numberOfRoomList);
        MaxNumBerOfRoomList.doEmpty();
    }
}
