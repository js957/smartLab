package com.ynusmartgrid.face_.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 人员实体类
 * Created by wjs on 2021/09/25
 */
public class Person {

    /*
        userNo	人员编号
     * 	realName 人员姓名
     * 	picUrl	人脸照片  人脸库地址
     */
    private static final long serialVersionUID = 1L;
    /**
    *@Description:
    *@Param:
     *  ID    编号
     * 	userNo	人员编号
     * 	realName	人员姓名
     * 	picUrl	人脸照片
     * 	userType	用户类型
     * 	beginTime	授权开始时间
     * 	endTime	授权结束时间
    *@return:
    *@Author: wjs
    *@date: 19:06
    */
    private int ID;
    private int userNo;
    private String realName;
    private String picUrl;
    private int userType;
    private String IDCard;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date beginTime;
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private Date endTime;
    private String nodeCode;

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public Person() {}

    public Person(int ID, int userNo, String realName, String picUrl, int userType, Date beginTime, Date endTime) {
        this.ID = ID;
        this.userNo = userNo;
        this.realName = realName;
        this.picUrl = picUrl;
        this.userType = userType;
        this.beginTime = beginTime;
        this.endTime = endTime;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getUserNo() {
        return userNo;
    }

    public void setUserNo(int userNo) {
        this.userNo = userNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        if (ID != person.ID) return false;
        if (userNo != person.userNo) return false;
        if (userType != person.userType) return false;
        if (!realName.equals(person.realName)) return false;
        if (!beginTime.equals(person.beginTime)) return false;
        return endTime.equals(person.endTime);

    }

    @Override
    public int hashCode() {
        int result = ID;
        result = 31 * result + userNo;
        result = 31 * result + realName.hashCode();
        result = 31 * result + userType;
        result = 31 * result + beginTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }
}
