package com.ynusmartgrid.face_.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class Monitor {
    //{"ID":"63","monitor_ip":"192.168.1.64","monitor_title":"2\u53f7\u697c1\u697c\u8d70\u5eca1","monitorNo":"1F-C12-2",
    // "rtspUrl":"rtsp:\/\/admin:xxjsb411@192.168.1.64\/Streaming\/Channels\/102?transportmode=unicast",
    // "monitor_userName":"admin","monitor_password":"xxjsb411"}

    @JSONField(name = "ID")
    private int ID;
    @JSONField(name = "monitor_ip")
    private String monitorIp;
    @JSONField(name = "monitor_title")
    private String monitorTitle;
    @JSONField(name = "monitorNo")
    private String monitorNo;
    @JSONField(name = "rtspUrl")
    private String rtspUrl;
    @JSONField(name = "monitor_userName")
    private String monitorUserName;
    @JSONField(name = "monitor_password")
    private String monitorPassword;
    private NativeLong userid;
    private Memory lastPicture;
    private int numOfErrors = 0;
    private int numOfExecutions = 0;

    public Monitor() {
    }

    public Monitor(int ID, String monitorIp, String monitorTitle, String monitorNo, String rtspUrl, String monitorUserName, String monitorPassword) {
        this.ID = ID;
        this.monitorIp = monitorIp;
        this.monitorTitle = monitorTitle;
        this.monitorNo = monitorNo;
        this.rtspUrl = rtspUrl;
        this.monitorUserName = monitorUserName;
        this.monitorPassword = monitorPassword;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getMonitorIp() {
        return monitorIp;
    }

    public void setMonitorIp(String monitorIp) {
        this.monitorIp = monitorIp;
    }

    public String getMonitorTitle() {
        return monitorTitle;
    }

    public void setMonitorTitle(String monitorTitle) {
        this.monitorTitle = monitorTitle;
    }

    public String getMonitorNo() {
        return monitorNo;
    }

    public void setMonitorNo(String monitorNo) {
        this.monitorNo = monitorNo;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getMonitorUserName() {
        return monitorUserName;
    }

    public void setMonitorUserName(String monitorUserName) {
        this.monitorUserName = monitorUserName;
    }

    public String getMonitorPassword() {
        return monitorPassword;
    }

    public void setMonitorPassword(String monitorPassword) {
        this.monitorPassword = monitorPassword;
    }

    public NativeLong getUserid() {
        return userid;
    }

    public void setUserid(NativeLong userid) {
        this.userid = userid;
    }

    public Memory getLastPicture() {
        return lastPicture;
    }

    public void setLastPicture(Memory lastPicture) {
        this.lastPicture = lastPicture;
    }

    public int getNumOfErrors() {
        return numOfErrors;
    }

    public void setNumOfErrors(int numOfErrors) {
        this.numOfErrors = numOfErrors;
    }

    public int getNumOfExecutions() {
        return numOfExecutions;
    }

    public void setNumOfExecutions(int numOfExecutions) {
        this.numOfExecutions = numOfExecutions;
    }

    @Override
    public String toString() {
        return "Monitor{" +
                "ID=" + ID +
                ", monitorIp='" + monitorIp + '\'' +
                ", monitorTitle='" + monitorTitle + '\'' +
                ", monitorNo='" + monitorNo + '\'' +
                ", rtspUrl='" + rtspUrl + '\'' +
                ", monitorUserName='" + monitorUserName + '\'' +
                ", monitorPassword='" + monitorPassword + '\'' +
                '}';
    }
}
