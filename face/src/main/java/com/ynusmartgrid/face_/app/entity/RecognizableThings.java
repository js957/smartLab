package com.ynusmartgrid.face_.app.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author wjs
 * @since 2022-04-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class RecognizableThings implements Serializable {

    private static final long serialVersionUID = 1L;
    public interface Add{

    }

    public interface Modify{

    }

    /**
     * 物品主键
     */
    @NotNull(message = "id不允许为空。",groups = Equipment.Modify.class)
    @NotNull(message = "id不允许为空。",groups = Equipment.Add.class)
    @TableId(type = IdType.INPUT)
    private Long thingId;

    /**
     * 物品名
     */
    @NotEmpty(message = "可检测物品的名称不允许为空。",groups = Equipment.Add.class)
    private String thingName;

    /**
     * 识别阈值
     */
    @NotNull(message = "识别阈值不允许为空。",groups = Equipment.Add.class)
    private Double recThreshold;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;


}
