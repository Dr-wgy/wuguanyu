package com.makenv.cache;

import com.makenv.vo.ProvinceVo;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/12.
 */
public class ProvinceCacheUtil {

    private static ProvinceCacheUtil instance;

    public  static ProvinceCacheUtil newInstance(){

        if(instance == null) {

            synchronized(ProvinceCacheUtil.class) {

                if(instance == null) {

                    instance = new ProvinceCacheUtil();

                }
            }
        }
        return instance;
    }


    public List<ProvinceVo> getProvinceList() {
        return provinceList;
    }

    public void setProvinceList(List<ProvinceVo> provinceList) {
        this.provinceList = provinceList;
    }

    private List<ProvinceVo> provinceList;

    public String getArea(String regionId) {

        if(StringUtils.isEmpty(regionId)) {

            return null;
        }

        for (ProvinceVo provinceVo:provinceList) {

            if(regionId.equals(provinceVo.getRegionId())) {

                return provinceVo.getRegionName();
            }


        }
        return null;
    }


}
