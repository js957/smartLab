package com.ynusmartgrid.face_.pojo;

public class ReportInfo {
    private String monitorIP;
    private String behaviorAlgorithm;
    private String companyCode;
    private String imageData;
    private String personNo;
    private String personName;
    private String reliability;
    private String addTime;
    private String behaviorContent;
    private String IDCard;
    private String nodeCode;


    public String getPersonNo() {
        return personNo;
    }

    public void setPersonNo(String personNo) {
        this.personNo = personNo;
    }

    public String getNodeCode() {
        return nodeCode;
    }

    public void setNodeCode(String nodeCode) {
        this.nodeCode = nodeCode;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getMonitorIP() {
        return monitorIP;
    }

    public void setMonitorIP(String monitorIP) {
        this.monitorIP = monitorIP;
    }

    public String getBehaviorAlgorithm() {
        return behaviorAlgorithm;
    }

    public void setBehaviorAlgorithm(String behaviorAlgorithm) {
        this.behaviorAlgorithm = behaviorAlgorithm;
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



    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getReliability() {
        return reliability;
    }

    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getBehaviorContent() {
        return behaviorContent;
    }

    public void setBehaviorContent(String behaviorContent) {
        this.behaviorContent = behaviorContent;
    }


    @Override
    public String toString() {
        return "ReportInfo{" +
                "monitorIP='" + monitorIP + '\'' +
                ", behaviorAlgorithm='" + behaviorAlgorithm + '\'' +
                ", companyCode='" + companyCode + '\'' +
                ", imageData='" + imageData + '\'' +
                ", userNo='" + personNo + '\'' +
                ", personName='" + personName + '\'' +
                ", reliability='" + reliability + '\'' +
                ", addTime='" + addTime + '\'' +
                ", behaviorContent='" + behaviorContent + '\'' +
                '}';
    }
}
