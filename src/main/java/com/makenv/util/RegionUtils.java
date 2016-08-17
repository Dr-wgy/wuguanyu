package com.makenv.util;

import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Map;

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
}
