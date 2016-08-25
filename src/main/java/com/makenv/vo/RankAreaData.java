package com.makenv.vo;

import com.sun.xml.internal.ws.developer.Serialization;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by wgy on 2016/8/23.
 */
public class RankAreaData implements Serializable {

    private static final long serialVersionUID = 8809663031120724956L;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double getVal_SO2() {
        return val_SO2;
    }

    public void setVal_SO2(double val_SO2) {
        this.val_SO2 = val_SO2;
    }

    public double getVal_NO2() {
        return val_NO2;
    }

    public void setVal_NO2(double val_NO2) {
        this.val_NO2 = val_NO2;
    }

    public double getVal_PM_25() {
        return val_PM_25;
    }

    public void setVal_PM_25(double val_PM_25) {
        this.val_PM_25 = val_PM_25;
    }

    public double getVal_PM_10() {
        return val_PM_10;
    }

    public void setVal_PM_10(double val_PM_10) {
        this.val_PM_10 = val_PM_10;
    }

    public double getVal_CO() {
        return val_CO;
    }

    public void setVal_CO(double val_CO) {
        this.val_CO = val_CO;
    }

    public double getVal_O3_8h() {
        return val_O3_8h;
    }

    public void setVal_O3_8h(double val_O3_8h) {
        this.val_O3_8h = val_O3_8h;
    }

    public double getVal_AQI() {
        return val_AQI;
    }

    public void setVal_AQI(double val_AQI) {
        this.val_AQI = val_AQI;
    }

    public double getVal_O3() {
        return val_O3;
    }

    public void setVal_O3(double val_O3) {
        this.val_O3 = val_O3;
    }

    public Integer getRankO3() {
        return rankO3;
    }

    public void setRankO3(Integer rankO3) {
        this.rankO3 = rankO3;
    }

    public Integer getRankAQI() {
        return rankAQI;
    }

    public void setRankAQI(Integer rankAQI) {
        this.rankAQI = rankAQI;
    }

    public Integer getRankO3_8h() {
        return rankO3_8h;
    }

    public void setRankO3_8h(Integer rankO3_8h) {
        this.rankO3_8h = rankO3_8h;
    }

    public Integer getRankCO() {
        return rankCO;
    }

    public void setRankCO(Integer rankCO) {
        this.rankCO = rankCO;
    }

    public Integer getRankPM10() {
        return rankPM10;
    }

    public void setRankPM10(Integer rankPM10) {
        this.rankPM10 = rankPM10;
    }

    public Integer getRankPM2_5() {
        return rankPM2_5;
    }

    public void setRankPM2_5(Integer rankPM2_5) {
        this.rankPM2_5 = rankPM2_5;
    }

    public Integer getRankNO2() {
        return rankNO2;
    }

    public void setRankNO2(Integer rankNO2) {
        this.rankNO2 = rankNO2;
    }

    public Integer getRankSO2() {
        return rankSO2;
    }

    public void setRankSO2(Integer rankSO2) {
        this.rankSO2 = rankSO2;
    }

    private String area;

    private double val_SO2;

    private double val_NO2;

    private double val_PM_25;

    private double val_PM_10;

    private double val_CO;

    private double val_O3_8h;

    private double val_AQI;

    private double val_O3;

    private Integer rankO3;

    private Integer rankAQI;

    private Integer rankO3_8h;

    private Integer rankCO;

    private Integer rankPM10;

    private Integer rankPM2_5;

    private Integer rankNO2;

    private Integer rankSO2;

    public String getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(String timePoint) {
        this.timePoint = timePoint;
    }

    private String timePoint;





}
