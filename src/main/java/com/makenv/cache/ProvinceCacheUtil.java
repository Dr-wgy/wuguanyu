package com.makenv.cache;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/12.
 */
public class ProvinceCacheUtil {

    private static ProvinceCacheUtil instance;

    public  static ProvinceCacheUtil newInstance(){

        if(instance == null) {

            synchronized(ProvinceCacheUtil.class) {

                if(instance == null) {

                    instance = new ProvinceCacheUtil();

                }
            }
        }
        return instance;
    }


    public List<Map<String,Object>> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<Map<String,Object>> provinceList) {
        this.provinceList = provinceList;
    }

    private List<Map<String,Object>> provinceList;


}
