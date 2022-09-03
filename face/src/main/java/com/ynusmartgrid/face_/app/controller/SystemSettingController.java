package com.ynusmartgrid.face_.app.controller;


import com.ynusmartgrid.face_.app.entity.SystemSetting;
import com.ynusmartgrid.face_.app.service.ISystemSettingService;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@RestController
@RequestMapping("/app/systemSetting")
public class SystemSettingController {

    @Autowired
    ISystemSettingService systemSettingServiceImpl;

    @GetMapping
    public CommonObjReturn getSystemSetting(){
        return new CommonObjReturn(systemSettingServiceImpl.list());
    }

    @GetMapping("/{id}")
    public CommonObjReturn searchSystemSetting(@PathVariable String id){
        return new CommonObjReturn(systemSettingServiceImpl.getById(id));
    }

    @PostMapping
    public CommonObjReturn addSystemSetting(@Valid @RequestBody SystemSetting systemSetting){
        return new CommonObjReturn(systemSettingServiceImpl.save(systemSetting));
    }

    @DeleteMapping("/{id}")
    public CommonObjReturn removeSetting(@PathVariable String id){
        return new CommonObjReturn(systemSettingServiceImpl.removeById(id));
    }

    @PutMapping(value = "/{id}")
    public CommonObjReturn updateSetting(@PathVariable String id, @Valid @RequestBody SystemSetting systemSetting){
        systemSetting.setId(Long.parseLong(id));
        return new CommonObjReturn(systemSettingServiceImpl.updateById(systemSetting));
    }

}
