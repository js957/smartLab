package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 *
 * 这有啥用啊！！！
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SystemSetting implements Serializable {

    private static final long serialVersionUID = 1L;


    public interface Add{

    }

    public interface Modify{

    }


    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 名称
     */
    @NotNull(message = "设置名不允许为空",groups = SystemSetting.Add.class)
    private String name;

    /**
     * 值
     */
    @NotNull(message = "参数不允许为空",groups = SystemSetting.Add.class)
    private String value;

    /**
     * 描述
     */
    private String description;

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


}
