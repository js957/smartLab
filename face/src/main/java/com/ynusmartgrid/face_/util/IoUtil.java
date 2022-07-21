package com.ynusmartgrid.face_.util;


import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;//需要导入的包
import com.ynusmartgrid.face_.app.entity.BehaviorRecognitionRecord;
import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.ynusmartgrid.face_.constant.Constant;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by wjs on 2021/09/26
 */
public class IoUtil {

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:24
     * 图片下载
    */
    public static String downloadImage(String fileUrl) {
        String path = null;
        String staticAndMksDir = null;
        if (fileUrl != null) {
            //下载时文件名称
            String[] str = fileUrl.split("/");
            String fileName = str[str.length-1];
            System.out.println(fileName);
            try {
                File localPath = new File(Constant.LOCAL_PICTURE_DIR);
                if(!localPath.exists()) localPath.mkdirs();
                path = localPath.toString() + File.separator + fileName;
                HttpUtil.downloadFile(fileUrl, path);

            } catch (Exception e) {
//                e.printStackTrace();
                System.out.println(fileUrl);
                System.out.println("下载文件错误！");
            }
        }
        return path;
    }

    /**
    *@Param: originalFilePath:原图片位置
     * parentDirPath:新位置目录
    *@Author: wjs
    *@date: 22:30
    */
    public static String copyToLocal(String originalFilePath,String parentDirPath){
        File picture = new File(originalFilePath);
        if(picture.exists()){
            parentDirPath += picture.getName();
            File parentDir = FileUtil.mkParentDirs(parentDirPath.toString());
            File result = FileUtil.copy(picture,parentDir,false);
            return parentDirPath.toString().replaceFirst(Constant.SMART_LAB_LOCAL_FILE_DIR,Constant.SMART_LAB_LOCAL_STATIC_DIR);
        }
        return "";
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 22:34
     * 专用写法，已废弃
    */
    public static String capturedRecordCopyToLocal(BehaviorRecognitionRecord behaviorRecognitionRecord){
        File picture = new File(behaviorRecognitionRecord.getCaptureImg());
        if(picture.exists()){
            StringBuffer parentDirPath = new StringBuffer();
            parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                    .append("BehaviorRecognitionRecord")
                    .append(FileUtil.FILE_SEPARATOR)
                    .append(behaviorRecognitionRecord.getDescription())
                    .append(FileUtil.FILE_SEPARATOR)
                    .append(picture.getName());
            File parentDir = FileUtil.mkParentDirs(parentDirPath.toString());
            File result = FileUtil.copy(picture,parentDir,false);
            return parentDirPath.toString().replaceFirst(Constant.SMART_LAB_LOCAL_FILE_DIR,Constant.SMART_LAB_LOCAL_STATIC_DIR);
        }
        return "";
    }

    public static String capturedRecordCopyToLocal(FaceCaptureRecord faceCaptureRecord){
        File picture = new File(faceCaptureRecord.getFace());
        if(picture.exists()){
            String facePath;
            if(faceCaptureRecord.getIsStranger()){
                facePath = "stranger" + FileUtil.FILE_SEPARATOR + faceCaptureRecord.getFaceId();
            }else{
                facePath = faceCaptureRecord.getFaceId();
            }
            StringBuffer parentDirPath = new StringBuffer();
            parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                    .append("faceCapture")
                    .append(FileUtil.FILE_SEPARATOR)
                    .append(facePath)
                    .append(FileUtil.FILE_SEPARATOR)
                    .append(picture.getName());
            File parentDir = FileUtil.mkParentDirs(parentDirPath.toString());
            File result = FileUtil.copy(picture,parentDir,false);
            return parentDirPath.toString().replaceFirst(Constant.SMART_LAB_LOCAL_FILE_DIR,Constant.SMART_LAB_LOCAL_STATIC_DIR);
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(copyToLocal("E:\\临时\\图片\\b612\\50_肖亮.jpg","E:\\临时\\图片\\test\\"));
    }

}
