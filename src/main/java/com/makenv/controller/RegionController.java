package com.makenv.controller;

import com.makenv.condition.StationDetailCondition;
import com.makenv.service.*;
import com.makenv.vo.RankAreaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.jnlp.IntegrationService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by wgy on 2016/8/17.
 */
@RestController
public class RegionController extends BaseController {

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CountyService countyService;

    @Autowired
    private StationDetailService stationDetailService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private RankService rankService;

    @RequestMapping(value = "api/avgMonthInRe", method = RequestMethod.GET)
    public Map<String,Object> getMonthResult(@RequestParam("year") Integer year,@RequestParam("month") Integer month, @RequestParam("regionCode") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultMap = stationDetailService.getAvgMonthResultByRegionCode(year, month, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, resultMap);

        return map;
    }


    @RequestMapping(value = "api/year", method = RequestMethod.GET)
    public Map<String,Object> getYearResult(@RequestParam("year") Integer year, @RequestParam("city") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> map1 =  stationDetailService.getAvgYearResultByRegionCode(year, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value="api/lastInRe",method = RequestMethod.GET)
    public Map<String,Object> getLastTimeSpanResultData(@RequestParam(value = "unit",defaultValue = "h")String unit,String regionCode,Integer timeSpan){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultData= stationDetailService.getLastTimeSpanResultData(regionCode, timeSpan, unit);

        map.put(RESULT,SUCCESS);

        map.put(DATA, resultData);

        return map;
    }

    @RequestMapping(value={"api/rank"},method = RequestMethod.GET)
    public Map<String,Object> getRankResult(StationDetailCondition stationDetailCondition){

        Map<String,Object> map = new HashMap<String,Object>();

        Map resultDataByArea= rankService.getRankResultDataByArea(stationDetailCondition);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultDataByArea);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=DATE"},method = RequestMethod.GET)
    public Map<String,Object> getRankDayResult(@RequestParam("year")Integer year,@RequestParam("month") Integer month,@RequestParam("date") Integer date,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year,month,date,regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=MONTH"},method = RequestMethod.GET)
    public Map<String,Object> getRankMonthResult(@RequestParam("year")Integer year,@RequestParam("month") Integer month,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year, month, regionCode);

        //Map resultDataByArea= stationDetailService.getRankResultDataByArea(stationDetailCondition);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=HOUR"},method = RequestMethod.GET)
    public Map<String,Object> getRankHourResult(@RequestParam("year")Integer year,@RequestParam("month") Integer month,@RequestParam("date") Integer date,@RequestParam ("hour") Integer hour,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year,month,date,hour,regionCode);

        //Map resultDataByArea= stationDetailService.getRankResultDataByArea(stationDetailCondition);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value={"api/rankByRe"},params = {"rankType=YEAR"},method = RequestMethod.GET)
    public Map<String,Object> getRankYearResult(@RequestParam("year")Integer year,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultMap = rankService.getRankResultDataByRegionCodes(year, regionCode);

        //Map resultDataByArea= stationDetailService.getRankResultDataByArea(stationDetailCondition);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }

    @RequestMapping(value="api/rank-all",method = RequestMethod.GET)
    public Map<String,Object> getRankALLResult(){

        Map<String,Object> map = new HashMap<String,Object>();

        Map resultDataByArea= stationDetailService.getRankALLResultDataByArea();

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultDataByArea);

        return map;
    }
    @RequestMapping(value = "api/baseYear", method = RequestMethod.GET)
    public Map<String,Object> getBaseYearResult(@RequestParam("area") String area,@RequestParam("year") Integer year){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> map1 =  stationDetailService.getAvgYearResultByRegionCode(year, area);

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

   /* @RequestMapping(value = "api/rankByRe",method= RequestMethod.GET)
    public Map<String,Object> getRankResultByRe(@RequestParam("year")Integer year,@RequestParam("month")Integer month,@RequestParam(value="date",required = false)Integer date,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List<RankAreaData> list = stationDetailService.getRankResultDataByRes(year, month, date, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,list);

        return map;
    }*/

}
