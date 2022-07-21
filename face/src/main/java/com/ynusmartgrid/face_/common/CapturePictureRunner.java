package com.ynusmartgrid.face_.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.hktv.ClientDemo.HCNetSDK;
import com.ynusmartgrid.face_.pojo.Monitor;
import com.ynusmartgrid.face_.util.RabbitMQ;
import com.ynusmartgrid.face_.util.RunGetPictureThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import static com.ynusmartgrid.face_.hktv.ClientDemo.ClientDemo.hCNetSDK;

/**
 * 服务启动时自动运行，请求摄像头设备信息并开启线程池截取图片
 * 默认线程池大小为设备数量(存在隐患，之后修改)
 */
//@Service
public class CapturePictureRunner implements CommandLineRunner {


	private static final Logger LOGGER = LoggerFactory.getLogger(CapturePictureRunner.class);
    //@Autowired
    RabbitMQ rabbitMQ;

    @Override
    public void run(String... args) throws Exception {
        // 用于自适应的根据cpu核心线程数创建线程池
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 请求设备列表
        String urlStr = Constant.GET_MONITORS_URL;
        String jsonStr = null;
        try {
            // 获取摄像头设备信息，json格式
            jsonStr = HttpClientHelper.sendGet(urlStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(jsonStr);
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<Monitor> monitorList = jsonArray.toJavaList(Monitor.class);
        // 批量选择获取截图的摄像头
//        monitorList=monitorList.subList(1,monitorList.size());

        System.out.println(monitorList);
        // 可复用参数集中存放，避免过多创建导致GC频繁回收导致进程切换的开销
        //截取图片的参数
        HCNetSDK.NET_DVR_JPEGPARA net_dvr_jpegpara = new HCNetSDK.NET_DVR_JPEGPARA();
        net_dvr_jpegpara.wPicSize = 5; // 5-HD720P(1280*720)
        net_dvr_jpegpara.wPicQuality = 0; // 0-最好，1-较好，2-一般

        //返回的大小(接口定义的，仅用于接收暂时没有更多操作)
        IntByReference lpSizeReturned = new IntByReference();
        lpSizeReturned.setValue(0);


        ThreadPoolTaskExecutor threadPoolTaskExecutorForCapturePicture = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutorForCapturePicture.setCorePoolSize(0);
        threadPoolTaskExecutorForCapturePicture.setMaxPoolSize(availableProcessors);
        threadPoolTaskExecutorForCapturePicture.setQueueCapacity(monitorList.size() );
        threadPoolTaskExecutorForCapturePicture.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (!executor.isShutdown()) {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        threadPoolTaskExecutorForCapturePicture.initialize();
        // ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(monitorList.size());
        List<Monitor> waitToDelete = new ArrayList<>();
        //int i = 0;
        while (true) {
            //System.out.println("本轮剩余线程数为：" + monitorList.size() + "/41-------------------------");
            for (Monitor monitor : monitorList) {
//                if (monitor.getNumOfExecutions() >= 30) {
//                    if (monitor.getNumOfErrors() / monitor.getNumOfExecutions() >= 0.4) {
//                        System.out.println("設備" + monitor.getID() + "產生錯誤過多，已移除");
//                        waitToDelete.add(monitor);
//                        continue;
//                    }
//                }

                if (monitor.getLastPicture() == Pointer.NULL) {
                    // 事先申请存放图片的内存，通过覆盖的方式复用避免内存溢出
                    monitor.setLastPicture(new Memory(1280 * 720));
                }

                //需考虑对象线程同步
                RunGetPictureThread runGetPictureThread = new RunGetPictureThread(monitor, net_dvr_jpegpara, rabbitMQ, lpSizeReturned);
                threadPoolTaskExecutorForCapturePicture.execute(runGetPictureThread);
            }

//            for (Monitor monitor : waitToDelete) {
//                monitorList.remove(monitor);
//                if (monitorList.size() < 32) {
//                    System.out.println("閾值過大");
//                }
//            }
            waitToDelete.clear();
        }
    }
}
