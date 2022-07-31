package com.ynusmartgrid.face_.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Created by wjs on 2022/07/29
 */
public class DateUtil {

    /**
     * 计算两个时间点的天数差
     *
     * @param dt1 第一个时间点
     * @param dt2 第二个时间点
     * @return int，即要计算的天数差
     */

    public static int dateDiff(LocalDateTime dt1, LocalDateTime dt2) {
        //获取第一个时间点的时间戳对应的秒数
        long t1 = dt1.toEpochSecond(ZoneOffset.ofHours(0));
        //获取第一个时间点在是1970年1月1日后的第几天
        long day1 = t1 / (60 * 60 * 24);
        //获取第二个时间点的时间戳对应的秒数
        long t2 = dt2.toEpochSecond(ZoneOffset.ofHours(0));
        //获取第二个时间点在是1970年1月1日后的第几天
        long day2 = t2 / (60 * 60 * 24);
        //返回两个时间点的天数差
        return (int) (day2 - day1);

    }
}
