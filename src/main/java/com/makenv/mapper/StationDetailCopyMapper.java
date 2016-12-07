package com.makenv.mapper;

import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/11/30.
 */
public interface StationDetailCopyMapper {

    Map getEveryDayRegionCode(@Param("startTime")LocalDateTime cacheStartTime, @Param("endTime")LocalDateTime cacheEndTime, @Param("regionId")String regionCode);

    List getAvgSpeciesResult1(@Param("timePoint")LocalDateTime standardTime, @Param("regionCodes")List<String> regionCodes);

    Map getAvgSpeciesResult(@Param("timePoint")LocalDateTime standardTime, @Param("regionId")String regionCode);

    Map getAvgSpeciesResultByProvinceOrCity(@Param("timePoint")LocalDateTime standardTime, @Param("regionId")String regionCode);

    Map getEveryDayByProvinceOrCity(@Param("startTime")LocalDateTime yesterday, @Param("endTime")LocalDateTime endDateTime, @Param("regionId")String regionCode);

    List<Map<String,Object>> getAvgSpeciesResultByCounty(@Param("timePoint")LocalDateTime endHourTime);

    List<Map<String,Object>> getAvgSpeciesResultByProvince(@Param("timePoint")LocalDateTime endHourTime);

    List<Map<String,Object>> getAvgSpeciesResultByCity(@Param("timePoint")LocalDateTime endHourTime);

    List<Map<String,Object>> getEveryDayByProvince(@Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime dayEndTime, @Param("regionCodes")List provinceList);

    List<Map<String,Object>> getEveryDayByCity(@Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime dayEndTime, @Param("regionCodes")List cityList);

    List<Map<String,Object>> getEveryDayByCounty(@Param("startTime")LocalDateTime startTime, @Param("endTime")LocalDateTime dayEndTime, @Param("regionCodes")List countyList);

    Map getAvgSpeciesResultByProvinceOrCity1(@Param("timePoint")LocalDateTime latestTime, @Param("regionId")String regionId);

    Map getAvgSpeciesResultByCounty2(@Param("timePoint")LocalDateTime latestTime, @Param("regionId")String regionCode);

    List<Map<String,Object>> getAvgSpeciesResultByCityInDate(@Param("startDateTime")LocalDateTime startDateTime, @Param("endDateTime")LocalDateTime endDateTime);

    List<Map<String,Object>> getAvgSpeciesResultByProvinceInDate(@Param("startDateTime")LocalDateTime startDateTime, @Param("endDateTime")LocalDateTime endDateTime);

    List<Map<String,Object>> getAvgSpeciesResultByCountyInDate(@Param("startDateTime")LocalDateTime startDateTime, @Param("endDateTime")LocalDateTime endDateTime);


}
