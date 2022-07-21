package com.ynusmartgrid.face_.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *  用于对获取或操作与摄像头相关的数据
 */
@RestController(value = "monitor")
public class MonitorController {

    @Autowired
    private Map<Integer, String> capturePictureMap;

    @GetMapping("getPictureMap")
    public Map<Integer, String> getPictureMap(){
        return capturePictureMap;
    }

}
