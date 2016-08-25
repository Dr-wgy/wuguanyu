package com.makenv.mapper;

import com.makenv.condition.StationDetailCondition;
import com.makenv.domain.StationDetail;
import com.makenv.vo.CityParamVo;
import com.makenv.vo.RankAreaData;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StationDetailMapper {
    int insert(StationDetail record);

    int insertSelective(StationDetail record);

    List<StationDetail> selectStationDetailByTimeInterval(@Param("startTime")String startTime, @Param("endTime")String endTime);

    List<Map<String,Object>> selectAvgRange(Map<String, Object> map1);

    //selectOverStandardGroupByDate1和selectOverStandardGroupByDate的区别只是时间的选择
    List<Map<String,Object>> selectOverStandardGroupByDate(Map<String, Object> map);

    List<Map<String,Object>> getLastTimeSpanResultData(Map<String, Object> map);

    List<Map<String,Object>> selectAvgYearResultByStationCode(Map<String, Object> map);

    List<Map<String,Object>> getAvgMonthResultByStationCode(Map<String, Object> map);

    List<Map<String,Object>> selectOverStandardGroupByDate1(Map<String, Object> map);

    List<Map<String,Object>> getAllCurrentPlace(CityParamVo cityParamVo);

    List<Map<String,Object>> getRankResultMonthDataByArea(@Param("listArea") List<String> listArea, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("tableName")String tableName);

    List<RankAreaData> getRankResultDataByArea(@Param("preSql")String preSql,@Param("headSql")String headSql, @Param("parameterModel") StationDetailCondition parameter, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("tailSql")String tailSql);

    int updateStatus(@Param("preSql")String preSql);

    List getRankResultDataByRe(@Param("unit")String unit, @Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime, @Param("stationList")List list);

    Map<String,Object> getAvgResultByRe(@Param("tableName")String tableName, @Param("tunit")String tunit, @Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime,@Param("stationList") List stationList);
}