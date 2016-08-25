package com.makenv.vo;

/**
 * Created by wgy on 2016/8/16.
 */
public class LonLatVo {

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getUnit() {

        if(unit == null) return "km";

        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStaCode() {
        return staCode;
    }

    public void setStaCode(String staCode) {
        this.staCode = staCode;
    }

    public String getStaName() {
        return staName;
    }

    public void setStaName(String staName) {
        this.staName = staName;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    private float lon;//经度

    private float lat;//纬度

    private String range;//范围

    private String unit = "km";//单位

    private String staCode;//站点Code

    private String staName;
}
