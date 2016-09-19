package com.makenv.cache;

import com.makenv.domain.Station;
import com.makenv.vo.StationVo;

import javax.validation.constraints.Null;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/8/8.
 */
public class StationCacheUtil {

    private static StationCacheUtil instance;

    public  static StationCacheUtil newInstance(){

        if(instance == null) {

            synchronized(StationCacheUtil.class) {

                if(instance == null) {

                    instance = new StationCacheUtil();

                }
            }
        }
        return instance;
    }

    public List<StationVo> getStationList() {
        return stationList;
    }

    public void setStationList(List<StationVo> stationList) {
        this.stationList = stationList;
    }

    private List<StationVo> stationList;

    /**
     * 通过城市名字其下面的stationList
     * @param cityName
     * @return
     */
    public List<StationVo> getStationListByCityName(String cityName){

        if(this.stationList !=null){

            return stationList.stream().filter(station -> (station !=null && station.getCityName()!= null)?station.getCityName().equals(cityName):false).collect(Collectors.toList());
        }

        return null;
    }

    /**
     * 通过城市id其下面的stationList
     * @param cityId
     * @return
     */
    public List<StationVo> getStationListByCityId(Integer cityId){

        if(this.stationList !=null){

            return stationList.stream().filter(station -> (station != null && station.getCityId() != null) ? station.getCityId().equals(cityId) : false).collect(Collectors.toList());
        }

        return null;
    }

    /**
     * 通过城市Code
     * @param code
     * @return
     */
    public List<StationVo> getStationListByCityCode(String code){

        if(code != null && code.length() >= 2) {

            return stationList.stream().filter(station -> (station != null && (station.getRegionId() != null||station.getAdCode() != null)) ? station.getRegionId().startsWith(code) : false).distinct().collect(Collectors.toList());

        }

        return null;
    }

    public List<StationVo> getStationListByAdCode(String adCode){

        if(adCode != null && adCode.length() >= 2) {

            return stationList.stream().filter(station -> (station != null && station.getAdCode() != null) ? station.getAdCode().startsWith(adCode) : false).distinct().collect(Collectors.toList());

        }

        return null;
    }

    public List<String> getStationIdList(String code) {

        return getStationListByCityCode(code).stream().map(StationVo::getStationId).collect(Collectors.toList());
    }

    public StationVo getStation(String cityCode){

        return this.stationList.stream().filter(station -> (station != null && station.getStationId() != null) ? station.getStationId().equals(cityCode) : false).findFirst().orElse(null);


    }
}
