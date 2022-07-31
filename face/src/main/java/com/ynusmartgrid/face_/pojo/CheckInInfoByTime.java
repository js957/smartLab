package com.ynusmartgrid.face_.pojo;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.ynusmartgrid.face_.app.entity.FaceGroupBelong;
import com.ynusmartgrid.face_.app.entity.JobRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wjs on 2022/07/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInInfoByTime {
    private String name;
    private List<CheckInInfoByDay> dayCheckInInfo;
    private Double attendance;
    private Double latenessRate;
    private Double attendanceOnTime;
    private int attend;
    private int belated;

    //复杂度 total*day*recode 优化空间很多但是没时间
    public static List<CheckInInfoByTime> transform2TimeDurationInfo(List<FaceGroupBelong> faceGroupBelongList, List<Map<String, Object>> checkInInfo, JobRecord jobRecord, List<String> dayPrior) {
        List<CheckInInfoByTime> checkInInfoByTimes = new ArrayList<>();
        for (FaceGroupBelong faceGroupBelong : faceGroupBelongList) {
            CheckInInfoByTime checkInInfoByTime = new CheckInInfoByTime();
            checkInInfoByTime.setName(faceGroupBelong.getFaceName()); // 成员人
            List<CheckInInfoByDay> checkInInfoByDayList = new ArrayList<>();
            int attend = 0; // 出席天数
            int belated = 0; //迟到天数
            for (String day : dayPrior) {
                CheckInInfoByDay checkInInfoByDay = new CheckInInfoByDay();
                checkInInfoByDay.setDay(day); // 期间内的每天
                checkInInfoByDay.setName(checkInInfoByTime.getName());
                for (Map<String, Object> map : checkInInfo) {
                    if (checkInInfoByDay.getName().equals(map.get("faceName").toString())
                            && checkInInfoByDay.getDay().equals(map.get("day").toString())) { // 如果在指定那天有记录
                        checkInInfoByDay.setAppearTime(map.get("appearTime").toString()); // 记录那天出现时间
                        checkInInfoByDay.setAttend(true); // 有出席
                        attend++;
                        if (CheckInInfoByDay.compare2DateTimeStrAndTimeStr(checkInInfoByDay.getAppearTime(), jobRecord.getCronExpression())) {//是否迟到
                            belated++;
                        } else {
                            checkInInfoByDay.setBelated(false);
                        }
                        break; //找到后中断循环止损
                    }
                }

                checkInInfoByDayList.add(checkInInfoByDay);
            }
            checkInInfoByTime.setAttend(attend);
            checkInInfoByTime.setBelated(belated);
            checkInInfoByTime.setAttendance((double)attend/dayPrior.size());
            checkInInfoByTime.setLatenessRate((double)belated/dayPrior.size());
            checkInInfoByTime.setAttendanceOnTime((double)(attend-belated)/dayPrior.size());
            checkInInfoByTime.setDayCheckInInfo(checkInInfoByDayList);
            checkInInfoByTimes.add(checkInInfoByTime);
        }
        return checkInInfoByTimes;
    }

    // 从两个yyyy-MM-dd格式的日期中获取当中的每一天的yyyy-MM-dd字符串
    public static List<String> getRangeDayList(String startDay, String endDay) {
        List<String> hashList = new ArrayList<>();
        String btime = startDay.substring(0, 10);//yyyy-MM-dd
        String etime = endDay.substring(0, 10);

        Date bDate = DateUtil.parse(btime, DatePattern.NORM_DATE_PATTERN);//yyyy-MM-dd
        Date eDate = DateUtil.parse(etime, DatePattern.NORM_DATE_PATTERN);
        List<DateTime> dateList = DateUtil.rangeToList(bDate, eDate, DateField.DAY_OF_YEAR);//创建日期范围生成器
        String hash = null;
        for (DateTime dt : dateList) {
            hash = dt.toString().substring(0, 10);
            hashList.add(hash);
        }
        return hashList;
    }
}
