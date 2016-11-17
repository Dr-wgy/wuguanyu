package com.makenv.service;

import com.makenv.condition.StationDetailCondition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by wgy on 2016/11/16.
 */
public interface RankService {

    List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, TreeSet<String> regionCode);

    List getRankResultDataByRegionCodes(Integer year, Integer month, TreeSet<String> regionCode);

    List getRankResultDataByRegionCodes(Integer year, Integer month, Integer date, Integer hour, TreeSet<String> regionCode);

    List getRankResultDataByRegionCodes(Integer year, TreeSet<String> regionCode);

    List getRankResultDataByArea(StationDetailCondition parameter, LocalDateTime startTime, LocalDateTime endTime);

    Map getRankResultDataByArea(StationDetailCondition stationDetailCondition);
}
