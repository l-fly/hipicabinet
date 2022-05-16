package com.haipai.cabinet.util;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;


public class JsonUtils {



    //将JSON数据解析生成指定的类  
    public static <T> T jsonToBean(String jsonResult, Class<T> clz) {
//        LogUtil.d("whj jsonToBean: "+jsonResult);
        Gson gson = new Gson();
        T t = gson.fromJson(jsonResult, clz);
        return t;
    }

    //将一个javaBean生成对应的Json数据  
    public static String beanToJson(Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
//        LogUtil.d("whj beanToJson: " + json);
        return json;
    }
    /**
     * json 转 List<T>
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
        return ts;
    }

    public static String readJsonFile() {
        String filePath =  "/mnt/sdcard/CloudPoint/FileObjectStore/VMMacId.json";
        StringBuilder sb = new StringBuilder();
        try {
            File file = new File(filePath);
            if(file.exists()){
                InputStream in = null;
                in = new FileInputStream(file);
                int tempbyte;
                while ((tempbyte = in.read()) != -1) {
                    sb.append((char) tempbyte);
                }
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    public static String getMacId(){
        String macId = "";
        String jsonStr = readJsonFile();
        if(jsonStr != null && !jsonStr.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                macId = jsonObject.optString("mac", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return macId;
    }
}
