package com.ynusmartgrid.face_.common;

import com.ynusmartgrid.face_.app.entity.PersonNumOfRoom;
import com.ynusmartgrid.face_.app.entity.StatisticRecode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by wjs on 2022/04/23
 */
@Configuration
public class MaxNumBerOfRoomList {

    private volatile static List<StatisticRecode> statisticRecodeList;

    private MaxNumBerOfRoomList(){}

    @Bean(name = "maxNumBerOfRoomList")
    public static List<StatisticRecode> getMaxNumBerOfRoomList(){
        if(statisticRecodeList == null) {
            synchronized (MaxNumBerOfRoomList.class) {
                    statisticRecodeList = new ArrayList<>();
            }
        }
        return statisticRecodeList;
    }

    /**
    *@Param:
    *@Author: wjs
    *@date: 21:56
     * 着急交，随便写的，以后再优化
    */
    public static void setNumBerOfRoomList(List<StatisticRecode> newList){
        statisticRecodeList = getMaxNumBerOfRoomList();
        if(statisticRecodeList.size()<=0){
            statisticRecodeList.addAll(newList);
        }else{
            statisticRecodeList = statisticRecodeList.stream().map(map->newList.stream()
                .filter(m-> Objects.equals(m.getGroupId(), map.getGroupId()))
                .filter(m->Integer.parseInt(m.getStatisticInfoRecode()) > Integer.parseInt(map.getStatisticInfoRecode()))
                .findFirst().map(m->{
                    map.setStatisticInfoRecode(m.getStatisticInfoRecode());
                    return map;
                }).orElse(map)).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    public static void doEmpty(){
        statisticRecodeList = null;
    }

    public static void main(String[] args) {
        List<StatisticRecode> numberOfRoomList = Arrays.asList(
                new StatisticRecode(null,4L,"1217","2", LocalDateTime.now(),0),
                new StatisticRecode(null,3L,"1215","8", LocalDateTime.now(),0),
                new StatisticRecode(null,2L,"1213","7", LocalDateTime.now(),0)
        );
        List<StatisticRecode> newNumberOfRoomList = Arrays.asList(
                new StatisticRecode(null,4L,"1217","3", LocalDateTime.now(),0),
                new StatisticRecode(null,3L,"1215","8", LocalDateTime.now(),0),
                new StatisticRecode(null,2L,"1213","2", LocalDateTime.now(),0)
        );
        numberOfRoomList = numberOfRoomList.stream().map(map->newNumberOfRoomList.stream()
                .filter(m-> Objects.equals(m.getGroupId(), map.getGroupId()))
                .filter(m->Integer.parseInt(m.getStatisticInfoRecode()) > Integer.parseInt(map.getStatisticInfoRecode()))
                .findFirst().map(m->{
                    map.setStatisticInfoRecode(m.getStatisticInfoRecode());
                    return map;
                }).orElse(map)).filter(Objects::nonNull).collect(Collectors.toList());
        System.out.println(numberOfRoomList);


    }
}
