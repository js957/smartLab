package com.ynusmartgrid.face_.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by wjs on 2022/03/14
 */
public class ControllerUtil {

    public QueryWrapper addQueryTimeLimit(QueryWrapper queryWrapper, String timeStr){
        // 查当天
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            ZonedDateTime parsedDate = ZonedDateTime.parse(timeStr);
            LocalDateTime startTime = parsedDate.toLocalDateTime();
            LocalDateTime endTime = startTime.plusDays(1L);
            queryWrapper.apply("UNIX_TIMESTAMP(gmt_create) >= UNIX_TIMESTAMP('" + startTime.format(df) + "')");
            queryWrapper.apply("UNIX_TIMESTAMP(gmt_create) < UNIX_TIMESTAMP('" + endTime.format(df) + "')");
            return queryWrapper;
    }
}
