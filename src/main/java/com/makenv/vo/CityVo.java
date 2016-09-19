package com.makenv.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by wgy on 2016/8/8.
 */
public class CityVo {

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }
    @JsonIgnore
    private Integer cityId;

    private String regionName;

    private String regionId;

    private String lon;

    private String lat;
}
