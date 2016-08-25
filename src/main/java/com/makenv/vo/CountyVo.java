package com.makenv.vo;

/**
 * Created by Administrator on 2016/8/17.
 */
public class CountyVo {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    private Integer id;

    private String regionId;

    private String regionName;

}
