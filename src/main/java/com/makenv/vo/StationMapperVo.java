package com.makenv.vo;

import java.util.List;

/**
 * Created by wgy on 2016/8/19.
 */
public class StationMapperVo {

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List getStationList() {
        return stationList;
    }

    public void setStationList(List stationList) {
        this.stationList = stationList;
    }

    public StationMapperVo(String startTime, String endTime, List stationList) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.stationList = stationList;
    }

    public StationMapperVo(){

    }

    private String startTime;

    private String endTime;

    private List stationList;
}
