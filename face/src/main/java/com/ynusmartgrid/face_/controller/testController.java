package com.ynusmartgrid.face_.controller;


import com.ynusmartgrid.face_.pojo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wjs on 2021/09/27
 */
@RestController
public class testController {

    @Autowired
    private List<Person> personnelList;

    @GetMapping("/getPersonList")
    public List<Person> getPersonList(){
        return personnelList;
    }

   @GetMapping("/test")
   public String test(){
        return "hello!!!";
   }
}
