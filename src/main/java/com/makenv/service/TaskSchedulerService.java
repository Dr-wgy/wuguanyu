package com.makenv.service;

import com.makenv.cache.*;
import com.makenv.constant.Constants;
import com.makenv.domain.County;
import com.makenv.mapper.StationDetailCopyMapper;
import com.makenv.vo.CityVo;
import com.makenv.vo.CountyVo;
import com.makenv.vo.ProvinceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/12/2.
 */
@Service
public class TaskSchedulerService {

    @Autowired
    private ActualTimeCache actualTimeCache;

    @Autowired
    private StationDetailService stationDetailService;

    @Autowired
    private RedisCache redisCache;


    public void doHourTask(LocalDateTime endHourTime) {

        if(actualTimeCache.containsKey(endHourTime)) {

            Object obj = actualTimeCache.get(endHourTime);

            if(obj != null) {

                return ;
            }
        }

        Map.Entry<LocalDateTime,Map> yougestEntry = actualTimeCache.getCacheNewMap();

        if(null != yougestEntry) {

            LocalDateTime dateTime = yougestEntry.getKey();

            if(null != dateTime) {

                long hours = ChronoUnit.HOURS.between(dateTime,endHourTime);

                for(int currHour = 1; currHour < hours;currHour++) {

                    addMap(dateTime.plus(currHour, ChronoUnit.HOURS));

                }

            }

        }

        Map resultMap = stationDetailService.getAvgSpeciesHourResultInRegionCode1(endHourTime);

        if(resultMap != null && resultMap.size()!=0) {

            actualTimeCache.put(endHourTime,resultMap);

        }

    }

    private void addMap(LocalDateTime endHourTime){

        Map resultMap = stationDetailService.getAvgSpeciesHourResultInRegionCode1(endHourTime);

        if(resultMap != null && resultMap.size()!=0) {

            actualTimeCache.put(endHourTime,resultMap);

        }

        else {

            actualTimeCache.put(endHourTime,new HashMap());
        }
    }


    public void doDateTask(LocalDateTime yesterday) {

       stationDetailService.getAvgSpeciesDateResultByRegionCode1(yesterday);


    }
}
