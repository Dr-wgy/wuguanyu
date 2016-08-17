package com.makenv.domain;

import java.util.Date;

public class StationDetail {
    private Integer aqi;

    private String area;

    private Float co;

    private Float co24h;

    private Integer id;

    private Float latitude;

    private Float longitude;

    private Float no2;

    private Float no224h;

    private Float o3;

    private Float o324h;

    private Float o38h;

    private Float o38h24h;

    private Integer orderid;

    private Float pm10;

    private Float pm1024h;

    private Float pm25;

    private Float pm2524h;

    private String positionname;

    private String primarypollutant;

    private String quality;

    private Float so2;

    private Float so224h;

    private String stationcode;

    private Date timepoint;

    public Integer getAqi() {
        return aqi;
    }

    public void setAqi(Integer aqi) {
        this.aqi = aqi;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area == null ? null : area.trim();
    }

    public Float getCo() {
        return co;
    }

    public void setCo(Float co) {
        this.co = co;
    }

    public Float getCo24h() {
        return co24h;
    }

    public void setCo24h(Float co24h) {
        this.co24h = co24h;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getNo2() {
        return no2;
    }

    public void setNo2(Float no2) {
        this.no2 = no2;
    }

    public Float getNo224h() {
        return no224h;
    }

    public void setNo224h(Float no224h) {
        this.no224h = no224h;
    }

    public Float getO3() {
        return o3;
    }

    public void setO3(Float o3) {
        this.o3 = o3;
    }

    public Float getO324h() {
        return o324h;
    }

    public void setO324h(Float o324h) {
        this.o324h = o324h;
    }

    public Float getO38h() {
        return o38h;
    }

    public void setO38h(Float o38h) {
        this.o38h = o38h;
    }

    public Float getO38h24h() {
        return o38h24h;
    }

    public void setO38h24h(Float o38h24h) {
        this.o38h24h = o38h24h;
    }

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public Float getPm10() {
        return pm10;
    }

    public void setPm10(Float pm10) {
        this.pm10 = pm10;
    }

    public Float getPm1024h() {
        return pm1024h;
    }

    public void setPm1024h(Float pm1024h) {
        this.pm1024h = pm1024h;
    }

    public Float getPm25() {
        return pm25;
    }

    public void setPm25(Float pm25) {
        this.pm25 = pm25;
    }

    public Float getPm2524h() {
        return pm2524h;
    }

    public void setPm2524h(Float pm2524h) {
        this.pm2524h = pm2524h;
    }

    public String getPositionname() {
        return positionname;
    }

    public void setPositionname(String positionname) {
        this.positionname = positionname == null ? null : positionname.trim();
    }

    public String getPrimarypollutant() {
        return primarypollutant;
    }

    public void setPrimarypollutant(String primarypollutant) {
        this.primarypollutant = primarypollutant == null ? null : primarypollutant.trim();
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality == null ? null : quality.trim();
    }

    public Float getSo2() {
        return so2;
    }

    public void setSo2(Float so2) {
        this.so2 = so2;
    }

    public Float getSo224h() {
        return so224h;
    }

    public void setSo224h(Float so224h) {
        this.so224h = so224h;
    }

    public String getStationcode() {
        return stationcode;
    }

    public void setStationcode(String stationcode) {
        this.stationcode = stationcode == null ? null : stationcode.trim();
    }

    public Date getTimepoint() {
        return timepoint;
    }

    public void setTimepoint(Date timepoint) {
        this.timepoint = timepoint;
    }
}