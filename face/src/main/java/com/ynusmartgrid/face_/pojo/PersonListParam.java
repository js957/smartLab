package com.ynusmartgrid.face_.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wjs on 2021/09/27
 */
public class PersonListParam implements Serializable {
/**
 * errCode	int	0:返回成功 非0，失败
 * errMsg	string	请求结果说明
 * data	array
 */
    private int errCode;
    private String errMsg;
    private List<Person> data;


    public PersonListParam() {}

    @Override
    public String toString() {
        return "CommonObjParam{" +
                "errCode=" + errCode +
                ", errMsg='" + errMsg + '\'' +
                ", data=" + data +
                '}';
    }

    public PersonListParam(int errCode, String errMsg, List<Person> data) {
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

    public List<Person> getData() {
        return data;
    }

    public void setData(List<Person> data) {
        this.data = data;
    }
}
