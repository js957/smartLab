package com.ynusmartgrid.face_.app.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class FaceGroupBelong implements Serializable {

    private static final long serialVersionUID = 1L;

    public interface Add{

    }

    public interface Modify{

    }

    /**
     * 人脸与组主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message = "id不允许为空。", groups = Modify.class)
    private Long faceGroupId;

    /**
     * 组号
     */
    @NotNull(message = "id不允许为空。", groups = Add.class)
    private Long groupId;

    /**
     * 人脸号
     */
    @NotEmpty(message = "faceId不允许为空。", groups = Add.class)
    private String faceId;

    /**
     * 人脸所属人名
     */
    @NotEmpty(message = "faceName不允许为空。", groups = Add.class)
    private String faceName;


}
