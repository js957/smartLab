package com.ynusmartgrid.face_.app;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.FaceApplication;
import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.ynusmartgrid.face_.app.entity.MemberGroup;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IFaceCaptureRecordService;
import com.ynusmartgrid.face_.app.service.IMemberGroupService;
import com.ynusmartgrid.face_.app.service.IStatisticRecodeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Created by wjs on 2022/07/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FaceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class testStasticRecordGenerate {


    @Autowired
    IFaceCaptureRecordService faceCaptureRecordServiceImpl;

    @Autowired
    IStatisticRecodeService statisticRecodeServiceImpl;

    @Autowired
    IMemberGroupService memberGroupServiceImpl;

    @Test
    public void taskRun() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss");
        MemberGroup memberGroup = memberGroupServiceImpl.getById(1);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Map<String, Object>> resultList = faceCaptureRecordServiceImpl.listMaps(
                new QueryWrapper<FaceCaptureRecord>()
                        .select("face_id as faceId", "face_name as faceName", "MIN(gmt_create) as appearTime, day")
                        .eq("is_stranger", false) //非陌生人
                        .inSql("face_id", "select face_id from face_group_belong where group_id=" + memberGroup.getGroupId()) //组内成员
                        .apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + LocalDateTime.of(LocalDate.of(2022,04,24), LocalTime.MIN).format(df) + "')")
                        .apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + LocalDateTime.of(2022,04,24,23,59).format(df) + "')") // 当天
                        .groupBy("face_id", "face_name", "day"));

        StatisticRecode statisticRecode = new StatisticRecode(memberGroup.getGroupId(), memberGroup.getGroupName(), JSONUtil.toJsonStr(resultList), 4);
        statisticRecode.setGmtCreate(LocalDateTime.of(2022,04,24,23,59));
        statisticRecodeServiceImpl.save(statisticRecode);
    }
}
