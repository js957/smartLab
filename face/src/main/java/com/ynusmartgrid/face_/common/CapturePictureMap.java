package com.ynusmartgrid.face_.common;

import com.ynusmartgrid.face_.pojo.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例的用于临时储存截取图片的map
 * {"monitorId":"base64ImageStr"}
 */
@Configuration
public class CapturePictureMap {

    private volatile static Map<Integer, String> capturePictureMap;
//    private volatile static Map<Integer, String> messageData;
 
    private CapturePictureMap(){}
    @Bean(name = "capturePictureMap")
    public static Map<Integer, String> getCapturePictureMap(){
        if(capturePictureMap == null) {
            synchronized (CapturePictureMap.class) {
                if (capturePictureMap == null) {
                    capturePictureMap = new ConcurrentHashMap<>();
                }
            }
        }
        return capturePictureMap;
    }

    public static void setCapturePictureMap(Integer monitorId, String strBase64Image){
        capturePictureMap.put(monitorId, strBase64Image);
    }
}
