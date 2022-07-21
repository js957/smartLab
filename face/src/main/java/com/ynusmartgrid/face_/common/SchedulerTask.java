package com.ynusmartgrid.face_.common;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sun.jna.NativeLong;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.hktv.ClientDemo.HCNetSDK;
import com.ynusmartgrid.face_.pojo.PersonListParam;
import com.ynusmartgrid.face_.pojo.Monitor;
import com.ynusmartgrid.face_.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import static com.ynusmartgrid.face_.hktv.ClientDemo.ClientDemo.hCNetSDK;
import static com.ynusmartgrid.face_.util.IoUtil.downloadImage;


/**
 * Created by wjs on 2021/09/25
 * 定时任务类，建议定时任务都写在这，如果需要的定时任务不多的话
 */
@Component
public class SchedulerTask {

    //制定的目录
//    File file = new File("");

    //@Autowired
    private List<Person> personnelList;

    //@Scheduled(cron = Constant.SCHEDULER_JOB_CRON)
    public void getPersons() throws Exception {
        try {
            File file=new File("C:\\1.7\\D\\face");//里面输入特定目录
            File temp=null;
            File[] filelist= file.listFiles();
            for(int i=0;i<filelist.length;i++) {
                temp=filelist[i];
                if(temp.getName().endsWith(".log") || temp.getName().endsWith(".mdmp"))//获得文件名，如果后缀为“”，这个你自己写，就删除文件
                {
                    temp.delete();//删除文件}
                }
            }
//            File temp = null;
//            File[] fileList = file.listFiles();
//            for(int i = 0 ; i<fileList.length;i++){
//                temp = fileList[i];
//                if(temp.getName().endsWith("log")){
//                    temp.delete();
//                }
//            }
            /*
             * 请求数据并更新内存中人员列表的数据
             */
//            String json = sendGet(Constant.GET_PERSONS_URL);
            String json = HttpUtil.get(Constant.GET_PERSONS_URL, CharsetUtil.CHARSET_UTF_8);
            JSONObject jsonObject = JSONUtil.parseObj(json);
            PersonListParam getResult = jsonObject.toBean(PersonListParam.class);
            if (getResult.getErrCode() == 0) {
                //判断是否有新数据，若有则插入
                List<Person> personList = getResult.getData();
                personnelList.retainAll(personList); //删除新获取的数据中已被删除的成员
                personList.removeAll(personnelList); //获取新获取的数据中新增的成员

                if (personList.size() <= 0) {
                    // 若新数据中没有新增则返回
                    return;
                }
                // 添加新成员并下载图片，修改集合中该对象的图片地址为本地
                for (Person person : personList) {
                    String newPath = downloadImage(person.getPicUrl());
                    person.setPicUrl(newPath);
                }
                personnelList.addAll(personList);
            }
        } catch (Exception e) {
            System.out.println("同步人脸库错误");
            e.printStackTrace();
        }

    }

    //@Scheduled(cron = Constant.CHECK_MONITOR_ERROR_CRON)
    public void checkMonitor() throws UnknownHostException {
        String urlStr = Constant.GET_MONITORS_URL;
        String jsonStr = null;
        HCNetSDK sdk = HCNetSDK.INSTANCE;
        InetAddress address = InetAddress.getLocalHost();
        try {
            // 获取摄像头设备信息，json格式
            jsonStr = HttpClientHelper.sendGet(urlStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<Monitor> monitorList = jsonArray.toJavaList(Monitor.class);
        for (Monitor monitor : monitorList) {
            //设备初始化
            boolean initSuc = sdk.NET_DVR_Init();
            if (initSuc != true) {
                JOptionPane.showMessageDialog(null, "初始化失败");
                System.out.println("设备" + monitor.getID() + "初始化失败");
            }
            // 获取设备信息
            HCNetSDK.NET_DVR_DEVICEINFO_V30 net_dvr_deviceinfo_v30 = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
            NativeLong userid = new NativeLong();
            userid = hCNetSDK.NET_DVR_Login_V30(monitor.getMonitorIp(),
                    (short) 8000, monitor.getMonitorUserName(), monitor.getMonitorPassword(), net_dvr_deviceinfo_v30);
            monitor.setUserid(userid);
            if (monitor.getUserid().intValue() < 0) {
                HashMap<String, String> bodyMap = new HashMap<>();
               // bodyMap.put("areaCode","1002");
                bodyMap.put("serverIP", address.getHostAddress());
                bodyMap.put("monitorIP", monitor.getMonitorIp());
                bodyMap.put("errorReason", "未能获取设备登入id");
                bodyMap.put("nodeCode", String.valueOf(hCNetSDK.NET_DVR_GetLastError()));
                bodyMap.put("areaCode","1002");
                HttpRequest httpRequest = HttpRequest.post(Constant.POST_MONITOR_ABNORMAL_FEEDBACK)
                        .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                        .formStr(bodyMap);
                String req = httpRequest
                        .execute()
                        .body();
            }
        }
    }

    // 统计每10分钟

}
