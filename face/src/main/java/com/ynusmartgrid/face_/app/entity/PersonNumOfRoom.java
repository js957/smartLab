package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wjs
 * @since 2022-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PersonNumOfRoom implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表id
     */
    private Long numPerId;

    /**
     * 房间号
     */
    private String room;

    /**
     * 当前时间段最大人数
     */
    private Integer numOfPerson;

    /**
     * type类型
     */
    private Integer type;

    /**
     * 记录时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;


}
