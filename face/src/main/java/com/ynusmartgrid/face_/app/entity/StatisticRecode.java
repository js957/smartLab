package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author wjs
 * @since 2022-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class StatisticRecode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 该统计所属的分组(分组如按房间分，按年级分等等)
     */
    private Long groupId;

    /**
     * 该分组名字
     */
    private String groupName;

    /**
     * 统计记录数，比如说组为房间----记录为人数即为房间内人数
     */
    private String statisticInfoRecode;

    /**
     * 记录时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    /**
     * 记录类型：(0:为时间段内房间内最大人数)
     */
    private Integer recodeType;


    public StatisticRecode() {
        //默认当前时间
        if(this.gmtCreate == null){
            gmtCreate=LocalDateTime.now();
        }
    }
}
