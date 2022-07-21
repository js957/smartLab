package com.ynusmartgrid.face_.service;

import com.ynusmartgrid.face_.pojo.PersonListParam;
import com.ynusmartgrid.face_.pojo.ReportInfo;
import com.ynusmartgrid.face_.util.HttpUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportPersonInfoService {

    public PersonListParam Report(ReportInfo reportInfo, String Url) throws IOException {
        Map mapInfo = new HashMap<String ,String>();
        mapInfo.put("monitorIP",reportInfo.getMonitorIP());
        mapInfo.put("behavior_algorithm",reportInfo.getBehaviorAlgorithm());
        mapInfo.put("companyCode",reportInfo.getCompanyCode());
        mapInfo.put("imageData",reportInfo.getImageData());
        mapInfo.put("personNo",reportInfo.getPersonNo());
        mapInfo.put("personName",reportInfo.getPersonName());
        mapInfo.put("reliability",reportInfo.getReliability());
        mapInfo.put("addTime",reportInfo.getAddTime());
        mapInfo.put("nodeCode",reportInfo.getNodeCode());
        mapInfo.put("CardID",reportInfo.getIDCard());
        mapInfo.put("behavior_content",reportInfo.getBehaviorContent());
        return HttpUtil.sendHttpRequest(Url,mapInfo);

    }


}
