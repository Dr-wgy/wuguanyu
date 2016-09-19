package com.makenv.mapper;

import com.makenv.condition.StationDetailCondition;
import com.makenv.domain.StationDetail;
import com.makenv.vo.CityParamVo;
import com.makenv.vo.RankAreaData;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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

    List<Map> getRankResultDataByArea(@Param("preSql")String preSql,@Param("headSql")String headSql, @Param("parameterModel") StationDetailCondition parameter, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("tailSql")String tailSql);

    int updateStatus(@Param("preSql")String preSql);

    List getRankResultDataByRe(@Param("unit")String unit, @Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime, @Param("stationList")List list);

    Map<String,Object> getAvgResultByRe(@Param("tableName")String tableName, @Param("tunit")String tunit, @Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime endTime,@Param("stationList") List stationList);

    Map<String,Object> getAvgMonthResultByCity(Map<String, Object> map);

    Map<String,Object> getAvgMonthResultByProvince(Map<String, Object> map);

    List<Map<String,Object>> selectAvgYearResultByProvince(Map<String, Object> map);

    List<Map<String,Object>> selectAvgYearResultByCity(Map<String, Object> map);

    List<Map<String,Object>> getLastTimeSpanResultDataByJING_JIN_JI(Map<String, Object> map);

    List<Map<String,Object>> getLastTimeSpanResultDataByNOT_JING_JIN_JI(Map<String, Object> map);

    List<Map<String,Object>> getAllCurrentPlaceByJING_JIN_JI(@Param("city")CityParamVo cityParamVo, @Param("extraMap")Map map);

    List<Map<String,Object>> getAllCurrentPlaceByNOT_JING_JIN_JI(@Param("city")CityParamVo cityParamVo, @Param("extraMap")Map map);

    List<String> getRepeatStationCodes();

    List<Map<String,Object>> getAvgResultByAreaOrStation(@Param("startDateTime")LocalDateTime startDateTime, @Param("endDateTime")LocalDateTime endDateTime, @Param("area")String area, @Param("stationIds")List<String> stationIds,@Param("unit") String unit, @Param("tableName")String tableName);

    Map<String,Object> getQualityData(@Param("startTime")LocalDateTime endTime,@Param("endTime")LocalDateTime plus,@Param("stationList") List<String> stationCodes,@Param("unit") String unit);

    Map<String,Object> getHevData(@Param("startTime") LocalDateTime endTime, @Param("endTime") LocalDateTime plus, @Param("repeatStationCodes")List<String> repeatCodes, @Param("stationList") List<String> stationCodes, @Param("unit") String unit,@Param("tailSql") String tailSql);

    Map getMaxMinResultByCity(Map map);

    List getRankALLResultDataByArea(@Param("preSql")String s,@Param("headSql")String s1, @Param("parameterModel")Map parameterMap,@Param("tailSql") String tailSql);

    List<Map> getAvgDateByArea(@Param("tableName")String tableName, @Param("area") String area, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("repeatCodes") List repeatCodes);

    List<Map> getMax_minDataByArea(@Param("tableName")String tableName, @Param("area") String area, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("repeatCodes") List repeatCodes);

    String getMaxTimePoint();
}