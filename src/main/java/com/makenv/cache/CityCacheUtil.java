package com.makenv.cache;



import com.makenv.domain.City;
import com.makenv.vo.CityVo;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/8.
 */
public class CityCacheUtil {

    private static CityCacheUtil instance;

    public  static CityCacheUtil newInstance(){

        if(instance == null) {

            synchronized(CityCacheUtil.class) {

                if(instance == null) {

                    instance = new CityCacheUtil();

                }
            }
        }
        return instance;
    }


    public List<CityVo> getCityList() {
        return cityList;
    }

    public void setCityList(List<CityVo> cityList) {
        this.cityList = cityList;
    }

    private List<CityVo> cityList;

    public CityVo getCityByRegionCode(String regionCode){

        return this.cityList.stream().filter(city->(city!= null && city.getRegionId()!=null)?city.getRegionId().startsWith(regionCode):false).findFirst().orElse(null);
    }

    public String getRegionCode(String area){

       CityVo cityVo = this.cityList.stream().filter(
               city ->(city != null && city.getRegionName()!=null)?city.getRegionName().startsWith(area):false)
               .findFirst().orElse(null);

        if( cityVo != null) {

            return cityVo.getRegionId();

        }

        return null;
    }

}
