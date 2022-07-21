package com.ynusmartgrid.face_.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wjs on 2021/09/27
 */
public class LocationResult implements Serializable {
/**
 * errCode	int	0:返回成功 非0，失败
 * errMsg	string	请求结果说明
 * data	array
 */
    private int errCode;
    private String errMsg;
    private List<LocationInfo> data;

    public LocationResult() {}

    @Override
    public String toString() {
        return "LocationResult{" +
                "errCode=" + errCode +
                ", errMsg='" + errMsg + '\'' +
                ", data=" + data +
                '}';
    }

    public LocationResult(int errCode, String errMsg, List<LocationInfo> data) {
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.data = data;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public List<LocationInfo> getData() {
        return data;
    }

    public void setData(List<LocationInfo> data) {
        this.data = data;
    }
}
