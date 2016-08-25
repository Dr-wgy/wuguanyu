package com.makenv.enums;

/**
 * Created by wgy on 2016/8/17.
 */
public enum UnitTimeEnum {


    HOUR("h","小时"),

    DAY("d","天"),

    MONTH("m","月");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    private String name;

    private String describe;



    private UnitTimeEnum(String name, String describe) {

        this.name = name;

        this.describe = describe;
    }
}
