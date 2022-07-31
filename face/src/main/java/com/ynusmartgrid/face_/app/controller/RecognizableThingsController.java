package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ynusmartgrid.face_.app.entity.Equipment;
import com.ynusmartgrid.face_.app.entity.RecognizableThings;
import com.ynusmartgrid.face_.app.service.IRecognizableThingsService;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import com.ynusmartgrid.face_.service.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-04-19
 */
@Slf4j
@RestController
@RequestMapping("/app/recognizableThings")
public class RecognizableThingsController {

    @Autowired
    IRecognizableThingsService recognizableThingsServiceImpl;

    // 查看识别物品信息
    /**
    *@Param:
    *@Author: wjs
    *@date: 14:55
     * 查看识别物品信息id,pageIndex,pageSize,thingName
    */
    @PostMapping("/getRecThing")
    public CommonObjReturn getRecThing(@RequestBody HashMap<String, Object> objMap){
        if(StrUtil.isNotBlank(objMap.get("id").toString())){
            return new CommonObjReturn(recognizableThingsServiceImpl.getById(objMap.get("id").toString()));
        }
        // 必须分页
        if(StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))){
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<RecognizableThings> iPage = new Page<>(pageIndex,pageSize);
        QueryWrapper<RecognizableThings> recThingQuery = new QueryWrapper<>();
        if(StrUtil.isNotBlank(objMap.get("thingName").toString())){
            recThingQuery.like("thing_name",objMap.get("thingName").toString());
        }
        return new CommonObjReturn(recognizableThingsServiceImpl.page(iPage));

    }
    // 添加识别物品
    /**
    *@Param:
    *@Author: wjs
    *@date: 14:56
     * 添加识别物品
    */
    @PostMapping("saveRecThing")
    public CommonObjReturn addRecThing(@Validated(value = {RecognizableThings.Add.class})@RequestBody RecognizableThings recognizableThings){
        boolean result = recognizableThingsServiceImpl.saveOrUpdate(recognizableThings);
        WebSocketServer.sendInfo(JSON.toJSONString(recognizableThingsServiceImpl.list()),"3");
        return new CommonObjReturn(result);
    }

    // 修改识别物品的阈值
    /**
    *@Param:
    *@Author: wjs
    *@date: 14:56
     * 修改识别物品的阈值
    */
    @PostMapping("/modifyRecThing")
    public CommonObjReturn modifyRecThing(@Validated(value = {RecognizableThings.Modify.class})@RequestBody RecognizableThings recognizableThings){
        boolean result = recognizableThingsServiceImpl.updateById(recognizableThings);
        WebSocketServer.sendInfo(JSON.toJSONString(recognizableThingsServiceImpl.list()),"3");
        return new CommonObjReturn(result);
    }

    // 删除识别物品
    /**
     * @param id
     * @return
     * 删除识别物品
     */
    @PostMapping("removeRecThing")
    public CommonObjReturn deleteRecThing(@RequestBody Integer id){
        boolean result = recognizableThingsServiceImpl.removeById(id);
        WebSocketServer.sendInfo(JSON.toJSONString(recognizableThingsServiceImpl.list()),"3");
        return new CommonObjReturn(result);
    }


    @GetMapping("/testWebSocket")
    public CommonObjReturn testWebSocket(){
        RecognizableThings things = new RecognizableThings();
        things.setThingId(1L);
        things.setThingName("knife");
        things.setRecThreshold(0.5);
        things.setDeleted(false);
        WebSocketServer.sendInfo(JSON.toJSONString(things),"3");
        log.info("发送数据-》"+ JSON.toJSONString(things) + "to user 3");
        return new CommonObjReturn(Boolean.TRUE);
    }

}
