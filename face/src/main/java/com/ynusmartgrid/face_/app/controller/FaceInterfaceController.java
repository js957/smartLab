package com.ynusmartgrid.face_.app.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ynusmartgrid.face_.app.entity.Equipment;
import com.ynusmartgrid.face_.app.entity.Face;
import com.ynusmartgrid.face_.app.service.IFaceInterfaceService;
import com.ynusmartgrid.face_.constant.Constant;
import com.ynusmartgrid.face_.pojo.CommonObjReturn;
import com.ynusmartgrid.face_.service.WebSocketServer;
import com.ynusmartgrid.face_.util.IoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
@RestController
@RequestMapping("/app/member")
public class FaceInterfaceController {

    @Autowired
    IFaceInterfaceService faceInterfaceServiceImpl;

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:28
     * 根据id查询特定成员信息
     * 根据分页以及(姓名name,phone,regStatus)查找成员
     */
    @PostMapping("/getMember")
    public CommonObjReturn getMember(@RequestBody HashMap<String, Object> objMap) {
        if (StrUtil.isNotBlank(objMap.get("id").toString())) {
            return new CommonObjReturn(faceInterfaceServiceImpl.getById(objMap.get("id").toString()));
        }
        // 必须分页
        if (StrUtil.isBlankIfStr(objMap.get("pageIndex")) || StrUtil.isBlankIfStr(objMap.get("pageSize"))) {
            return new CommonObjReturn<Object>("页号与页面大小不允许为空", Constant.RS_FIELD_INVALID_RECODE);
        }
        int pageIndex = Integer.parseInt(objMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(objMap.get("pageSize").toString());
        IPage<Face> iPage = new Page<>(pageIndex, pageSize);
        QueryWrapper<Face> faceQuery = new QueryWrapper<>();
        if (StrUtil.isNotBlank(objMap.get("name").toString())) {
            faceQuery.like("name", objMap.get("name"));
        }
        if (StrUtil.isNotBlank(objMap.get("phone").toString())) {
            faceQuery.like("phone", objMap.get("phone"));
        }
        if (StrUtil.isNotBlank(objMap.get("regStatus").toString())) {
            faceQuery.eq("registration_status", Boolean.parseBoolean(objMap.get("regStatus").toString()));
        }
        return new CommonObjReturn(faceInterfaceServiceImpl.page(iPage, faceQuery));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:37
     * 根据传入的设备信息创建成员
     */
    @PostMapping("/saveMember")
    public CommonObjReturn addMember(@Validated(value = {Face.Add.class}) @RequestBody Face face) {
        StringBuffer parentDirPath = new StringBuffer();
        parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                .append("memberFace")
                .append(FileUtil.FILE_SEPARATOR)
                .append(face.getName())
                .append(FileUtil.FILE_SEPARATOR);
        face.setFace(IoUtil.copyToLocal(face.getFace(), parentDirPath.toString()));
        Boolean result = faceInterfaceServiceImpl.saveOrUpdate(face);
        WebSocketServer.sendInfo(JSON.toJSONString(face), "2");
        return new CommonObjReturn(result);
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:37
     * 根据id删除成员
     */
    @PostMapping("/removeMember")
    public CommonObjReturn deleteMember(@RequestBody Long id) {
        Face member = faceInterfaceServiceImpl.getById(id);
        if (member == null) {
            return new CommonObjReturn(true, "记录已被删除", Constant.RS_SUCCESS);
        }
        return new CommonObjReturn(faceInterfaceServiceImpl.removeById(id));
    }

    /**
     * @Param:
     * @Author: wjs
     * @date: 21:37
     * 根据传入的成员信息进行修改
     */
    @PostMapping("/modifyMember")
    public CommonObjReturn modifyMemberInfo(@Validated(value = {Face.Modify.class}) @RequestBody Face face) {
        Face thisFace = faceInterfaceServiceImpl.getById(face.getId());
        if (StrUtil.isNotBlank(face.getFace())) {
            StringBuffer parentDirPath = new StringBuffer();
            parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                    .append("memberFace")
                    .append(FileUtil.FILE_SEPARATOR)
                    .append(face.getName())
                    .append(FileUtil.FILE_SEPARATOR);
            face.setFace(IoUtil.copyToLocal(face.getFace(), parentDirPath.toString()));
        }

        return new CommonObjReturn(faceInterfaceServiceImpl.updateById(face));
    }


    /**
     * @Param:
     * @Author: wjs
     * @date: 22:02
     * 文件上传,先存到临时文件夹
     */
    @PostMapping("/facePictureUpload")
    public CommonObjReturn facePictureUpload(@RequestParam("picture") MultipartFile uploadFile) throws IOException {
        if (uploadFile.isEmpty()) {
            return new CommonObjReturn("未获取到上传文件！", Constant.RS_FIELD_INVALID_RECODE);
        }
        StringBuffer parentDirPath = new StringBuffer();
        parentDirPath.append(Constant.SMART_LAB_LOCAL_FILE_DIR)
                .append("temp")
                .append(FileUtil.FILE_SEPARATOR)
                .append(uploadFile.getOriginalFilename());
        FileUtil.mkParentDirs(parentDirPath.toString());
        FileUtil.writeFromStream(uploadFile.getInputStream(), parentDirPath.toString());
        return new CommonObjReturn(parentDirPath.toString());
    }

    @GetMapping("/sentFaceInfo")
    public void sentFaceInfo() throws InterruptedException {
        List<Face> list = faceInterfaceServiceImpl.list();
        for(Face face: list){
            Thread.sleep(500);
            WebSocketServer.sendInfo(JSON.toJSONString(face),"2");
        }
    }
}
