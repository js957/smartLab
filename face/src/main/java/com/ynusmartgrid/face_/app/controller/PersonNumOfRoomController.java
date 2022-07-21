package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.PersonNumOfRoom;
import com.ynusmartgrid.face_.app.service.IPersonNumOfRoomService;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-04-24
 */
@RestController
@RequestMapping("/app/personNumOfRoom")
public class PersonNumOfRoomController {

    @Autowired
    IPersonNumOfRoomService personNumOfRoomServiceImpl;

    @PostMapping("/getHistoricalOfRoom")
    public CommonObjReturn getHistoricalOfRoom(@RequestBody HashMap<String,Object> objMap){
        QueryWrapper<PersonNumOfRoom> pnorQuery = new QueryWrapper<>();
        Integer numOfRecord = 3;
        // 需要进行总结
        if(StrUtil.isNotBlank(objMap.get("numOfRecord").toString())){
            numOfRecord = Integer.parseInt(objMap.get("numOfRecord").toString());
        }
        pnorQuery.last("limit " + numOfRecord);
        if(StrUtil.isNotBlank(objMap.get("requestSumUp").toString())){
            if(BooleanUtil.toBoolean(objMap.get("requestSumUp").toString())){
                pnorQuery.select("SUM(num_of_person) as total","gmt_create");
                pnorQuery.groupBy("gmt_create");
                pnorQuery.orderByDesc("gmt_create");
                return new CommonObjReturn(personNumOfRoomServiceImpl.listMaps(pnorQuery));
            }
        }
        if(StrUtil.isNotBlank(objMap.get("room").toString())){
            pnorQuery.eq("room",objMap.get("room").toString());
        }
        if(StrUtil.isNotBlank(objMap.get("startTime").toString()) && StrUtil.isNotBlank(objMap.get("endTime").toString())){
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("startTime").toString(), df);
            LocalDateTime endTime = LocalDateTime.parse(objMap.get("endTime").toString(), df);
            pnorQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            pnorQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(personNumOfRoomServiceImpl.list(pnorQuery));
    }
}
