package com.ynusmartgrid.face_.app.entity;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class MemberGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    public interface Add{

    }

    public interface Modify{

    }

    /**
     * 组主键
     */
    @TableId(type = IdType.AUTO)
    @NotNull(message = "id不允许为空。",groups = Modify.class)
    private Long groupId;

    /**
     * 对该组的描述
     */
    private String groupDescription;

    /**
     * 组名
     */
    @NotEmpty(message = "组名不允许为空。", groups = Add.class)
    private String groupName;

    /**
     * 是否删除
     */
    @TableLogic
    private Boolean deleted;

    @TableField(exist = false)
    private List<FaceGroupBelong> faceGroupBelongList;

}
