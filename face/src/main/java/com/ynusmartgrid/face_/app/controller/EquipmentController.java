package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ynusmartgrid.face_.app.entity.Equipment;
import com.ynusmartgrid.face_.app.service.IEquipmentService;
import com.ynusmartgrid.face_.app.service.impl.EquipmentServiceImpl;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@RestController
@RequestMapping("/app/equipment")
public class EquipmentController {

    @Autowired
    IEquipmentService equipmentServiceImpl;

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:28
     * 根据id查询特定设备
     * 根据分页以及(设备名name,设备地址(address),描述description,时间gmtCreate)查找设备
    */
    @PostMapping("/getEquipment")
    public CommonObjReturn getEquipment(@RequestBody HashMap<String,Object> objMap){
        if(StrUtil.isNotBlank(objMap.get("id").toString())){
            return new CommonObjReturn(equipmentServiceImpl.getById(objMap.get("id").toString()));
        }
        // 必须分页
        if(StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))){
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<Equipment> iPage = new Page<>(pageIndex,pageSize);
        QueryWrapper<Equipment> equipQuery = new QueryWrapper<>();
        if(StrUtil.isNotBlank(objMap.get("address").toString())){
            equipQuery.like("address",objMap.get("address"));
        }
        if(StrUtil.isNotBlank(objMap.get("description").toString())){
            equipQuery.like("description",objMap.get("description"));
        }
        if(StrUtil.isNotBlank(objMap.get("gmtCreate").toString())){
            // 查当天
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startTime = LocalDateTime.parse(objMap.get("gmtCreate").toString(),df);
            LocalDateTime endTime = startTime.plusDays(1L);
            equipQuery.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            equipQuery.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
        }
        return new CommonObjReturn(equipmentServiceImpl.page(iPage,equipQuery));
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:37
     * 根据传入的设备信息创建设备
    */
    @PostMapping("/saveEquipment")
    public CommonObjReturn addEquipment(@Validated(value = {Equipment.Add.class}) @RequestBody Equipment equipment){
        return new CommonObjReturn(equipmentServiceImpl.saveOrUpdate(equipment));
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:37
     * 根据id删除设备
    */
    @PostMapping("/removeEquipment")
    public CommonObjReturn deleteEquipment(@RequestBody String id){
        Equipment equipment = equipmentServiceImpl.getById(id);
        boolean result = false;
        if(equipment != null){
            result = equipmentServiceImpl.removeById(id);
        }else {
            return new CommonObjReturn(result, "该记录不存在",Constant.RS_SUCCESS);
        }
        return new CommonObjReturn(result);
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:37
     * 根据传入的设备信息进行修改
    */
    @PostMapping("/modifyEquipment")
    public CommonObjReturn modifyEquipment(@Validated(value = {Equipment.Modify.class})@RequestBody Equipment equipment){
        return new CommonObjReturn(equipmentServiceImpl.updateById(equipment));
    }
}
