package com.makenv.util.aqi.model;

/**
 * Created by wgy on 2016/11/28.
 */
public class Aqi {

    public Aqi(int aqi, String primaryPollutant) {
        this.aqi = aqi;
        this.primaryPollutant = primaryPollutant;
    }

    private int aqi;
    private String primaryPollutant;

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public String getPrimaryPollutant() {
        return primaryPollutant;
    }

    public void setPrimaryPollutant(String primaryPollutant) {
        this.primaryPollutant = primaryPollutant;
    }
}
