package com.makenv.mapper;

import com.makenv.domain.City;
import com.makenv.domain.Station;

import java.util.List;
import java.util.Map;

public interface StationMapper {
    int deleteByPrimaryKey(String stationid);

    int insert(Station record);

    int insertSelective(Station record);

    Station selectByPrimaryKey(String stationid);

    int updateByPrimaryKeySelective(Station record);

    int updateByPrimaryKey(Station record);

    List<Map<String,Object>> selectAllStation();

    List<Station> getStationByCity(String city);

    List getStationByCity1(City city);
}