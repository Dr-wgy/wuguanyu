package com.makenv.service;

import com.makenv.condition.StationDetailCondition;
import com.makenv.domain.StationDetail;
import com.makenv.vo.CityParamVo;
import com.makenv.vo.LonLatVo;
import com.makenv.vo.RankAreaData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wgy on 2016/8/9.
 */
public interface StationDetailService {

    List<StationDetail> selectStationDetailByTimeInterval(String startTime, String endTime);

    List<Map<String,Object>> getAroundDataResult(String stationCode, Integer range, String unit);

    List<Map<String,Object>> getAroundDataResult(LonLatVo lonLatVo) throws Exception;

    Map<String, Object> getAvgYearResultByRegionCode(Integer year, String regionCode);

    Map<String,Object> getLastTimeSpanResultData(String regionCode, Integer timeSpan, String unit);

    Map<String,Object> getLast24ResultData(String area, String areaId);

    Map<String,Object> getAvgMonthResultByRegionCode(Integer year, Integer month, String regionCode);

    Map<String,Object> getAllCurrentPlace(CityParamVo cityParamVo);

    List<Map<String,Object>> getRankMonResultRegionCode(List<String> regionCode, LocalDateTime year, List<String> areas, String tUnit);

    List getRankResultDataByArea(StationDetailCondition parameter, LocalDateTime startTime, LocalDateTime endTime);

    List getRankResultDataByRes(Integer year, Integer month, Integer date, Set<String> regionCode);

    Map getRankResultDataByRe(String tableName,String tunit,LocalDateTime startTime,LocalDateTime endTime,String regionCode);

    Map getRankResultDataByArea(StationDetailCondition stationDetailCondition);

    Map<String,Object> getAvgDateResultByRegionCode(Integer year, Integer month, Integer date, String regionCode);

    Map<String,Object> getAvgResultByAreaOrStation(LocalDateTime startDateTime, LocalDateTime startDateTime1, String area, String areaId, String unit);

    List getQualityData(Integer year, Integer month, String city, String timeSpan, String timeUnit);

    Map getHevData(Integer year, Integer month, String city, String timeSpan, String timeUnit);

    Map<String,Object> getAllMonResult(String city);

    Map getRankALLResultDataByArea();

    Map<String,Object> getALLDate(String city, Integer year, Integer month);
}
