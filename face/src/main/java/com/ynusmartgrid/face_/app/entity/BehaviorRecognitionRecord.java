package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Calendar;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 *
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BehaviorRecognitionRecord implements Serializable {

    public interface Add {

    }

    public interface Modify {

    }


    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "id不允许为空。", groups = Modify.class)
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 抓拍的图片（存地址）
     */
    @NotEmpty(message = "抓拍的图片不允许为空。", groups = Add.class)
    private String captureImg;

    /**
     * 抓拍的视频，存本地视频保存的路径，保存的根目录查看系统设置表
     */
    private String captureVideo;

    /**
     * 行为的类型
     */
    private Integer type;

    /**
     * 识别出的人脸 id（没有 id 则为陌生人）
     */
    private Long faceId;

    /**
     * 抓拍的设备id
     */
    @NotEmpty(message = "抓拍的设备id不允许为空。", groups = Add.class)
    private String equipmentIp;

    /**
     * 是否上报（0，1代表删除）
     */
    private Boolean isReport;

    /**
     * 人脸的置信度
     */
    private Float faceConfidence;

    /**
     * 行为的置信度
     */
    private Float behaviorConfidence;

    /**
     * 描述
     */
    private String description;

    /**
     * 识别到的房间号
     */
    private String room;

    /**
     * 是否删除（0，1代表删除）
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime gmtModified;

    public BehaviorRecognitionRecord() {
        //默认当前时间
        if (this.gmtCreate == null) {
            gmtCreate = LocalDateTime.now();
        }
    }

}
