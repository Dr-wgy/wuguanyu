package com.makenv.cache;



import com.makenv.domain.City;
import com.makenv.util.RegionUtils;
import com.makenv.vo.CityVo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<CityVo> getKeyCities(){


        return this.cityList.stream().limit(74).collect(Collectors.toList());


    }

    public List<CityVo> getkeyCityListByPro(String provinceId){

        final String provinceId1 = RegionUtils.convertRegionCode(provinceId);

        return this.getKeyCities().stream().filter(cityVo -> cityVo!=null && cityVo.getCityId() != null?cityVo.getRegionId().startsWith(provinceId1):false).collect(Collectors.toList());

    }

    public List<CityVo> getCityListByPro(String provinceId) {

        final String provinceId1 = RegionUtils.convertRegionCode(provinceId);


        return this.cityList.stream().filter(cityVo -> cityVo!=null && cityVo.getCityId() != null?cityVo.getRegionId().startsWith(provinceId1):false).collect(Collectors.toList());

    }
}
