package com.makenv.service;

import com.makenv.condition.StationDetailCondition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by wgy on 2016/11/16.
 */
public interface RankService {

    List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, Set<String> regionCode);

    List getRankResultDataByRegionCodes(Integer year, Integer month, Set<String> regionCode);

    List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, Integer hour, Set<String> regionCode);

    List getRankResultDataByRegionCodes(Integer year, Set<String> regionCode);

    List getRankResultDataByArea(StationDetailCondition parameter, LocalDateTime startTime, LocalDateTime endTime);

    Map getRankResultDataByArea(StationDetailCondition stationDetailCondition);

    List getRankResultDataByRegionCodes(LocalDateTime startTime, LocalDateTime endTime, Set<String> regionCode);

    List getRankResultDataByRegionCodesInMonth(LocalDateTime startTime, LocalDateTime endTime, Set<String> regionCode);

    List getRankResultDataByRegionCodesInDate(LocalDateTime startTime, LocalDateTime endTime, Set<String> regionCode);

    List getRankResultDataNow(Set<String> regionCodes);

    List getRankResultDataLast24(Set<String> regionCodes);

    List getRankResultDataNowByGroupBy(String groupId);

    List getRankResultDataByRegionCodes1(LocalDateTime startTime1, LocalDateTime endTime1, String groupId);
}
