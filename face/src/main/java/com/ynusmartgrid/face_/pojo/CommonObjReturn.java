package com.ynusmartgrid.face_.pojo;

import com.ynusmartgrid.face_.constant.Constant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by wjs on 2022/03/10
 */
@Data
public class CommonObjReturn<T> implements Serializable {

    private T data;

    private boolean result;

    private String msg;

    private String code;

    public CommonObjReturn(T data,boolean result, String msg, String code){
        this.data = data;
        this.result = result;
        this.msg = msg;
        this.code = code;
    }

    public CommonObjReturn(boolean result, String msg, String code){
        this.data = null;
        this.result = result;
        this.msg = msg;
        this.code = code;
    }

    public CommonObjReturn(String msg, String code){
        this.data = null;
        this.result = false;
        this.msg = msg;
        this.code = code;
    }

    public CommonObjReturn(T data){
        this.data = data;
        this.result = true;
        this.msg = "请求成功";
        this.code = Constant.RS_SUCCESS;
    }

    public CommonObjReturn(boolean result){
        this.data = null;
        this.result = result;
        if(result){
            this.msg = "成功！";
            this.code = Constant.RS_SUCCESS;
        }else{
            this.msg = "出现问题了！";
            this.code = Constant.RS_UNKNOWN_ERROR;
        }
    }


}
