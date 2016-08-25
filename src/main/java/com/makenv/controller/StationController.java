package com.makenv.controller;

import com.makenv.service.StationDetailService;
import com.makenv.vo.LonLatVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/17.
 */
@RestController
public class StationController extends BaseController {

    @Autowired
    private StationDetailService stationDetailService;

    @RequestMapping(value="api/aroundRangeBystaCode",method = RequestMethod.GET)
    public Map<String,Object> getAroundStationCodeRange(@RequestParam("staCode")String stationCode,@RequestParam("range")Integer range,@RequestParam(value = "unit",defaultValue = "km") String unit){

        Map<String,Object> map = new HashMap<String,Object>();

        List<Map<String,Object>> list = stationDetailService.getAroundDataResult(stationCode,range,unit);

        map.put(RESULT,SUCCESS);

        map.put(DATA, list);

        return map;
    }

    @RequestMapping(value="api/aroundRangeByLaLong",method = RequestMethod.GET)
    public Map<String,Object> aroundRangeByLaLong(LonLatVo lonLatVo) throws Exception {

        Map<String,Object> map = new HashMap<String,Object>();

        List<Map<String,Object>> list = stationDetailService.getAroundDataResult(lonLatVo);

        map.put(RESULT,SUCCESS);

        map.put(DATA, list);

        return map;

    }



}
