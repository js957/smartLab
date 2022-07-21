package com.ynusmartgrid.face_.app.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
public class Face implements Serializable {

    private static final long serialVersionUID = 1L;

    public interface Add {

    }

    public interface Modify {

    }


    @NotNull(message = "id不允许为空。", groups = Face.Modify.class)
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 写入海康后返回的真实系统id
     */
    private String systemIdentity;

    /**
     * 人脸，地址
     */
    @NotEmpty(message = "人脸不允许为空。", groups = Face.Add.class)
    private String face;

    /**
     * 真实姓名
     */
    @NotEmpty(message = "姓名不允许为空。", groups = Face.Add.class)
    private String name;

    /**
     * 性别（男0，女1）
     */
    private Boolean sex;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 居住地
     */
    private String address;

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不允许为空。", groups = Face.Add.class)
    @Length(min = 11, max = 11, message = "手机号只能为11位")
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    private String phone;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 0:网站注册提交图片未处理,1:网站注册且提交处理(海康)
     */
    private Boolean registrationStatus;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否删除（0，1代表删除）
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 出勤次数累计
     */
    private int attendance;

    public Face() {
        //默认当前时间
        if(this.gmtCreate == null){
            gmtCreate=LocalDateTime.now();
        }
    }

}
