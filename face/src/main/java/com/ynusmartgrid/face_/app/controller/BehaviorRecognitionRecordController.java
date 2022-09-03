package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.BehaviorRecognitionRecord;
import com.ynusmartgrid.face_.app.entity.PersonNumOfRoom;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import com.ynusmartgrid.face_.app.service.IBehaviorRecognitionRecordService;
import com.ynusmartgrid.face_.common.MaxNumBerOfRoomList;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import com.ynusmartgrid.face_.service.WebSocketServer;
import com.ynusmartgrid.face_.util.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */

@RestController
@RequestMapping("/app/behaviorRecognitionRecord")
public class BehaviorRecognitionRecordController {

    @Autowired
    IBehaviorRecognitionRecordService behaviorRecognitionRecordServiceImpl;


    /**
     * @Param:
     * @Author: wjs
     * @date: 21:14
     * 根据id获取特定记录
     * 根据分页(pageIndex,pageSize)与特定条件(人脸faceId，设备equipmentId，时间gmtCreate，类型type，人脸置信faceConfidence，行为置信behaviorConfidence)获取分页列表
     */
    @PostMapping("/getBehaviorRecognitionRecord")
    public CommonObjReturn getBehaviorRecognitionRecord(@RequestBody HashMap<String, String> objMap) {
        if (StrUtil.isNotBlank(objMap.get("id").toString())) {
            return new CommonObjReturn(behaviorRecognitionRecordServiceImpl.getById(Long.parseLong(objMap.get("id").toString())));
        }
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        QueryWrapper<BehaviorRecognitionRecord> brrQuery = new QueryWrapper<>();
        if (StrUtil.isNotBlank(objMap.get("equipmentIp").toString())) {
            brrQuery.eq("equipment_ip", objMap.get("equipmentIp"));
        }
        if (StrUtil.isNotBlank(objMap.get("room").toString())) {
            brrQuery.eq("room", objMap.get("room"));
        }
        if (StrUtil.isNotBlank(objMap.get("gmtCreate").toString())) {
            // 查当天
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("gmtCreate").toString(), df);
            LocalDateTime endTime = startTime.plusDays(1L);
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        if (StrUtil.isNotBlank(objMap.get("type").toString())) {
            brrQuery.eq("type", objMap.get("type"));
        }
        if (StrUtil.isNotBlank(objMap.get("behaviorConfidence").toString())) {
            brrQuery.ge("behavior_confidence", objMap.get("behaviorConfidence"));
        }
        brrQuery.orderByDesc("gmt_create");
        return new CommonObjReturn(behaviorRecognitionRecordServiceImpl.selectPage(pageIndex, pageSize, brrQuery));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:22
     * 算法端请求添加记录
     */
    @PostMapping("/saveBehaviorRecognitionRecord")
    public CommonObjReturn addBehaviorRecognitionRecord(@Validated(value = {BehaviorRecognitionRecord.Add.class}) @RequestBody BehaviorRecognitionRecord behaviorRecognitionRecord) {
        // 存储图片并修改识别记录的图片地址为映射地址
        StringBuffer parentDirPath = new StringBuffer();
        parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                .append("behaviorRecodeFiles")
                .append(FileUtil.FILE_SEPARATOR)
                .append(behaviorRecognitionRecord.getDescription())
                .append(FileUtil.FILE_SEPARATOR);
        behaviorRecognitionRecord.setCaptureImg(IoUtil.copyToLocal(behaviorRecognitionRecord.getCaptureImg(),parentDirPath.toString()));
        //behaviorRecognitionRecord.setCaptureImg(IoUtil.capturedRecordCopyToLocal(behaviorRecognitionRecord));
        return new CommonObjReturn(behaviorRecognitionRecordServiceImpl.save(behaviorRecognitionRecord));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:24
     * 逻辑删除
     */
    @PostMapping("/removeBehaviorRecognitionRecord")
    public CommonObjReturn deleteBehaviorRecognitionRecord(@RequestBody Long id) {
        BehaviorRecognitionRecord behaviorRecognitionRecord = behaviorRecognitionRecordServiceImpl.getById(id);
        boolean result = false;
        if (behaviorRecognitionRecord != null) {
            result = behaviorRecognitionRecordServiceImpl.removeById(id);
        } else {
            return new CommonObjReturn(result, "该记录不存在", Constant.RS_SUCCESS);
        }
        return new CommonObjReturn(result);
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 14:19
     * 获取房间号，房间人数，时间的列表
     */
    @PostMapping("/uploadNumberOfRooms")
    public CommonObjReturn uploadNumberOfRooms(@RequestBody List<StatisticRecode> numberOfRoomList) {
        // 保存一个最大人数的列表
        MaxNumBerOfRoomList.setNumBerOfRoomList(numberOfRoomList);
        WebSocketServer.sendInfo(JSON.toJSONString(numberOfRoomList), "1");
        return new CommonObjReturn(true);
    }


}
