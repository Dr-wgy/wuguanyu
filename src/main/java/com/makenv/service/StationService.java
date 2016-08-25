package com.makenv.service;

import com.makenv.domain.City;
import com.makenv.domain.Station;
import com.makenv.vo.StationVo;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/6.
 * 站点服务,一定是和站点有关系
 */
public interface  StationService {

    List<StationVo> getAllStations();

    List getStationByCity(String city);

    List getStationByCity1(City city);

}
