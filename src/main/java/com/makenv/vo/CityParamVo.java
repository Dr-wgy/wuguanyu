package com.makenv.vo;

import com.makenv.util.DateUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by wgy on 2016/7/13.
 *
 */
public class CityParamVo {

    private String area;

    private boolean isTimePointOrTimeInterval = true;

    private String dateTime;

    private String beforeTime;

    private String afterTime;

    public String getTableName() {

        return tableName;

    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private String tableName;

    public String getArea() {

        if("area".equals(area)){

            return "Area";
        }
        else if("station".equals(area)) {

            return "StationCode";

        }

        return "Area";
    }

    public void setArea(String area) {

        this.area = area;
    }

    public boolean getIsTimePointOrTimeInterval() {
        return isTimePointOrTimeInterval;
    }

    public void setIsTimePointOrTimeInterval(boolean isTimePointOrTimeInterval) {

        this.isTimePointOrTimeInterval = isTimePointOrTimeInterval;
    }

    public String getDateTime() {

        return dateTime;
    }

    public void setDateTime(String dateTime) {

        this.dateTime = dateTime;
    }

    public String getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(String beforeTime) {
        this.beforeTime = beforeTime;
    }

    public String getAfterTime() {
        return afterTime;
    }

    public void setAfterTime(String afterTime) {
        this.afterTime = afterTime;
    }



    //代码中有问题如果2013在一张表中,2014在另一张表中
}
