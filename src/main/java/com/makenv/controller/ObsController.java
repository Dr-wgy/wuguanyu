package com.makenv.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.CountyCacheUtil;
import com.makenv.cache.ProvinceCacheUtil;
import com.makenv.cache.StationCacheUtil;
import com.makenv.serializer.MyNullKeyJsonSerializer;
import com.makenv.service.*;
import com.makenv.vo.CityParamVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    @Autowired
    private ScheduledTaskService scheduledTaskService;


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

    @RequestMapping(value="/all-current-place",method = RequestMethod.GET)
    public String  getCurrentCityResult(CityParamVo cityParamVo) throws JsonProcessingException {

        Map<String,Object> map = new HashMap<String,Object>();

        Map resultData= stationDetailService.getAllCurrentPlace(cityParamVo);

        map.put(RESULT,SUCCESS);

        map.put(DATA, resultData);

        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.getSerializerProvider().setNullKeySerializer(new MyNullKeyJsonSerializer());

        return  mapper.writeValueAsString(map);

    }


/*    @RequestMapping(value="/last",method = RequestMethod.GET)
    public Map<String,Object> getLastTimeSpanResultData(@RequestParam(value = "unit",defaultValue = "h")String unit,String regionCode,Integer timeSpan){

        Map<String,Object> map = new HashMap<String,Object>();

        List list = stationDetailService.getLastTimeSpanResultData(regionCode,timeSpan,unit);

        map.put(RESULT,SUCCESS);

        map.put(DATA, list);

        return null;
    }*/

    //stationCode或者regionCode
    @RequestMapping(value="/last24BySta",method = RequestMethod.GET)
    public Map<String,Object> getLast24Result(){

        Map<String,Object> map = new HashMap<String,Object>();

        //stationDetailService.getLast24ResultData(regionCode);

        return null;
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
