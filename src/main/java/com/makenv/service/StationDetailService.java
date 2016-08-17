package com.makenv.service;

import com.makenv.vo.StationDetailVo;

import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/9.
 */
public interface StationDetailService {

    List<Map<String,Object>> selectStationDetailByTimeInterval(String startTime, String endTime);

    List<Map<String,Object>> getAroundDataResult(String stationCode, Integer range, String unit);

    List<Map<String,Object>> getAroundDataResult(StationDetailVo stationDetailVo) throws Exception;

    Map<String, Object> getYearResultByCity(Integer year, String regionCode);
}
