package com.makenv.vo;

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

        if(isTimePointOrTimeInterval) {

            LocalDateTime now = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));

            dateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));

            if(now.getYear() <= 2013){


                return "Sum_All_PM25_copy";
            }
            else {

                return "PM_25";
            }

        }

        else {

            LocalDateTime after = LocalDateTime.parse(afterTime, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));

            afterTime = after.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));

            LocalDateTime before = LocalDateTime.parse(beforeTime, DateTimeFormatter.ofPattern("yyyy-MM-dd_HH"));

            beforeTime = before.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00"));

            if(after.getYear() <= 2013){


                return "Sum_All_PM25_copy";
            }
            else {


                return "PM_25";
            }
        }



    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private String tableName;

    public String getArea() {

        if(area.equals("regionCode")){

            return "Area";
        }
        else if(area.equals("stationCode")) {

            return "StationCode";

        }

        return "Area";
    }

    public void setArea(String area) {

        this.area = area;
    }

    public boolean isTimePointOrTimeInterval() {
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
