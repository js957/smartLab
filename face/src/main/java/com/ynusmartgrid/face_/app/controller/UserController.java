package com.ynusmartgrid.face_.app.controller;


import com.alibaba.fastjson.JSON;
import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.ynusmartgrid.face_.app.entity.User;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@RestController
@RequestMapping("/app/user")
public class UserController {

     /**
    *@Param:
    *@Author: wjs
    *@date: 21:28
     * 根据id查询特定用户
     * 根据分页以及(姓名，角色)查找用户
    */
    public CommonObjReturn getUser(@RequestBody HashMap<String,Object> objMap){

        return null;
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:37
     * 根据传入的用户信息创建用户
    */
    public CommonObjReturn addUser(@Validated(value = {User.Add.class}) @RequestBody User user){
        return null;
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:37
     * 根据id删除用户
    */
    public CommonObjReturn deleteUser(@RequestBody Integer id){
        return null;
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 22:27
     * 修改用户信息
    */
    public CommonObjReturn modifyUser(@Validated(value = {User.Modify.class}) @RequestBody User user){
        return null;
    }

    public CommonObjReturn login(@Validated(value = User.Login.class) @RequestBody User user){
        return null;
    }

    public CommonObjReturn logout(){
        return null;
    }

}
