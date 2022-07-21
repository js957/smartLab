package com.ynusmartgrid.face_.pojo;

public class LocationInfo {
    private String location;
    private String recordingTime;
    private String monitorTitle;
    private String buildingNo;
    private String bidRoom;

    @Override
    public String toString() {
        return "LocationInfo{" +
                "location='" + location + '\'' +
                ", recordingTime='" + recordingTime + '\'' +
                ", monitorTitle='" + monitorTitle + '\'' +
                ", buildingNo='" + buildingNo + '\'' +
                ", bidRoom='" + bidRoom + '\'' +
                '}';
    }

    public String getBuildingNo() {
        return buildingNo;
    }

    public void setBuildingNo(String buildingNo) {
        this.buildingNo = buildingNo;
    }

    public void setBidRoom(String bidRoom) {
        this.bidRoom = bidRoom;
    }

    public String getBidRoom() {
        return bidRoom;
    }


    public String getLocaTion() {
        return location;
    }

    public void setLocaTion(String location) {
        this.location = location;
    }

    public String getRecordingTime() {
        return recordingTime;
    }

    public void setRecordingTime(String recordingTime) {
        this.recordingTime = recordingTime;
    }

    public String getMonitorTitle() {
        return monitorTitle;
    }

    public void setMonitorTitle(String monitorTitle) {
        this.monitorTitle = monitorTitle;
    }
}

