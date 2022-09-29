package com.ynusmartgrid.face_.common.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ynusmartgrid.face_.app.entity.Face;
import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.ynusmartgrid.face_.app.service.IFaceCaptureRecordService;
import com.ynusmartgrid.face_.app.service.IFaceInterfaceService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wjs on 2022/05/12
 */
@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatisticAttendanceByDay implements Job {

    @Autowired
    IFaceCaptureRecordService faceCaptureRecordServiceImpl;

    @Autowired
    IFaceInterfaceService faceInterfaceServiceImpl;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.MIN);
        LocalDateTime endTime = startTime.plusDays(1L);
        QueryWrapper<FaceCaptureRecord> fcrQuery = new QueryWrapper<>();
        fcrQuery.select("face_id")
                .eq("is_stranger",0)
                .apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')")
                .apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')")
                .groupBy("face_id");
        List<String> faceIds = faceCaptureRecordServiceImpl.list(fcrQuery).stream().map(n->n.getFaceId()).collect(Collectors.toList());
        if(faceIds.size() < 1){
            log.info("============异常！无人出勤。============");
            return;
        }
        UpdateWrapper<Face> faceUpdate = new UpdateWrapper<>();
        faceUpdate.setSql("attendance = attendance + 1")
                .in("system_identity", faceIds);
        faceInterfaceServiceImpl.update(faceUpdate);
        log.info("============每日到勤已添加============");

    }
}
