package com.makenv.controller;

import com.makenv.condition.StationDetailCondition;
import com.makenv.service.AsyncService;
import com.makenv.service.CountyService;
import com.makenv.service.ProvinceService;
import com.makenv.service.StationDetailService;
import com.makenv.vo.RankAreaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value = "api/avgMonthInRe", method = RequestMethod.GET)
    public Map<String,Object> getMonthResult(@RequestParam("year") Integer year,@RequestParam("month") Integer month, @RequestParam("regionCode") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultMap = stationDetailService.getAvgMonthResultByRegionCode(year, month, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, resultMap);

        return map;
    }

    @RequestMapping(value = "api/dateInRe", method = RequestMethod.GET)
    public Map<String,Object> getDayValue(@RequestParam("date") Integer year,@RequestParam("month") Integer month,@RequestParam("date") Integer date, @RequestParam("regionCode") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, "");

        return map;
    }


    @RequestMapping(value = "api/avgYearInRe", method = RequestMethod.GET)
    public Map<String,Object> getYearResult(@RequestParam("year") Integer year, @RequestParam("regionCode") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> map1 =  stationDetailService.getAvgYearResultByRegionCode(year, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value="api/last24InRe",method = RequestMethod.GET)
    public Map<String,Object> getLast24Result(@RequestParam("regionCode") String regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultData = stationDetailService.getLast24ResultData(regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultData);

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

    @RequestMapping(value="api/rank",method = RequestMethod.GET)
    public Map<String,Object> getRankResult(Integer year,Integer month){

        Map<String,Object> map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        return null;
    }

   @RequestMapping(value="api/rankLastByArea",method=RequestMethod.GET)
   public Map<String,Object> rankLastByArea(StationDetailCondition stationDetailCondition){

       Map<String,Object> map = new HashMap<String,Object>();

       List list = stationDetailService.getRankResultDataByArea(stationDetailCondition);

       map.put(RESULT,SUCCESS);

       map.put(DATA,list);

       return map;
   }

    @RequestMapping(value="api/rankByArea",method=RequestMethod.GET)
    public Map<String,Object> getRankResult1(@RequestParam("year")Integer year,@RequestParam("month")Integer month,@RequestParam(value="date",required = false)Integer date,@RequestParam(required = false)List<String> areas){

        Map<String,Object> map = new HashMap<String,Object>();

        StationDetailCondition condition = new StationDetailCondition();

        LocalDateTime startTime = null;

        LocalDateTime endTime = null;

        String datePattern = "";

        if(date != null) {

            condition.setTunit("d");

            endTime = LocalDateTime.of(year,month,date+1,0,0);

        }
        else {

            endTime = LocalDateTime.of(year,month+1,1,0,0);
        }

        if(areas != null && areas.size() != 0) {

            condition.setAreas(areas);
        }

        switch (condition.getTunit()) {

            case "m":

                startTime = endTime.minus(1, ChronoUnit.MONTHS);

                break;

            case "d":

                startTime = endTime.minus(1, ChronoUnit.DAYS);

                break;

            default:

                break;

        }

        List<RankAreaData> list = stationDetailService.getRankResultDataByArea(condition, startTime, endTime);

        map.put(RESULT,SUCCESS);

        map.put(DATA,list);

        return map;
    }

    @RequestMapping(value = "api/rankByRe" ,method= RequestMethod.GET)
    public Map<String,Object> getRankResultByRe(@RequestParam("year")Integer year,@RequestParam("month")Integer month,@RequestParam(value="date",required = false)Integer date,@RequestParam("regionCode")TreeSet<String> regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        List<RankAreaData> list = stationDetailService.getRankResultDataByRes(year, month, date, regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,list);

        return map;
    }


}
