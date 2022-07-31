package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ynusmartgrid.face_.app.entity.Equipment;
import com.ynusmartgrid.face_.app.entity.FaceGroupBelong;
import com.ynusmartgrid.face_.app.entity.MemberGroup;
import com.ynusmartgrid.face_.app.service.IFaceGroupBelongService;
import com.ynusmartgrid.face_.app.service.IMemberGroupService;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *  为成员进行分组表，与人脸组从属表共同构成人脸分组功能
 *  所有关于组和组与组成员的删除皆为物理删除，因为没必要
 * @author wjs
 * @since 2022-04-19
 */
@RestController
@RequestMapping("/app/memberGroup")
public class MemberGroupController {

    @Autowired
    IMemberGroupService memberGroupServiceImpl;

    @Autowired
    IFaceGroupBelongService faceGroupBelongServiceImpl;

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:01
     * 获取组的信息，可以查找所有组(无输入,仅显示组名), 可以根据组id(groupId)获取组成员
    */
    @PostMapping("/getMemberGroup")
    public CommonObjReturn getMemberGroup(@RequestBody HashMap<String,Object> objMap){
        if(StrUtil.isNotBlank(objMap.get("id").toString())){
            MemberGroup memberGroup = memberGroupServiceImpl.getById(objMap.get("id").toString());
            List<FaceGroupBelong> faceGroupBelongList = faceGroupBelongServiceImpl
                    .list(new QueryWrapper<FaceGroupBelong>().eq("group_id",objMap.get("id").toString()));
            memberGroup.setFaceGroupBelongList(faceGroupBelongList);
            return new CommonObjReturn(memberGroup);
        }
        return new CommonObjReturn(memberGroupServiceImpl.list());

    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:09
     * 修改组信息,主要为修改组名(groupName)、组描述(groupDescription)
    */
    @PostMapping("/modifyMemberGroup")
    public CommonObjReturn modifyMemberGroup(@Validated(value = {MemberGroup.Modify.class})@RequestBody MemberGroup memberGroup){
        return new CommonObjReturn(memberGroupServiceImpl.updateById(memberGroup));
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:17
     * 添加组
    */
    @PostMapping("/saveMemberGroup")
    public CommonObjReturn addMemberGroup(@Validated(value = {MemberGroup.Add.class}) @RequestBody MemberGroup memberGroup){
        return new CommonObjReturn(memberGroupServiceImpl.save(memberGroup));
    }

    //
    /**
    *@Param:
    *@Author: wjs
    *@date: 21:19
     * 批量添加组员,传入人脸列表中的人脸id(faceId),人脸名称(faceName),组号(groupId)
    */
    @PostMapping("/saveMemberByBatch")
    public CommonObjReturn addMemberByBatch(@Validated(value = {FaceGroupBelong.Add.class}) @RequestBody List<FaceGroupBelong> faceGroupBelongList){
        return new CommonObjReturn(faceGroupBelongServiceImpl.saveBatch(faceGroupBelongList));
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:20
     * 删除组，传入参数为组号
    */
    @PostMapping("/removeMemberGroup")
    public CommonObjReturn deleteMemberGroup(@RequestBody Long id){
        return new CommonObjReturn(memberGroupServiceImpl.removeById(id));
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:21
     * 批量删除组员,传入参数为组员id列表与组号
    */
    @PostMapping("/removeMemberByBatch")
    public CommonObjReturn deleteMemberByBatch(@RequestBody List<Long> ids){
        return new CommonObjReturn(faceGroupBelongServiceImpl.removeByIds(ids));
    }




}
