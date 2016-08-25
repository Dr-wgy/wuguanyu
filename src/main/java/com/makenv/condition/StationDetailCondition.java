package com.makenv.condition;

import com.makenv.cache.CityCacheUtil;
import com.makenv.vo.CityVo;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/8/23.
 */
public class StationDetailCondition implements Serializable {

    private static final long serialVersionUID = -8593575815017421448L;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer date) {
        this.date = date;
    }

    public List<String> getAreas() {

        if(areas == null) {

            areas = CityCacheUtil.newInstance().getCityList().stream()
                    .map(CityVo::getRegionName).limit(74)
                    .collect(Collectors.toList());

        }
        return areas;
    }

    public void setAreas(List<String> areas) {

        if(areas != null && areas.size()!= 0) {

            this.areas = areas.stream().sorted((item1,item2)->{

                return item1.compareTo(item2);

            }).collect(Collectors.toList());
        }
        else {

            this.areas = areas;
        }
    }

    public String getTunit() {
        return tunit;
    }

    public void setTunit(String tunit) {
        this.tunit = tunit;
    }

    private String tunit = "m";//最小单位

    private transient Integer year;

    private transient Integer month;

    private transient Integer date;

    private List<String> areas;

    public Integer getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(Integer timeSpan) {
        this.timeSpan = timeSpan;
    }

    private transient Integer timeSpan = 12;

    public String getRedisKey() {
        return redisKey;
    }

    public void setRedisKey(String redisKey) {
        this.redisKey = redisKey;
    }

    private String redisKey;

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    private Integer hour;

    public String getTableName() {

        this.tableName ="PM_25";

        if(year != null) {

            if(year > 2013) {

                this.tableName ="PM_25";
            }

            else {

                this.tableName = "Sum_All_PM25_copy";
            }

        }
        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }

    private String tableName;

}
