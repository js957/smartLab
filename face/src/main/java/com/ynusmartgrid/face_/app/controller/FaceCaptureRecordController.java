package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ynusmartgrid.face_.app.entity.BehaviorRecognitionRecord;
import com.ynusmartgrid.face_.app.entity.Equipment;
import com.ynusmartgrid.face_.app.entity.Face;
import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.ynusmartgrid.face_.app.service.IFaceCaptureRecordService;
import com.ynusmartgrid.face_.app.service.IFaceInterfaceService;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import com.ynusmartgrid.face_.util.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@RestController
@RequestMapping("/app/faceCaptureRecord")
public class FaceCaptureRecordController {

    @Autowired
    IFaceCaptureRecordService faceCaptureRecordServiceImpl;

    @Autowired
    IFaceInterfaceService faceInterfaceServiceImpl;

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:28
     * 根据id查询特定记录
     * 根据分页以及(人脸faceId，设备equipmentId，置信度confidence，描述description，时间gmtCreate)查找记录
     */
    @PostMapping("/getFaceCaptureRecord")
    public CommonObjReturn getFaceCaptureRecord(@RequestBody HashMap<String, Object> objMap) {
        if (StrUtil.isNotBlank(objMap.get("id").toString())) {
            return new CommonObjReturn(faceCaptureRecordServiceImpl.getById(objMap.get("id").toString()));
        }
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<FaceCaptureRecord> iPage = new Page<>(pageIndex, pageSize);
        QueryWrapper<FaceCaptureRecord> fcrQuery = new QueryWrapper<>();
        fcrQuery.orderByDesc("id");
        if (StrUtil.isNotBlank(objMap.get("faceId").toString())) {
            fcrQuery.eq("face_id", objMap.get("faceId"));
        }
        if (StrUtil.isNotBlank(objMap.get("faceName").toString())) {
            fcrQuery.eq("face_name", objMap.get("faceName"));
        }
        if (StrUtil.isNotBlank(objMap.get("equipmentIp").toString())) {
            fcrQuery.eq("equipment_ip", objMap.get("equipmentIp"));
        }
        if (StrUtil.isNotBlank(objMap.get("confidence").toString())) {
            fcrQuery.ge("confidence", objMap.get("confidence"));
        }
        if (StrUtil.isNotBlank(objMap.get("description").toString())) {
            fcrQuery.like("description", objMap.get("description"));
        }
        if (StrUtil.isNotBlank(objMap.get("isStranger").toString())) {
            fcrQuery.eq("is_stranger", objMap.get("isStranger"));
        }
        if (StrUtil.isNotBlank(objMap.get("address").toString())) {
            fcrQuery.eq("address", objMap.get("address"));
        }
        if (StrUtil.isNotBlank(objMap.get("gmtCreate").toString())) {
            // 查当天
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("gmtCreate").toString(), df);
            LocalDateTime endTime = startTime.plusDays(1L);
            fcrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            fcrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(faceCaptureRecordServiceImpl.page(iPage, fcrQuery));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:37
     * 根据传入的设备信息创建设备
     */
    @PostMapping("/saveFaceCaptureRecord")
    public CommonObjReturn addFaceCaptureRecord(@Validated(value = {FaceCaptureRecord.Add.class}) @RequestBody FaceCaptureRecord faceCaptureRecord) {
        // 如果是陌生人直接存
        String facePath;
        if (faceCaptureRecord.getIsStranger()) {
            facePath = "stranger" + FileUtil.FILE_SEPARATOR + faceCaptureRecord.getFaceId();
        } else {
            // 查询上一条数据
            QueryWrapper<FaceCaptureRecord> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("face_id", faceCaptureRecord.getFaceId());
            queryWrapper.orderByDesc("gmt_create").last("limit 1");
            FaceCaptureRecord theLastRecord = faceCaptureRecordServiceImpl.getOne(queryWrapper);
            // 在同一个房间并且时间相隔小于一小时不存
            // 在同一个房间并且时间相隔大于一小时存
            // 不在同一个房间 存
            if (theLastRecord != null && theLastRecord.getAddress().equals(faceCaptureRecord.getAddress())
                    && theLastRecord.getGmtCreate().getHour() == faceCaptureRecord.getGmtCreate().getHour()) {
                return new CommonObjReturn(true);
            }
            facePath = faceCaptureRecord.getFaceId();
        }
        StringBuffer parentDirPath = new StringBuffer();
        parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                .append("faceCapture")
                .append(FileUtil.FILE_SEPARATOR)
                .append(facePath)
                .append(FileUtil.FILE_SEPARATOR);
        faceCaptureRecord.setFace(IoUtil.copyToLocal(faceCaptureRecord.getFace(), parentDirPath.toString()));
        return new CommonObjReturn(faceCaptureRecordServiceImpl.saveOrUpdate(faceCaptureRecord));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:37
     * 根据id删除人脸记录
     */
    @PostMapping("/removeFaceCaptureRecord")
    public CommonObjReturn deleteFaceCaptureRecord(@RequestBody Long id) {
        return new CommonObjReturn(faceCaptureRecordServiceImpl.removeById(id));
    }


    /**
     * @Param:
     * @Author: wjs
     * @date: 20:52
     * 获取人员轨迹
     * 页号pageIndex，页大小pageSize
     */
    @PostMapping("/getPersonnelTrack")
    public CommonObjReturn getPersonnelTrack(@RequestBody HashMap<String, Object> objMap) {
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<Face> page = new Page<>(pageIndex, pageSize);
        List<Long> faceIds = faceInterfaceServiceImpl.page(page).getRecords().stream().map(Face::getId).collect(Collectors.toList());
        QueryWrapper<FaceCaptureRecord> brrQuery = new QueryWrapper<>();
        brrQuery.orderByAsc("gmt_create");
        brrQuery.in("face_id", faceIds);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        brrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
        brrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        List<FaceCaptureRecord> faceCaptureRecordList = faceCaptureRecordServiceImpl.list(brrQuery);
        Map<String, List<FaceCaptureRecord>> fcrListMap = faceCaptureRecordList.stream().collect(Collectors.groupingBy(FaceCaptureRecord::getFaceName));
        return new CommonObjReturn(fcrListMap);
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:26
     * 获取单人的历史轨迹
     * faceId,startTime,endTime
     */
    @PostMapping("/getPersonnelTrackByFace")
    public CommonObjReturn getPersonnelTrackByFace(@RequestBody HashMap<String, Object> objMap) {
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        if (StrUtil.isBlankIfStr(objMap.get("faceId"))) {
            return new CommonObjReturn<Object>("查询对象faceId不允许为空不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<FaceCaptureRecord> page = new Page<>(pageIndex, pageSize);
        QueryWrapper<FaceCaptureRecord> brrQuery = new QueryWrapper<>();
        brrQuery.orderByAsc("gmt_create");
        brrQuery.eq("face_id", objMap.get("faceId"));
        if (StrUtil.isNotBlank(objMap.get("startTime").toString()) && StrUtil.isNotBlank(objMap.get("endTime").toString())) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("startTime").toString(), df);
            LocalDateTime endTime = LocalDateTime.parse(objMap.get("endTime").toString(), df);
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        } else {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            brrQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(faceCaptureRecordServiceImpl.page(page, brrQuery));
    }


}
