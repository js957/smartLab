package com.ynusmartgrid.face_.app.service;

import com.ynusmartgrid.face_.app.entity.FaceCaptureRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ynusmartgrid.face_.pojo.CheckInInfoByTime;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wjs
 * @since 2022-03-03
 */
public interface IFaceCaptureRecordService extends IService<FaceCaptureRecord> {

    public void exportExcelByToDay(HttpServletResponse response, HashMap<String,Object> resultMap);

    public void exportExcelByPeriod(HttpServletResponse response, List<CheckInInfoByTime> checkInInfoByTimes, List<String> dayPeriod);
}
