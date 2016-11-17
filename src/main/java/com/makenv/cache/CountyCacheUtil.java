package com.makenv.cache;

import com.makenv.vo.CityVo;
import com.makenv.vo.CountyVo;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2016/8/12.
 */
public class CountyCacheUtil {

    private static CountyCacheUtil instance;

    public  static CountyCacheUtil newInstance(){

        if(instance == null) {

            synchronized(CountyCacheUtil.class) {

                if(instance == null) {

                    instance = new CountyCacheUtil();

                }
            }
        }
        return instance;
    }


    public List<CountyVo> getCountyList() {
        return countyList;
    }

    public void setCountyList(List<CountyVo> countyList) {
        this.countyList = countyList;
    }

    private List<CountyVo> countyList;

    public CountyVo getCountyByRegionCode(String regionCode){

        return this.countyList.stream().filter(county->(county!= null && county.getRegionId()!=null)?county.getRegionId().equals(regionCode):false).findFirst().orElse(null);
    }

    public List getCountyListByCity(String specialCityId) {

        return this.countyList.stream().filter(county -> (county != null && county.getRegionId() != null) ? county.getRegionId().startsWith(specialCityId) : false).collect(Collectors.toList());
    }

    public String getArea(String regionCode) {

        if(StringUtils.isEmpty(regionCode)) {

            return null;
        }

        for (CountyVo countyVo:countyList) {

            if(regionCode.equals(countyVo.getRegionId())) {

                return countyVo.getRegionName();
            }


        }

        return null;
    }
}
