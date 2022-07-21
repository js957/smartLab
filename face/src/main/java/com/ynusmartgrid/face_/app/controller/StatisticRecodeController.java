package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.PersonNumOfRoom;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IStatisticRecodeService;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-05-10
 * 统计记录
 */
@RestController
@RequestMapping("/app/statisticRecode")
public class StatisticRecodeController {

    @Autowired
    IStatisticRecodeService statisticRecodeServiceImpl;

    /**
    *@Param:
    *@Author: wjs
    *@date: 9:58
     * numOfRecord:获取的数量，requestSumUp:是否需要加总，room:房间号，startTime:开始时间，endTime:结束时间
    */
    @PostMapping("/getHistoricalOfRoom")
    public CommonObjReturn getHistoricalOfRoom(@RequestBody HashMap<String, Object> objMap) {
        QueryWrapper<StatisticRecode> pnorQuery = new QueryWrapper<>();
        // 因为已经明确这个接口用于获取
        pnorQuery.eq("recode_type", 0);
        Integer numOfRecord = 3;
        // 需要进行总结
        if (StrUtil.isNotBlank(objMap.get("numOfRecord").toString())) {
            numOfRecord = Integer.parseInt(objMap.get("numOfRecord").toString());
        }
        pnorQuery.last("limit " + numOfRecord);
        // 获取某个时间点的总数(时间点：比如三个房间在某个时刻的人数，在此加总)
        if (StrUtil.isNotBlank(objMap.get("requestSumUp").toString())) {
            if (BooleanUtil.toBoolean(objMap.get("requestSumUp").toString())) {
                pnorQuery.select("SUM(statistic_info_recode) as total", "gmt_create");
                pnorQuery.groupBy("gmt_create");
                pnorQuery.orderByDesc("gmt_create");
                return new CommonObjReturn(statisticRecodeServiceImpl.listMaps(pnorQuery));
            }
        }
        if (StrUtil.isNotBlank(objMap.get("room").toString())) {
            pnorQuery.eq("group_name", objMap.get("room").toString());
        }
        if (StrUtil.isNotBlank(objMap.get("startTime").toString()) && StrUtil.isNotBlank(objMap.get("endTime").toString())) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("startTime").toString(), df);
            LocalDateTime endTime = LocalDateTime.parse(objMap.get("endTime").toString(), df);
            pnorQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            pnorQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(statisticRecodeServiceImpl.list(pnorQuery));
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:59
     * 获取统计数据
    */
    @PostMapping("/getStatisticRecode")
    public CommonObjReturn getStatisticRecode(@RequestBody HashMap<String, Object> objMap){
        QueryWrapper<StatisticRecode> staQuery = new QueryWrapper<>();
        staQuery.orderByDesc("gmt_create");
        if(StrUtil.isNotBlank(objMap.get("groupId").toString())){
            staQuery.eq("group_id", objMap.get("groupId").toString());
        }
        if(StrUtil.isNotBlank(objMap.get("recodeType").toString())){
            staQuery.eq("recode_type", objMap.get("recodeType").toString());
        }
        return new CommonObjReturn(statisticRecodeServiceImpl.getOne(staQuery));
    }
}
