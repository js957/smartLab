package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author wjs
 * @since 2022-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class JobRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 任务主键
     */
    @TableId(type = IdType.AUTO)
    private Long jobId;

    /**
     * 任务名
     */
    private String jobName;

    /**
     * 任务组名
     */
    private String jobGroup;

    /**
     * 第一次任务开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 任务结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 任务调用的类
     */
    private String jobClassName;

    /**
     * corn表达式，表示任务执行的触发时间
     */
    private String cronExpression;

    /**
     * 传参
     */
    private String invokeParam;

    /**
     * 任务绑定的组，组中为此任务涉及的人员，若空表示所有人都参与此任务
     */
    private Long groupId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 任务关闭时间
     */
    private String closeCronExpression;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;

    public JobRecord() {
        //默认当前时间
        if (this.createTime == null) {
            createTime = LocalDateTime.now();
        }
    }

}
