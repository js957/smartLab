package com.ynusmartgrid.face_.util;


import cn.hutool.json.JSONObject;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.ynusmartgrid.face_.common.CapturePictureMap;
import com.ynusmartgrid.face_.hktv.ClientDemo.HCNetSDK;
import com.ynusmartgrid.face_.pojo.Monitor;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import com.ynusmartgrid.face_.common.CapturePictureMap;

import static com.ynusmartgrid.face_.hktv.ClientDemo.ClientDemo.hCNetSDK;

/**
 * 线程操作类，用于根据传入的摄像头信息操作摄像头捕获摄像头图片
 * 并放入静态变量capturePictureMap(单例)中
 */


public class RunGetPictureThread implements Runnable {


    private Monitor monitor;
    private HCNetSDK.NET_DVR_JPEGPARA net_dvr_jpegpara;
    private RabbitMQ rabbitMQ;
    private IntByReference lpSizeReturned;

    public RunGetPictureThread(Monitor monitor, HCNetSDK.NET_DVR_JPEGPARA net_dvr_jpegpara, RabbitMQ rabbitMQ, IntByReference lpSizeReturned) {
        this.monitor = monitor;
        this.net_dvr_jpegpara = net_dvr_jpegpara;
        this.rabbitMQ = rabbitMQ;
        this.lpSizeReturned = lpSizeReturned;
    }


    @Override
    public void run() {

        long startTime = System.currentTimeMillis();
        HCNetSDK sdk = HCNetSDK.INSTANCE;
        // 截取图片结果
        boolean captureSuccess = Boolean.FALSE;

        // 频繁的登陆会导致海康威视摄像头拒绝访问，所以这里只需要登陆一次就行
        if(monitor.getUserid() == null || monitor.getUserid().intValue() < 0) {
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
//            if (monitor.getUserid().intValue() < 0) {
//                System.out.println("设备" + monitor.getID() + "注册失败|网络通讯库错误码："+hCNetSDK.NET_DVR_GetLastError());
//            }
//            //DVR工作状态
//            HCNetSDK.NET_DVR_WORKSTATE_V30 devwork = new HCNetSDK.NET_DVR_WORKSTATE_V30();
//            HCNetSDK.NET_DVR_IPPARACFG ipcfg = new HCNetSDK.NET_DVR_IPPARACFG();//IP接入配置结构
//            ipcfg.write();
//            if (!sdk.NET_DVR_GetDVRWorkState_V30(monitor.getUserid(), devwork)) {
//                // 返回Boolean值，判断是否获取设备能力
//                System.out.println("返回设备状态失败");
//            }
//            //获取相关参数配置
//            sdk.NET_DVR_GetDVRConfig(monitor.getUserid(), HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0),
//                    monitor.getLastPicture(), ipcfg.size(), lpSizeReturned);
//            ipcfg.read();
//            System.out.print("IP地址:" + monitor.getMonitorIp());
//            System.out.println("|设备状态：" + devwork.dwDeviceStatic);// 0正常，1CPU占用率过高，2硬件错误，3未知
//            //System.out.println("ChanNum"+devinfo.byChanNum);
//            // 显示模拟通道
//            for (int i = 0; i < net_dvr_deviceinfo_v30.byChanNum; i++) {
//                System.out.print("Camera" + i + 1);// 模拟通道号名称
//                System.out.print("|是否录像:" + devwork.struChanStatic[i].byRecordStatic);// 0不录像，不录像
//                System.out.print("|信号状态:" + devwork.struChanStatic[i].bySignalStatic);// 0正常，1信号丢失
//                System.out.println("|硬件状态:" + devwork.struChanStatic[i].byHardwareStatic);// 0正常，1异常
//            }
        }
        Memory pointer = monitor.getLastPicture();

        byte[] bytes = null;
        // 存儲文件夾地址
        File file = new File("C:\\wyw\\image\\"+monitor.getID());
        if(!file.exists()){
            file.mkdir();
        }
        // 组织数据，为了放入消息队列
        Map<String, String> messageData = new HashMap<>();

        //


        // 截图放置在变量pointer中
        captureSuccess = sdk.NET_DVR_CaptureJPEGPicture_NEW(monitor.getUserid(), new NativeLong(1), net_dvr_jpegpara, pointer, 1280 * 720, lpSizeReturned);
//        System.out.println("设备" + monitor.getID() + "照片捕捉：" + captureSuccess);
        // 既爲賦予圖片名稱又為排除頻繁錯誤設備

        if (captureSuccess) {
            // 从pointer中获取字节数组格式的图像
            bytes = pointer.getByteArray(0, lpSizeReturned.getValue());

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = new BufferedImage(1280,720,BufferedImage.TYPE_3BYTE_BGR);
            try {
                synchronized (monitor) {
                    monitor.setNumOfExecutions(monitor.getNumOfExecutions() + 1);
                    if (monitor.getNumOfExecutions() >= 100) {
                        monitor.setNumOfExecutions(0);
                        monitor.setNumOfErrors(0);
                    }
                    bufferedImage.getGraphics().drawImage(ImageIO.read(byteArrayInputStream), 0, 0, 1280, 720, null);
                    File f1 = new File("C:\\wyw\\image\\"
                            + monitor.getID() + "\\"
                            + monitor.getID() + "_"
                            + monitor.getNumOfExecutions() % 20
                            + ".jpg");

                    ImageIO.write(bufferedImage, "jpg", f1);
//                    Thread.sleep(new Long(500));
                }
            } catch (IOException e) {
                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
            //String strBase64Image = Base64.getEncoder().encodeToString(bytes);

            // 使用CapturePictureMap的静态方法为静态的单例map赋值，将id和图片数据传入MQ
            //CapturePictureMap.setCapturePictureMap(monitor.getID(), strBase64Image);
//            // 传入消息队列
//            messageData.put(monitor.getID()+"", strBase64Image);
//            try {
//                rabbitMQ.sendFanoutMessage(messageData);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (TimeoutException e) {
//                e.printStackTrace();
//            }
//            rabbitMQ.sendFanoutMessagT(messageData);
//            System.out.println("設備：" + monitor.getID() + ",截圖花費時間為：" + (System.currentTimeMillis() - startTime));
        } else {
            // 为排除频繁访问出错的设备
            monitor.setNumOfErrors(monitor.getNumOfErrors() + 1);
        }


        //一般来说每次截图都要注销申请的用户名的，但我们需要实时的截图所以不需要logout
//        sdk.NET_DVR_Logout(monitor.getUserid());
//        sdk.NET_DVR_Cleanup();
    }


}
