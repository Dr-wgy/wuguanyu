package com.makenv.enums;

/**
 * Created by wgy on 2016/8/15.
 */
public enum UnitLengthEnum {

    KIlOMETER("km","千米");

    // 成员变量
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

    private UnitLengthEnum(String name, String describe) {

        this.name = name;

        this.describe = describe;
    }
}
