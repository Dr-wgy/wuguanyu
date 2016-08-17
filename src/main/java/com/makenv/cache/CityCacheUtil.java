package com.makenv.cache;



import com.makenv.domain.City;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/8.
 */
public class CityCacheUtil {

    private static CityCacheUtil instance;

    public  static CityCacheUtil newInstance(){

        if(instance == null) {

            synchronized(CityCacheUtil.class) {

                if(instance == null) {

                    instance = new CityCacheUtil();

                }
            }
        }
        return instance;
    }


    public List<Map<String,Object>> getCityList() {
        return cityList;
    }

    public void setCityList(List<Map<String,Object>> cityList) {
        this.cityList = cityList;
    }

    private List<Map<String,Object>> cityList;

}
