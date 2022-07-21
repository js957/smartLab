package com.ynusmartgrid.face_.pojo;

import java.util.List;

public class OCParmars {
    private String monitorIP;
    private String monitorNo;
    private String behavior_algorithm;
    private String companyCode;
    private String imageData;
    private String monitorTitle;
    private float thresh;
    private float reliability;
    private String addTime;
    private String behavior_content;
    private String buildingNo;
    private String bidRoom;
    private List<String> target;


    @Override
    public String toString() {
        return "OCParmars{" +
                "monitorIP='" + monitorIP + '\'' +
                ", monitorNo='" + monitorNo + '\'' +
                ", behavior_algorithm='" + behavior_algorithm + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", imageData='" + imageData + '\'' +
                ", monitorTitle='" + monitorTitle + '\'' +
                ", thresh=" + thresh +
                ", reliability=" + reliability +
                ", addTime='" + addTime + '\'' +
                ", behavior_content='" + behavior_content + '\'' +
                ", buildingNo='" + buildingNo + '\'' +
                ", bidRoom='" + bidRoom + '\'' +
                ", target=" + target +
                '}';
    }

    public String getBidRoom() {
        return bidRoom;
    }

    public void setBidRoom(String bidRoom) {
        this.bidRoom = bidRoom;
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }


    public String getMonitorTitle() {
        return monitorTitle;
    }

    public void setMonitorTitle(String monitorTitle) {
        this.monitorTitle = monitorTitle;
    }

    public String getMonitorIP() {
        return monitorIP;
    }

    public void setMonitorIP(String monitorIP) {
        this.monitorIP = monitorIP;
    }

    public String getMonitorNo() {
        return monitorNo;
    }

    public void setMonitorNo(String monitorNo) {
        this.monitorNo = monitorNo;
    }

    public String getBehavior_algorithm() {
        return behavior_algorithm;
    }

    public void setBehavior_algorithm(String behavior_algorithm) {
        this.behavior_algorithm = behavior_algorithm;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public float getThresh() {
        return thresh;
    }

    public void setThresh(float thresh) {
        this.thresh = thresh;
    }

    public float getReliability() {
        return reliability;
    }

    public void setReliability(float reliability) {
        this.reliability = reliability;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getBehavior_content() {
        return behavior_content;
    }

    public void setBehavior_content(String behavior_content) {
        this.behavior_content = behavior_content;
    }

    public List<String> getTarget() {
        return target;
    }

    public void setTarget(List<String> target) {
        this.target = target;
    }




}
