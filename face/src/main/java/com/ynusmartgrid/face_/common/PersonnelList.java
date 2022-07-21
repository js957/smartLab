package com.ynusmartgrid.face_.common;

import com.ynusmartgrid.face_.pojo.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * personnelList单例
 * Created by wjs on 2021/09/26
 */
@Configuration
public class PersonnelList {

    private volatile static List<Person> personnelList;

    private PersonnelList(){}

    @Bean(name = "personnelList")
    public static List<Person> getPersonnelList(){
        if(personnelList == null) {
            synchronized (PersonnelList.class) {
                if (personnelList == null) {
                    personnelList = new ArrayList<>();
                }
            }
        }
        return personnelList;
    }
}
