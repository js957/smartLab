package com.ynusmartgrid.face_.pojo;

import com.ynusmartgrid.face_.app.entity.FaceGroupBelong;
import com.ynusmartgrid.face_.app.entity.JobRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.datetime.DateFormatter;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wjs on 2022/07/29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInInfoByDay {
    private String name;
    private String appearTime;
    private String day;
    private boolean attend = false;
    private boolean belated = true;

    public static List<CheckInInfoByDay> transform2DayInfo(List<FaceGroupBelong> faceGroupBelongList, List<Map<String, Object>> checkInInfo, JobRecord jobRecord){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<CheckInInfoByDay> checkInInfoByDayList = new ArrayList<>();
        for(FaceGroupBelong faceGroupBelong: faceGroupBelongList){
            CheckInInfoByDay checkInInfoByDay = new CheckInInfoByDay();
            checkInInfoByDay.setName(faceGroupBelong.getFaceName());
            for(Map<String,Object> map: checkInInfo){
                if(faceGroupBelong.getFaceName().equals(map.get("faceName").toString())){
                    checkInInfoByDay.setDay(map.get("day").toString());
                    checkInInfoByDay.setAppearTime(map.get("appearTime").toString());
                    checkInInfoByDay.setAttend(true);
                    checkInInfoByDay.setBelated(compare2DateTimeStrAndTimeStr(checkInInfoByDay.getAppearTime(), jobRecord.getCronExpression()));
                }
            }
            checkInInfoByDayList.add(checkInInfoByDay);
        }
        return checkInInfoByDayList;


    }

    public static boolean compare2DateTimeStrAndTimeStr(String dateTimeStr, String timeStr){
        // dateTimeStr dateFormatter is yyyy-MM-dd HH:mm:ss timeStr is HH:mm:ss
        String dateTimeSub = dateTimeStr.substring(11,19);
        LocalTime dts = LocalTime.parse(dateTimeSub);
        LocalTime ts = LocalTime.parse(timeStr);
        // 出勤时间(dts) 小于 规定时间(ts) compareTo返回-1 没迟到 返回取反即判断大于0 得false
        return dts.compareTo(ts) > 0;
    }
}
