package com.makenv.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.CountyCacheUtil;
import com.makenv.cache.ProvinceCacheUtil;
import com.makenv.cache.StationCacheUtil;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.mapper.StationMapper;
import com.makenv.serializer.MyNullKeyJsonSerializer;
import com.makenv.service.*;
import com.makenv.util.DateUtils;
import com.makenv.vo.CityParamVo;
import com.makenv.vo.CityVo;
import com.makenv.vo.ProvinceVo;
import com.makenv.vo.StationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by wgy on 2016/8/8.
 * 观测服务
 */
@RestController
@RequestMapping("/api")
public class ObsController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(ObsController.class);

    private String [] prefixedid = {"11","12","31","50"};

    @Autowired
    private StationDetailService stationDetailService;

    @Autowired
    private StationDetailMapper stationMapper;

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
    @RequestMapping(value = {"/list/province","/list/getProvince"}, method = RequestMethod.GET)
    public Map<String,Object> getProvinceList(){

        Map map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, ProvinceCacheUtil.newInstance().getProvinceList());

        return map;
    }

    @RequestMapping(value = {"/list/getCity"}, method = RequestMethod.GET)
    public Map<String,Object> getCityList1(){

        Map map = new HashMap<String,Object>();

        Map<String,List> map1 = new HashMap();

        List<CityVo> list = CityCacheUtil.newInstance().getKeyCities();

        map.put(RESULT,SUCCESS);

        map.put(DATA, list);

        return map;
    }


    @RequestMapping(value = {"/list/city"}, method = RequestMethod.GET)
    public Map<String,Object> getCityList(){

        Map map = new HashMap<String,Object>();

        Map<String,List> map1 = new HashMap();

        List<ProvinceVo> provinceList = ProvinceCacheUtil.newInstance().getProvinceList();

        provinceList.forEach(provinceVo ->{

            map1.put(provinceVo.getRegionId(),CityCacheUtil.newInstance().getkeyCityListByPro(provinceVo.getRegionId()));

        });

        for (String specialCityId:prefixedid) {

            if(specialCityId.length() == 2) {

                String pronvinceId = specialCityId.substring(0,2);

                if(map1.containsKey(pronvinceId)) {

                    List list = CountyCacheUtil.newInstance().getCountyListByCity(specialCityId);

                    if(list != null && list.size() != 0) {

                        map1.get(pronvinceId).addAll(list);
                    }
                };
            }
        }


        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value = {"/list/city-all"}, method = RequestMethod.GET)
    public Map<String,Object> getCityLLList(){

        Map map = new HashMap<String,Object>();

        Map<String,List> map1 = new HashMap();

        List<ProvinceVo> provinceList = ProvinceCacheUtil.newInstance().getProvinceList();

        provinceList.forEach(provinceVo ->{

            map1.put(provinceVo.getRegionId(),CityCacheUtil.newInstance().getCityListByPro(provinceVo.getRegionId()));

        });

        map.put(RESULT,SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value={"/list/county","/list/getCounty"},method = RequestMethod.GET)
    public Map<String,Object> getCountyList(){

        Map map = new HashMap<String,Object>();

        map.put(RESULT,SUCCESS);

        map.put(DATA, CountyCacheUtil.newInstance().getCountyList());

        return map;
    }

    @RequestMapping(value = "/list/station", method = RequestMethod.GET)
    public Map<String,Object> getStationList(){

        Map map = new HashMap<String,Object>();

        final Map<String,List> map1 = new HashMap<String,List>();

        List<StationVo> list = StationCacheUtil.newInstance().getStationList();

        list.stream().filter(StationVo -> StationVo != null && StationVo.getCityName() != null).forEach(StationVo->{

            String regionCode = CityCacheUtil.newInstance().getRegionCode(StationVo.getCityName());

            if(map1.containsKey(regionCode)) {

                map1.get(regionCode).add(StationVo);

            }
            else {

                List stationList = new ArrayList();

                stationList.add(StationVo);

                map1.put(regionCode,stationList);

            }

            String adCode = StationVo.getAdCode();

            if(adCode != null) {

                if(map1.containsKey(adCode)) {

                    map1.get(adCode).add(StationVo);

                }
                else {

                    List stationList1 = new ArrayList();

                    stationList1.add(StationVo);

                    map1.put(adCode,stationList1);

                }
            }

        });

        map.put(RESULT, SUCCESS);

        map.put(DATA, map1);

        return map;
    }

    @RequestMapping(value = "/list/getStation", method = RequestMethod.GET)
    public Map<String,Object> getStationList1(){

        Map map = new HashMap<String,Object>();

        final Map<String,List> map1 = new HashMap<String,List>();

        List<StationVo> list = StationCacheUtil.newInstance().getStationList();

        map.put(RESULT, SUCCESS);

        map.put(DATA, list);

        return map;
    }

    @RequestMapping(value="/current-city",method = RequestMethod.GET)
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

    @RequestMapping(value = "/obs", method = RequestMethod.GET)
    public Map<String,Object> everyUnitData(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd")Date startDate,@DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam("endDate") Date endDate,
                                            @RequestParam(value = "area",defaultValue = "area") String area,@RequestParam("areaId") String areaId,@RequestParam(value = "res",defaultValue = "m") String res){

        Map<String,Object> map = new HashMap<String,Object>();

        LocalDateTime startDateTime = DateUtils.convertDateToLocaleDateTime(startDate);

        LocalDateTime endDateTime = DateUtils.convertDateToLocaleDateTime(endDate);

        Map resultData = stationDetailService.getAvgResultByAreaOrStation(startDateTime,endDateTime,area,areaId,res);

        map.put(RESULT, SUCCESS);

        map.put(DATA, resultData);


        return map;
    }

    @RequestMapping(value="/last24",method = RequestMethod.GET)
    public Map<String,Object> getLast24Result(@RequestParam("area")String area, @RequestParam("areaId")String areaId){

        Map<String,Object> map = new HashMap<String,Object>();

        Map<String,Object> resultData = stationDetailService.getLast24ResultData(area, areaId);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultData);

        return map;
    }

    @RequestMapping(value = "/all-month", method = RequestMethod.GET)
    public Map<String,Object> getMonthValue(@RequestParam("city") String city){

        Map map = new HashMap();

        Map<String,Object> resultData = stationDetailService.getAllMonResult(city);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultData);

        return map;
    }

    @RequestMapping(value = "/all-days", method = RequestMethod.GET)
    public Map<String,Object> getALLDate(@RequestParam("city") String city,@RequestParam Integer year,@RequestParam Integer month){

        Map map = new HashMap();

        Map<String,Object> resultData = stationDetailService.getALLDate(city,year,month);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultData);

        return map;
    }


    @RequestMapping(value = "/quality", method = RequestMethod.GET)
    public Map<String,Object> getQualityData(@RequestParam("year") Integer year,@RequestParam("month") Integer month,
                                             @RequestParam("city") String city,
                                             @RequestParam(value = "timeSpan",defaultValue="12") String timeSpan,
                                            @RequestParam(value="timeUnit",defaultValue = "m") String timeUnit){

        Map<String,Object> map = new HashMap<String,Object>();

        List resultData = stationDetailService.getQualityData(year, month, city,timeSpan,timeUnit);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultData);

        return map;
        }

    @RequestMapping(value = "/hev", method = RequestMethod.GET)
    public Map<String,Object> getHevData(@RequestParam("year") Integer year,@RequestParam("month") Integer month,
            @RequestParam("city") String city, @RequestParam(value = "timeSpan",defaultValue="12") String timeSpan,
             @RequestParam(value="timeUnit",defaultValue = "m")String timeUnit){

        Map<String,Object> map = new HashMap<String,Object>();

        Map resultMap = stationDetailService.getHevData(year, month, city,timeSpan,timeUnit);

        map.put(RESULT,SUCCESS);

        map.put(DATA,resultMap);

        return map;
    }
}
