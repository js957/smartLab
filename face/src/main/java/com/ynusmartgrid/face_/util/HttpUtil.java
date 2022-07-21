package com.ynusmartgrid.face_.util;

import cn.hutool.json.JSONUtil;
import com.ynusmartgrid.face_.pojo.PersonListParam;
import com.ynusmartgrid.face_.pojo.LocationResult;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;


public class HttpUtil {
    public static PersonListParam sendHttpRequest(String httpURL, Map<String, String> params) throws IOException {
        //Url:https://way.jd.com/gzchengshu/mobile_region?mobile=15587090517&appkey=bf930634d546f174767dbc8c799e5db7
        //定义需要访问的地址
        URL url = new URL(httpURL);
        //连接url
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //请求方式
        connection.setRequestMethod("POST");
        //携带参数
        connection.setDoOutput(true);

        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> param : params.entrySet()) {
                sb.append("&").append(param.getKey()).append("=").append(param.getValue());

            }
            //去除第一个&
            connection.getOutputStream().write(sb.substring(1).toString().getBytes("UTF-8"));

        }
        //发起请求
        connection.connect();
        String response = StreamUtils.copyToString(connection.getInputStream(), Charset.forName("UTF-8"));
        PersonListParam result = JSONUtil.toBean(response, PersonListParam.class);
        return result;
    }


    public static LocationResult sendLocationRequest(String httpURL, Map<String, String> params) throws IOException {
        //Url:https://way.jd.com/gzchengshu/mobile_region?mobile=15587090517&appkey=bf930634d546f174767dbc8c799e5db7
        //定义需要访问的地址
        URL url = new URL(httpURL);
        //连接url
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //请求方式
        connection.setRequestMethod("POST");
        //携带参数
        connection.setDoOutput(true);

        if (params != null && params.size() > 0) {
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, String> param : params.entrySet()) {
                sb.append("&").append(param.getKey()).append("=").append(param.getValue());

            }
            //去除第一个&
            connection.getOutputStream().write(sb.substring(1).toString().getBytes("UTF-8"));

        }
        //发起请求
        connection.connect();
        String response = StreamUtils.copyToString(connection.getInputStream(), Charset.forName("UTF-8"));
        LocationResult result = JSONUtil.toBean(response, LocationResult.class);
        return result;
    }
}
