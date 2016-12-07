package com.makenv.util;

import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.CountyCacheUtil;
import com.makenv.cache.ProvinceCacheUtil;
import javafx.collections.ObservableMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/8/12.
 */
public class RegionUtils {

    //半径
    private  static long radius = 6371;

    private static String unit = "公里";

    private static  double  radianTo;

    public static long getRadius() {
        return radius;
    }

    public static void setRadius(long radius) {
        RegionUtils.radius = radius;
    }

    /**
     *
     * @param latA  纬度
     * @param logA  经度
     * @param range 范围
     * @param latB  纬度
     * @param logB  经度 以b为中心点
     * @return
     */
    public boolean judgeInRangeUseCircle(double latA, double logA,double latB, double logB ,double range){

        boolean flag = radius * Math.acos(MathUtils.cos(Math.toRadians(latA)) * MathUtils.cos(Math.toRadians(latB)) *
                MathUtils.cos(Math.toRadians(logA)- MathUtils.cos(logB)) + MathUtils.sin(Math.toRadians(latA))* MathUtils.sin(Math.toRadians(latB)))
                <= range;

        return flag;
    }

    /**
     *
     * @param latA 纬度
     * @param logA 经度
     * @param latB 纬度
     * @param logB 经度 以B为中心点
     * @param range 范围
     * @return
     */
    public static boolean judgeInRangeUseRectangle(double latA, double logA,double latB, double logB ,double  range){

        double radian = range / radius;

        double degree = Math.toDegrees(radian);

        if(Math.abs(latA - latB) <= degree && Math.abs(logA - logB) <= degree) {

            return true;
        }
        else {

            return false;
        }
    }

    public static Map<String,Object> getRangeLonAndLat(double latB, double logB ,double  range){

        Map<String,Object> map =new HashMap<String, Object>();

        double radian = range/radius;

        map.put("latMax",latB + Math.toDegrees(radian));

        map.put("latMin",latB - Math.toDegrees(radian));

        map.put("lonMax",logB + Math.toDegrees(radian));

        map.put("lonMin",logB - Math.toDegrees(radian));

        return map;
    }

    //转换代码
    public static String convertRegionCode(String regionCode){

        if(regionCode != null && regionCode.length()!= 0) {

            if(regionCode.length()== 6) {

                if(regionCode.endsWith("0000")){

                    return regionCode.substring(0,2);
                }
                else if (regionCode.endsWith("00")){

                    return regionCode.substring(0,4);
                }
            }
            else if(regionCode.length()== 4) {

                if (regionCode.endsWith("00")){

                    return regionCode.substring(0,2);
                }
            }
        }

        return regionCode;

    }

    public static String getRegionName(String regionCode) {

        String regionName = "";

        switch (regionCode.length()) {

            case 2:

                regionName = ProvinceCacheUtil.newInstance().getArea(regionCode);

                break;

            case 4:

                regionName = CityCacheUtil.newInstance().getArea(regionCode);

                break;

            case 6:

                regionName = CountyCacheUtil.newInstance().getArea(regionCode);



        }


        return regionName;
    }

    public static String getParentName(String regionCode, String regionName) {

        String parentName = "";

        switch (regionCode.length()) {

            case 2:

                return regionName;

            case 4:

                parentName = ProvinceCacheUtil.newInstance().getArea(regionCode.substring(0,2));

                break;

            case 6:

                parentName = CityCacheUtil.newInstance().getArea(regionCode.substring(0,4));

        }

        if(StringUtils.isEmpty(parentName)) {

            parentName = regionName;
        }


        return parentName;
    }

    public static Set convertRegionCode(Set regionCode){

        Set set = new HashSet();

        regionCode.stream().forEach(str->{

            set.add(convertRegionCode(str.toString()));

         });

        return set;

    }

}
