package com.makenv.cache;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/12.
 */
public class CountyCacheUtil {

    private static CountyCacheUtil instance;

    public  static CountyCacheUtil newInstance(){

        if(instance == null) {

            synchronized(CountyCacheUtil.class) {

                if(instance == null) {

                    instance = new CountyCacheUtil();

                }
            }
        }
        return instance;
    }


    public List<Map<String, Object>> getCountyList() {
        return countyList;
    }

    public void setCountyList(List<Map<String, Object>> countyList) {
        this.countyList = countyList;
    }

    private List<Map<String,Object>> countyList;

}
