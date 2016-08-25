package com.makenv.controller;

import com.makenv.service.StationDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wgy on 2016/8/19.虚拟站点
 */
@RestController
public class VirtualSiteController extends BaseController {

    @Autowired
    private StationDetailService stationDetailService;


    @RequestMapping(value = "api/monthInVir", method = RequestMethod.GET)
    public Map<String,Object> getMonthResult(@RequestParam("date") Integer year,@RequestParam("month") Integer month, @RequestParam("virtualSite") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, "");

        return map;
    }

    @RequestMapping(value = "api/dateInVir", method = RequestMethod.GET)
    public Map<String,Object> getDayValue(@RequestParam("date") Integer year,@RequestParam("month") Integer month,@RequestParam("date") Integer date, @RequestParam("virtualSite") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, "");

        return map;
    }


    @RequestMapping(value = "/yearInVir", method = RequestMethod.GET)
    public Map<String,Object> getYearResult(@RequestParam("year") Integer year, @RequestParam("virtualSite") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> map1 =  stationDetailService.getYearResultByVirtualSite(year,regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value="/last24InVir",method = RequestMethod.GET)
    public Map<String,Object> getLast24Result(@RequestParam("virtualSite") String regionCode){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultData = stationDetailService.getLast24ResultDataByVirtualSite(regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultData);

        return map;
    }


    @RequestMapping(value="/lastInVir",method = RequestMethod.GET)
    public Map<String,Object> getLastTimeSpanResultData(@RequestParam(value = "unit",defaultValue = "h")String unit,String regionCode,Integer timeSpan){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultData= stationDetailService.getLastTimeSpanResultData(regionCode, timeSpan, unit);

        map.put(RESULT,SUCCESS);

        map.put(DATA, resultData);

        return map;
    }

}
