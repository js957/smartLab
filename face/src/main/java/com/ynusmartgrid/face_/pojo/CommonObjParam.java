package com.ynusmartgrid.face_.pojo;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * Created by wjs on 2022/03/10
 */
@Data
public class CommonObjParam implements Serializable {
    public interface Page{

    }
    public interface Default{

    }
    @NotEmpty(message = "data不允许为空", groups = Default.class)
    @NotEmpty(message = "data不允许为空", groups = Page.class)
    private Object data;

    @NotEmpty(message = "页号不允许为空", groups = Page.class)
    @Min(message = "页号由1开始！",groups = Page.class,value=1L)
    private Integer pageIndex;

    @NotEmpty(message = "页大小不允许为空", groups = Page.class)
    @Range(min = 5,max = 30,message = "每页大小不能小于5，不能大于30",groups = Page.class)
    private Integer pageSize;
}
