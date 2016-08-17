package com.makenv.service.impl;

import com.makenv.cache.StationCacheUtil;
import com.makenv.config.FigConfig;
import com.makenv.domain.StationDetail;
import com.makenv.enums.UnitEnum;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.mapper.StationMapper;
import com.makenv.service.StationDetailService;
import com.makenv.service.StationService;
import com.makenv.util.RegionUtils;
import com.makenv.vo.StationDetailVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/9.
 */
@Service
public class StationDetailServiceImpl implements StationDetailService {


    private final static Logger logger = LoggerFactory.getLogger(StationDetailServiceImpl.class);

    @Autowired
    private StationDetailMapper stationDetailMapper;

    @Autowired
    private FigConfig figConfig;

    @Override

    public List<Map<String,Object>> selectStationDetailByTimeInterval(String startTime, String endTime) {

        return stationDetailMapper.selectStationDetailByTimeInterval(startTime,endTime);

    }

    @Override
    @Cacheable(key = "#stationCode+#range+#unit",value = "station")
    public List<Map<String, Object>> getAroundDataResult(String stationCode, Integer range, String unit) {


        if(UnitEnum.KIlOMETER.getName().equals(unit) || UnitEnum.KIlOMETER.getDescribe().equals(unit)) {

            Map<String,Object> map = StationCacheUtil.newInstance().getStation(stationCode);

            Double lon =  (Double)map.get("lon");//获得经度

            Double lat =  (Double)map.get("lat");//获得纬度

            if(!StringUtils.isEmpty(lon)&& !StringUtils.isEmpty(lat)) {

                Map<String,Object> map1 = RegionUtils.getRangeLonAndLat(lat,lon,range);

                return stationDetailMapper.selectAvgRange(map1);

            }

        }

        return null;
    }

    @Override
    @Cacheable(key = "#stationDetailVo",value = "station")
    public List<Map<String, Object>> getAroundDataResult(StationDetailVo stationDetailVo) throws Exception {

        if(UnitEnum.KIlOMETER.getName().equals(stationDetailVo.getUnit()) || UnitEnum.KIlOMETER.getDescribe().equals(stationDetailVo.getUnit())) {

            float lat = stationDetailVo.getLat();

            float lon = stationDetailVo.getLon();

            if(lon < figConfig.getXmax()&& lon > figConfig.getXmin() && lat < figConfig.getYmax() && lat > figConfig.getYmin()) {

                if(!StringUtils.isEmpty(lon)&& !StringUtils.isEmpty(lat)) {

                    double range = Double.parseDouble(stationDetailVo.getRange());

                    Map<String,Object> map1 = RegionUtils.getRangeLonAndLat(lat,lon,range);

                    return stationDetailMapper.selectAvgRange(map1);

                }

            }

            else {

                throw new Exception("经度纬度不在有效范围内");

            }

        }
        return null;
    }

    @Cacheable(key = "#year+#regionCode",value = "station")
    public Map<String, Object> getYearResultByCity(Integer year, String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        List<Map<String,Object>> resultList = null;

        if(year > 2013) {

            map.put("tableName","PM_25");
        }

        else {

            map.put("tableName","Sum_All_PM25_copy");
        }

        map.put("year",year);

    /*    map.put("regionCode",regionCode);*/

        map.put("stationList", StationCacheUtil.newInstance().getStationListByCityCode(regionCode));

        if(((List)map.get("stationList")).size() == 0){

            return null;
        }

        //省级平均
        switch (regionCode.length()) {

            case 2:

                resultList =  stationDetailMapper.selectByYearResultByProvince(map);

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        item.put(key,map1);

                    });

                });

                break;

            case 4:
                //是平均加超标日期
                List<Map<String,Object>> overStandardList = stationDetailMapper.selectOverStandardGroupByDate(map);

                overStandardList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("days", value);

                        item.put(key, map1);

                    });
                });

                Map<String,Object> overStandardMap = overStandardList.get(0);

                resultList =  stationDetailMapper.selectByYearResultByCity(map);

                resultList.forEach(item -> {

                    item.forEach((key, value) -> {

                        Map map1 = new HashMap<String, Object>();

                        map1.put("val", value);

                        if(overStandardMap.containsKey(key)) {

                            map1.put("days", ((Map)overStandardMap.get(key)).get("days"));

                        }

                        item.put(key, map1);

                    });

                });

                break;

            case 6:


                break;


            default:
                break;

        }

        if(resultList != null && resultList.size() == 1) {

            return resultList.get(0);
        }

        return null;
    }
}
