package com.makenv.vo;

/**
 * Created by wgy on 2016/8/17.
 */
public class StationVo {

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    private  String stationId;
    private  String stationName;
    private  double lat;
    private  double lon;
    private String regionId;
    private String cityName;

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    private String adCode;

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    private Integer cityId;

    @Override
    public String toString() {
        return "StationVo{" +
                "stationId='" + stationId + '\'' +
                ", stationName='" + stationName + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", regionId='" + regionId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", adCode='" + adCode + '\'' +
                ", cityId=" + cityId +
                '}';
    }
}
