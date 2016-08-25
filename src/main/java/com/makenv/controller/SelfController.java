package com.makenv.controller;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.makenv.annotation.SelfAnnnotation;
import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.ProvinceCacheUtil;
import com.makenv.cache.StationCacheUtil;
import com.makenv.service.CityService;
import com.makenv.service.ProvinceService;
import com.makenv.service.ScheduledTaskService;
import com.makenv.service.StationService;
import com.sun.javaws.CacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wgy on 2016/8/8.
 */
@RestController
@RequestMapping("makenv/self")
public class SelfController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(SelfController.class);

    @Autowired
    private StationService stationService;

    @Autowired
    private CityService cityService;

    @Autowired
    private ProvinceService provinceService;


    @Autowired
    private ScheduledTaskService scheduledTaskService;


    @RequestMapping(value = "/refresh/{type}",method = RequestMethod.GET)
    @SelfAnnnotation
    public void refresh(@PathVariable("type")String type) {

        switch (type){

            case "city":

                List cityList = cityService.getAllCity();

                CityCacheUtil.newInstance().setCityList(cityList);

                break;

            case "province":

                List provinceList = cityService.getAllCity();

                ProvinceCacheUtil.newInstance().setProvinceList(provinceList);

                break;

            case "station":

                List stationList = stationService.getAllStations();

                StationCacheUtil.newInstance().setStationList(stationList);

                break;

            default:

                break;

        }

    }

    @RequestMapping(value="/bulidCache",method = RequestMethod.GET)
    public void bulidCache(){

        scheduledTaskService.fixTimeExecution();
    }
}
