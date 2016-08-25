package com.makenv.cache;

import com.makenv.domain.Station;
import com.makenv.vo.StationVo;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

            return stationList.stream().filter(station -> (station != null && station.getCityId() != null)?station.getCityId().equals(cityId):false).collect(Collectors.toList());
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

            return stationList.stream().filter(station -> (station != null && station.getRegionId() != null)?station.getRegionId().startsWith(code):false).distinct().collect(Collectors.toList());

        }

        return null;
    }

    public StationVo getStation(String stationCode){

        return this.stationList.stream().filter(station -> (station != null && station.getStationId() != null)?station.getStationId().equals(stationCode):false).findFirst().orElse(null);


    }

    public static void main(String[] args) {

        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();

        Map map = new HashMap();

        map.put("regionId",110100);

        map = new HashMap();

        list.add(map);

        map.put("regionId", 120100);

        map = new HashMap();

        list.add(map);

        map.put("regionId", 130100);

        map.put("regionName", "haha");

        list.add(map);

       // StationCacheUtil.newInstance().setStationList(list);

        List list1 = StationCacheUtil.newInstance().getStationListByCityCode("130100");

        System.out.println(list1);

    }
}
