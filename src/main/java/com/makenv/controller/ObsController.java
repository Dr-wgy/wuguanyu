package com.makenv.controller;

import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.CountyCacheUtil;
import com.makenv.cache.ProvinceCacheUtil;
import com.makenv.cache.StationCacheUtil;
import com.makenv.service.*;
import com.makenv.vo.StationDetailVo;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/8/8.
 * 观测服务
 */
@RestController
@RequestMapping("/api")
public class ObsController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(ObsController.class);

    @Autowired
    private StationDetailService stationDetailService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CountyService countyService;

    @Autowired
    private StationService stationService;


    /**
     * 这三个请求时常用的数据直接哦那个缓存中取,如果发生变化得刷新缓存,，使用接口
     * @return
     */
    @RequestMapping(value = "/list/province", method = RequestMethod.GET)
    public Map<String,Object> getProvinceList(){

        Map map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, ProvinceCacheUtil.newInstance().getProvinceList());

        return map;
    }

    @RequestMapping(value = "/list/city", method = RequestMethod.GET)
    public Map<String,Object> getCityList(){

        Map map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, CityCacheUtil.newInstance().getCityList());

        return map;
    }

    @RequestMapping(value="/list/county",method = RequestMethod.GET)
    public Map<String,Object> getCountyList(){

        Map map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, CountyCacheUtil.newInstance().getCountyList());

        return map;
    }

    @RequestMapping(value = "/list/station", method = RequestMethod.GET)
    public Map<String,Object> getStationList(){

        Map map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, StationCacheUtil.newInstance().getStationList());

        return map;
    }

    @RequestMapping(value = "/year", method = RequestMethod.GET)
    public Map<String,Object> getYearResult(@RequestParam("year") Integer year, @RequestParam("regionCode") String regionCode) {

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> map1 =  stationDetailService.getYearResultByCity(year,regionCode);

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public Map<String,Object> getMonthResult(@RequestParam("date") Integer year,@RequestParam("month") Integer month, @RequestParam("city") String city) {

        Map<String,Object> map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, "");

        return map;
    }

    @RequestMapping(value = "/date", method = RequestMethod.GET)
    public Map<String,Object> getDayValue(@RequestParam("date") Integer year,@RequestParam("month") Integer month,@RequestParam("date") Integer date, @RequestParam("city") String city) {

        Map<String,Object> map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, "");

        return map;
    }

    @RequestMapping(value="/aroundRangeBystaCode",method = RequestMethod.GET)
    public Map<String,Object> getAroundStationCodeRange(@RequestParam("staCode")String stationCode,@RequestParam("range")Integer range,@RequestParam(value = "unit",defaultValue = "km") String unit){

        Map<String,Object> map = new HashMap<String,Object>();

        List<Map<String,Object>> list = stationDetailService.getAroundDataResult(stationCode,range,unit);

        map.put(RESULT,SUCCESS);

        map.put(DATA, list);

        return map;
    }

    @RequestMapping(value="/aroundRangeByLaLong",method = RequestMethod.GET)
    public Map<String,Object> aroundRangeByLaLong(StationDetailVo stationDetailVo) throws Exception {

        Map<String,Object> map = new HashMap<String,Object>();

        List<Map<String,Object>> list = stationDetailService.getAroundDataResult(stationDetailVo);

        map.put(RESULT,SUCCESS);

        map.put(DATA, list);

        return map;

    }


    //历史观测数据 逐月
    /*@RequestMapping(value = "/obs", method = RequestMethod.GET)
    public IResponse getObsVal(@RequestParam("startDate") String startDate,@RequestParam("endDate") String endDate,
                               @RequestParam("area") String area,@RequestParam("areaId") String areaId,@RequestParam("res") String res){
        return new SuccessResponse(obsService.getResult(startDate, endDate, area,areaId, res));
    }

    @RequestMapping(value = "/all-month", method = RequestMethod.GET)
    public IResponse getMonthValue(@RequestParam("city") String city){
        return new SuccessResponse(yearService.getResult(city));
    }

    @RequestMapping(value = "/all-days", method = RequestMethod.GET)
    public IResponse getDayValue(@RequestParam("year") Integer year,@RequestParam("month") Integer month,@RequestParam("city") String city){
        return new SuccessResponse(monthService.getResult(year,month,city));
    }


    @RequestMapping(value = "/month", method = RequestMethod.GET)
    public IResponse getYearResult(@RequestParam("year") Integer year,@RequestParam("month") Integer month,@RequestParam("city") String city) {
        return new SuccessResponse(monthAvgService.getResult(year, month,city));
    }

    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public IResponse getMonthRank(@RequestParam("year") Integer year,@RequestParam("month") Integer month){
        return new SuccessResponse(rankService.getResult(year,month));
    }

    @RequestMapping(value = "/rank-all", method = RequestMethod.GET)
    public IResponse getAllMonthRank(){
        return new SuccessResponse(rankAllService.getResult());
    }

    @RequestMapping(value = "/quality", method = RequestMethod.GET)
    public IResponse getQualityRank(@RequestParam("year") Integer year,@RequestParam("month") Integer month, @RequestParam("city") String city){
        return new SuccessResponse(qualityService.getResult(year,month, city));
    }

    @RequestMapping(value = "/ratio", method = RequestMethod.GET)
    public IResponse getYearRatio(@RequestParam("baseYear") Integer baseYear,@RequestParam("caseYear") Integer caseYear,
                                  @RequestParam("species") String species,@RequestParam("regionId") String regionId,
                                  @RequestParam("ratio") Double ratio){
        return new SuccessResponse(ratioService.getResult(baseYear, caseYear, species, regionId, ratio));
    }

    @RequestMapping(value = "/hev", method = RequestMethod.GET)
    public IResponse getHevPollut(@RequestParam("year") Integer year,@RequestParam("month") Integer month, @RequestParam("city") String city){
        return new SuccessResponse(hevService.getResult(year, month,city));
    }

    @RequestMapping(value = "/last24", method = RequestMethod.GET)
    public IResponse getLast24Result(@RequestParam("area") String area,@RequestParam("areaId") String areaId){
        return new SuccessResponse(last24Service.getResult(area,areaId));
    }

    @RequestMapping(value = "/current-city", method = RequestMethod.GET)
    public IResponse getCurrentCityResult(CityParamVo cityParamVo){
        return new SuccessResponse(currentService.getResult(cityParamVo));
    }*/
}
