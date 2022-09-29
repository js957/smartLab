package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

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
public class Equipment implements Serializable {


    public interface Add{

    }

    public interface Modify{

    }



    private static final long serialVersionUID = 1L;



    /**
     * 设备id即ip字符串
     */
    @NotEmpty(message = "id不允许为空。",groups = Modify.class)
    @NotEmpty(message = "id不允许为空。",groups = Add.class)
    @TableId(type = IdType.INPUT)
    private String id;

    /**
     * 设备地址
     */
    @NotEmpty(message = "设备地址不允许为空。",groups = Add.class)
    private String address;

    /**
     * 视频流地址
     */
    @NotEmpty(message = "视频流地址不允许为空。",groups = Add.class)
    private String videostream;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否开启“决策规则”预警(识别目标私自将文件资料或自行记录的纸质资料带离评标场所时)
     */
    private Boolean isDecisionRulesEarlyWarning;

    /**
     * 开启“决策规则”预警的阈值

     */
    private Float decisionRulesEarlyWarningThresh;

    /**
     * 是否开启专家异常行为跟踪(单目标跟踪和多目标跟踪)
     */
    private Boolean isAbnormalBehaviorTracking;

    /**
     * 专家异常行为跟踪的阈值(单目标跟踪和多目标跟踪)
     */
    private Float abnormalBehaviorTrackingThresh;

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

    public Equipment() {
        //默认当前时间
        if(this.gmtCreate == null){
            gmtCreate=LocalDateTime.now();
        }
    }
}
