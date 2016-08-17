package com.makenv.mapper;

import com.makenv.domain.StationDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StationDetailMapper {
    int insert(StationDetail record);

    int insertSelective(StationDetail record);

    List<Map<String,Object>> selectStationDetailByTimeInterval(@Param("startTime")String startTime, @Param("endTime")String endTime);

    List<Map<String,Object>> selectAvgRange(Map<String, Object> map1);

    /*List<Map<String,Object>> selectByYearResult(Map map);*/

    List<Map<String,Object>> selectByYearResultCounty(Map<String, Object> map);

    List<Map<String,Object>> selectByYearResultByProvince(Map<String, Object> map);

    List<Map<String,Object>> selectByYearResultByCity(Map<String, Object> map);

    List<Map<String,Object>> selectOverStandardGroupByDate(Map<String, Object> map);
    
}