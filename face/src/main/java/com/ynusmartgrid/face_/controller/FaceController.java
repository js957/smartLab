package com.ynusmartgrid.face_.controller;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ErrorInfo;
import com.arcsoft.face.toolkit.ImageInfo;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.*;
import com.ynusmartgrid.face_.service.FaceService;
import com.ynusmartgrid.face_.service.ReportPersonInfoService;
import com.ynusmartgrid.face_.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import static com.arcsoft.face.toolkit.ImageFactory.getRGBData;

import java.util.Base64;
import java.util.List;


//ip：8081/face

/**
 * 人脸COntroller
 *
 * @author JiaQi
 */
@RestController
public class FaceController {
    @Autowired
    private List<Person> personnelList;
    //    @Autowired
//    private FaceService faceService;
    @Autowired
    private ReportPersonInfoService reportPersonInfoService;

    FaceService faceService = new FaceService();

    //  String monitorIP,String monitorNo,String behavior_algorithm,String companyCode,String imageData,float thresh,float reliability,String addTime,String behavior_content,List<String> target
    @PostMapping("/face")
    public PersonListParam getPersonList(@RequestBody OCParmars ocParmars) throws IOException {

        //从官网获取
        String appId = Constant.APP_ID;
        String sdkKey = Constant.SDK_KEY;


        FaceEngine faceEngine = new FaceEngine(Constant.ENGINE_DIR);
        //激活引擎
        int errorCode = faceEngine.activeOnline(appId, sdkKey);
//        System.out.println(errorCode);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }


        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);


        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }


//        System.out.println(ocParmars);
        //南方电网给的置信度，我们识别出来的要高于这个置信度
        float requiredThresh = ocParmars.getThresh();
        //reliability 上游算法得出的置信度
        // -1 上报人脸置信度  其他值 上报reliability这个置信度
        float reliability = ocParmars.getReliability();

        //  需要识别人脸的地址
        //ocParmars.getTarget() 需要检测图的地址 封装了十个角度摄像头里面的图片 用完删除
        List<String> faceUrlList = ocParmars.getTarget();
        //创建要上传的信息的实体类
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setNodeCode("1001");

        //开始进行人脸对比
        String faceUrl = null;
        Float realThresh = null;
        Person person = null;
        for (int i = 0; i < faceUrlList.size(); i++) {
            faceUrl = faceUrlList.get(i);
            for (int j = 0; j < personnelList.size(); j++) {
                float faceSimilar = 0;
                try {

                    //人脸检测
                    ImageInfo imageInfo = getRGBData(new File(faceUrl));
                    List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
                    errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
                    //特征提取
                    FaceFeature faceFeature = new FaceFeature();
                    errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);

                    //人脸检测2
                    ImageInfo imageInfo2 = getRGBData(new File(personnelList.get(j).getPicUrl()));
                    List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
                    errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2);

                    //特征提取2
                    FaceFeature faceFeature2 = new FaceFeature();
                    errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), faceFeature2);

                    //特征比对
                    FaceFeature targetFaceFeature = new FaceFeature();
                    targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
                    FaceFeature sourceFaceFeature = new FaceFeature();
                    sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
                    FaceSimilar face_Similar = new FaceSimilar();
                    errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, face_Similar);
                    faceSimilar = face_Similar.getScore();


                } catch (Exception e) {
//                    e.printStackTrace();
                }
                System.out.println("faceSimilar is "+ faceSimilar);

                if (faceSimilar >= 0.5) {
                    person = new Person();
                    person.setID(personnelList.get(j).getID());
                    person.setUserNo(personnelList.get(j).getUserNo());
                    person.setRealName(personnelList.get(j).getRealName());
                    person.setIDCard(personnelList.get(j).getIDCard());
                    realThresh = faceSimilar;
                    //找到人脸后开始封装要上传的信息 如置信度、人员编号等...
                    reportInfo.setIDCard(person.getIDCard());
                    reportInfo.setMonitorIP(ocParmars.getMonitorIP());
                    reportInfo.setBehaviorAlgorithm(ocParmars.getBehavior_algorithm());
                    reportInfo.setCompanyCode("1001");
                    reportInfo.setImageData(ocParmars.getImageData());
                    reportInfo.setPersonNo(personnelList.get(j).getUserNo() + "");
                    reportInfo.setPersonName(personnelList.get(j).getRealName());
                    reportInfo.setReliability(realThresh + "");
                    reportInfo.setNodeCode("1001");
                    reportInfo.setIDCard(personnelList.get(j).getIDCard());
                    reportInfo.setAddTime(new Date().toString());
                    reportInfo.setBehaviorContent(ocParmars.getBehavior_content());
                    System.out.println("姓名:" + person.getRealName() + "-----------》位置：" + ocParmars.getMonitorTitle() + "----------------》置信度：" + faceSimilar + "------------->行为：" + ocParmars.getBehavior_content() + "-------------->时间:" + ocParmars.getAddTime());
                }
            }
        }


        errorCode = faceEngine.unInit();
        faceEngine = null;
        engineConfiguration = null;
        System.gc();


        if (realThresh == null) {
            PersonListParam personListParam = new PersonListParam();
            personListParam.setErrCode(-1);
            personListParam.setErrMsg("未找到该人脸");
            personListParam.setData(null);

            return personListParam;
        }
        //用faceUrlList 里面的url personnelList 取置信度最高的 如果置信度比要求的大 可以结束
        //删除target图片
        faceUrlList.clear();

        //上报结果需要请求服务器，该参数为请求服务器后返回的请求结果
        PersonListParam requestResult = null;


        try {
            //TODO 此处的-1 需要于南方电网对接是否为-1还是其他值
            if (reliability != -1) {
                //上报脸置信度

                requestResult = reportPersonInfoService.Report(reportInfo, Constant.REPORT_BEHAVIOR_URL);
                System.out.println(reportInfo.getCompanyCode());
//                System.out.println("-----------上报----人脸");
            } else {
                //上报上报reliability这个置信度
                reportInfo.setReliability(reliability + "");
                requestResult = reportPersonInfoService.Report(reportInfo, Constant.REPORT_BEHAVIOR_URL);
//                System.out.println("-----------上报----算法");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //封装结果对象
        PersonListParam personListParam = new PersonListParam();
        personListParam.setErrCode(0);
        personListParam.setErrMsg("找到到该人脸， " + requestResult);
        personListParam.setData(null);
        reportInfo.setImageData("");
        System.out.println(reportInfo);

        return personListParam;
    }


    @PostMapping("/getLocation")
    public PersonListParam getPersonLocation(@RequestBody OCParmars ocParmars) throws IOException {
        String OCBuildingNo = ocParmars.getBuildingNo();
        String OCBidRoom = ocParmars.getBidRoom();
        //从官网获取
        String appId = Constant.APP_ID;
        String sdkKey = Constant.SDK_KEY;

        FaceEngine faceEngine = new FaceEngine(Constant.ENGINE_DIR);
        //激活引擎
        int errorCode = faceEngine.activeOnline(appId, sdkKey);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }


        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);


        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }

        //南方电网给的置信度，我们识别出来的要高于这个置信度
        float requiredThresh = ocParmars.getThresh();
        //reliability 上游算法得出的置信度
        // -1 上报人脸置信度  其他值 上报reliability这个置信度
        float reliability = ocParmars.getReliability();

        //  需要识别人脸的地址
        //ocParmars.getTarget() 需要检测图的地址 封装了十个角度摄像头里面的图片 用完删除
        List<String> faceUrlList = ocParmars.getTarget();
        //创建要上传的信息的实体类
        ReportInfo reportInfo = new ReportInfo();
        reportInfo.setNodeCode("1001");

        //开始进行人脸对比
        String faceUrl = null;
        Float realThresh = null;
        Person person = null;

        for (int i = 0; i < faceUrlList.size(); i++) {
            faceUrl = faceUrlList.get(i);
            for (int j = 0; j < personnelList.size(); j++) {
                float faceSimilar = 0;
                try {

                    //人脸检测
                    ImageInfo imageInfo = getRGBData(new File(faceUrl));
                    List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
                    errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
                    //特征提取
                    FaceFeature faceFeature = new FaceFeature();
                    errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);

                    //人脸检测2
                    ImageInfo imageInfo2 = getRGBData(new File(personnelList.get(j).getPicUrl()));
                    List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
                    errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2);

                    //特征提取2
                    FaceFeature faceFeature2 = new FaceFeature();
                    errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), faceFeature2);

                    //特征比对
                    FaceFeature targetFaceFeature = new FaceFeature();
                    targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
                    FaceFeature sourceFaceFeature = new FaceFeature();
                    sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
                    FaceSimilar face_Similar = new FaceSimilar();
                    errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, face_Similar);
                    faceSimilar = face_Similar.getScore();


                } catch (Exception e) {
//                    e.printStackTrace();
                }

                if (faceSimilar >= 0.5) {
                    person = new Person();
                    person.setUserNo(personnelList.get(j).getUserNo());
                    person.setID(personnelList.get(j).getID());
                    person.setRealName(personnelList.get(j).getRealName());
                    person.setIDCard(personnelList.get(j).getIDCard());
                    realThresh = faceSimilar;
                    //找到人脸后开始封装要上传的信息 如置信度、人员编号等...
                    reportInfo.setMonitorIP(ocParmars.getMonitorIP());
                    reportInfo.setBehaviorAlgorithm(ocParmars.getBehavior_algorithm());
                    reportInfo.setCompanyCode("1001");
                    reportInfo.setImageData(ocParmars.getImageData());
                    reportInfo.setPersonNo(personnelList.get(j).getUserNo() + "");
                    reportInfo.setPersonName(personnelList.get(j).getRealName());
                    reportInfo.setReliability(realThresh + "");
                    reportInfo.setAddTime(new Date().toString());
                    reportInfo.setNodeCode("1001");
                    reportInfo.setIDCard(personnelList.get(j).getIDCard());
                    reportInfo.setBehaviorContent(ocParmars.getBehavior_content());
                    System.out.println("姓名:" + person.getRealName() + "-----------》位置：" + ocParmars.getMonitorTitle() + "-----門禁-----------》置信度：" + faceSimilar + "------------->行为：" + ocParmars.getBehavior_content() + "-------------->时间:" + ocParmars.getAddTime());
                }
            }
        }


        errorCode = faceEngine.unInit();
        faceEngine = null;
        engineConfiguration = null;
        System.gc();


        if (realThresh == null) {
            System.out.println("未找到该人脸");
            PersonListParam personListParam = new PersonListParam();
            personListParam.setErrCode(-1);
            personListParam.setErrMsg("未找到该人脸");
            personListParam.setData(null);

            return personListParam;
        }
        //用faceUrlList 里面的url personnelList 取置信度最高的 如果置信度比要求的大 可以结束
        //删除target图片
        faceUrlList.clear();

        //上报结果需要请求服务器，该参数为请求服务器后返回的请求结果
        PersonListParam requestResult = null;


        //    //封装请求参数
        Map param = new HashMap<String, String>();
        param.put("userNo", String.valueOf(person.getUserNo()));
        LocationResult result = HttpUtil.sendLocationRequest(Constant.GET_PERSON_LOCATION_URL, param);
        int errCode = result.getErrCode();
        List<LocationInfo> peopleLocation = result.getData();
        Boolean Flag = false;

        if (OCBuildingNo.equals(peopleLocation.get(0).getBuildingNo()) && OCBidRoom.equals(peopleLocation.get(0).getBidRoom())) {
                Flag = true;
        }


        if (errCode != 0) {
            try {
                //TODO 此处的-1 需要于南方电网对接是否为-1还是其他值
                if (reliability != -1) {
                    //上报脸置信度
                    requestResult = reportPersonInfoService.Report(reportInfo, Constant.REPORT_BEHAVIOR_URL);
                    System.out.println("-----------上报----人脸");
                } else {
                    //上报上报reliability这个置信度
                    reportInfo.setReliability(reliability + "");
                    requestResult = reportPersonInfoService.Report(reportInfo, Constant.REPORT_BEHAVIOR_URL);
                    System.out.println(reportInfo);
                    System.out.println("-----------上报----算法");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {

            LocationInfo locationInfo = peopleLocation.get(0);
            String monitorTitle = locationInfo.getMonitorTitle();

            String local = monitorTitle.split("（")[0];

            if (!Flag) {
                System.out.println(ocParmars.getMonitorTitle() + "------------------门禁出现地点--" + reportInfo.getPersonName());
                try {
                    //TODO 此处的-1 需要于南方电网对接是否为-1还是其他值
                    if (reliability != -1) {
                        //上报脸置信度
                        requestResult = reportPersonInfoService.Report(reportInfo, Constant.REPORT_BEHAVIOR_URL);
                        System.out.println("-----------上报----人脸");
                    } else {
                        //上报上报reliability这个置信度
                        reportInfo.setReliability(reliability + "");
                        requestResult = reportPersonInfoService.Report(reportInfo, Constant.REPORT_BEHAVIOR_URL);
                        System.out.println("-----------上报----算法");
                    }
                } catch (Exception e) {
                }
            } else {
                System.out.println(ocParmars.getMonitorTitle() + "----------不上报  此人在此地点--" + reportInfo.getPersonName());

            }
        }


        //封装结果对象
        PersonListParam personListParam = new PersonListParam();
        personListParam.setErrCode(0);
        personListParam.setErrMsg("找到到该人脸， " + requestResult);
        personListParam.setData(null);

        reportInfo.setImageData("");

        System.out.println(reportInfo);


        return personListParam;
    }


    @PostMapping("/getInfoByImg")
    public PersonListParam reportInfo(@RequestBody String imgB64) throws IOException {
        String savePath = "D:\\temp_img\\face.jpg";
        try {
            // 解密
            Base64.Decoder decoder = Base64.getDecoder();
            // 去掉base64前缀 data:image/jpeg;base64,
            imgB64 = imgB64.substring(imgB64.indexOf(",", 1) + 1, imgB64.length());
            byte[] b = decoder.decode(imgB64);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            // 保存图片
            OutputStream out = new FileOutputStream(savePath);
            out.write(b);
            out.flush();
            out.close();

        } catch (IOException e) {
            return null;
        }

        //从官网获取
        String appId = Constant.APP_ID;
        String sdkKey = Constant.SDK_KEY;


        FaceEngine faceEngine = new FaceEngine(Constant.ENGINE_DIR);
        //激活引擎
        int errorCode = faceEngine.activeOnline(appId, sdkKey);
//        System.out.println(errorCode);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }


        ActiveFileInfo activeFileInfo = new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }

        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);

        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            System.out.println("初始化引擎失败");
        }

        //开始进行人脸对比
        String faceUrl = null;
        Person person = null;
        faceUrl = savePath;
        float faceSimilar = 0;
        int index = 0;
        for (int j = 0; j < personnelList.size(); j++) {
            float temp = 0 ;
            try {

                //人脸检测
                ImageInfo imageInfo = getRGBData(new File(faceUrl));
                List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
                errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
                //特征提取
                FaceFeature faceFeature = new FaceFeature();
                errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), faceFeature);

                //人脸检测2
                ImageInfo imageInfo2 = getRGBData(new File(personnelList.get(j).getPicUrl()));
                List<FaceInfo> faceInfoList2 = new ArrayList<FaceInfo>();
                errorCode = faceEngine.detectFaces(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2);

                //特征提取2
                FaceFeature faceFeature2 = new FaceFeature();
                errorCode = faceEngine.extractFaceFeature(imageInfo2.getImageData(), imageInfo2.getWidth(), imageInfo2.getHeight(), imageInfo.getImageFormat(), faceInfoList2.get(0), faceFeature2);

                //特征比对
                FaceFeature targetFaceFeature = new FaceFeature();
                targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
                FaceFeature sourceFaceFeature = new FaceFeature();
                sourceFaceFeature.setFeatureData(faceFeature2.getFeatureData());
                FaceSimilar face_Similar = new FaceSimilar();
                errorCode = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, face_Similar);
                temp = face_Similar.getScore();
                if (temp > faceSimilar){
                    faceSimilar = temp;
                    index = j;
                }



            } catch (Exception e) {
//                    e.printStackTrace();
            }
        }

        File file = new File(savePath);
        file.delete();

        person = new Person();
        person.setID(personnelList.get(index).getID());
        person.setUserNo(personnelList.get(index).getUserNo());
        person.setRealName(personnelList.get(index).getRealName());
        person.setIDCard(personnelList.get(index).getIDCard());
        person.setPicUrl(personnelList.get(index).getPicUrl());
        person.setUserType(personnelList.get(index).getUserType());
        person.setNodeCode("1001");

        errorCode = faceEngine.unInit();
        faceEngine = null;
        engineConfiguration = null;
        System.gc();


        if (faceSimilar< 0.5) {
            PersonListParam personListParam = new PersonListParam();
            personListParam.setErrCode(-1);
            personListParam.setErrMsg("未找到该人脸");
            personListParam.setData(null);
            return personListParam;
        }

        //封装结果对象
        PersonListParam personListParam = new PersonListParam();
        personListParam.setErrCode(0);
        personListParam.setErrMsg("找到该人脸");
        List list = new ArrayList();
        list.add(person);
        personListParam.setData(list);

        return personListParam;
    }
}


