package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.sql.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
public class FaceCaptureRecord implements Serializable {

    private static final long serialVersionUID = 1L;


    public interface Add{

    }

    public interface Modify{

    }


    @TableId(type = IdType.AUTO)
    @NotNull(message = "id不允许为空。", groups = FaceCaptureRecord.Modify.class)
    private Long id;

    /**
     * 抓拍的人脸，地址
     */
    @NotEmpty(message = "抓拍的人脸地址不允许为空。", groups = FaceCaptureRecord.Add.class)
    @JSONField(name = "face")
    private String face;

    /**
     * 人脸id
     */
    @NotNull(message = "人脸id不允许为空。", groups = FaceCaptureRecord.Add.class)
    @JSONField(name = "faceId")
    private String faceId;

    /**
     * 人脸名
     */
    @NotNull(message = "人脸名称不允许为空。", groups = FaceCaptureRecord.Add.class)
    @JSONField(name = "faceName")
    private String faceName;

    /**
     * 抓拍的设备id
     */
    @NotNull(message = "抓拍的设备ip不允许为空。", groups = FaceCaptureRecord.Add.class)
    @JSONField(name = "equipmentIp")
    private String equipmentIp;

    /**
     * face 属于 face_id 的置信度
     */
    @JSONField(name = "confidence")
    private Float confidence;

    /**
     * 是否上报（0，1代表删除）
     */
    @JSONField(name = "isReport")
    private Boolean isReport;

    /**
     * 是否上报（0，1代表删除）
     */
    @JSONField(name = "isStranger")
    private Boolean isStranger;

    /**
     * 描述
     */
    @JSONField(name = "description")
    private String description;

    /**
     * 描述
     */
    @JSONField(name = "address")
    private String address;

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

    /**
     * 记录的天
     */
    private Date day;

    public FaceCaptureRecord() {
        //默认当前时间
        if(this.day == null){
            this.day = new Date(new java.util.Date().getTime());
        }

    }
}
