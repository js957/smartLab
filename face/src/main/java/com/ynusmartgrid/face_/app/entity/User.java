package com.ynusmartgrid.face_.app.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

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
public class User implements Serializable {

    private static final long serialVersionUID = 1L;


    public interface Add{

    }

    public interface Modify{

    }

    public interface Login{

    }
    @NotEmpty(message = "id不允许为空", groups = User.Modify.class)
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户登录名 不允许更改 不允许重复
     */
    @NotEmpty(message = "用户名不允许为空", groups = User.Login.class)
    @NotEmpty(message = "用户名不允许为空", groups = User.Add.class)
    private String username;

    /**
     * 用户密码 限制6~16位
     */
    @NotEmpty(message = "用户密码不允许为空", groups = User.Login.class)
    @Length(min = 6,max = 16,message = "用户密码 限制6~16位",groups = User.Add.class)
    @Length(min = 6,max = 16,message = "用户密码 限制6~16位",groups = User.Modify.class)
    @Length(min = 6,max = 16,message = "用户密码 限制6~16位",groups = User.Login.class)
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 角色，0代表普通管理员，1代表系统管理员
     */
    private Integer role;

    /**
     * 个人说明
     */
    private String note;


}
