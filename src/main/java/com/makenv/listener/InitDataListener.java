package com.makenv.listener;

import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.CountyCacheUtil;
import com.makenv.cache.ProvinceCacheUtil;
import com.makenv.cache.StationCacheUtil;
import com.makenv.mapper.CityMapper;
import com.makenv.mapper.ProvinceMapper;
import com.makenv.service.CityService;
import com.makenv.service.CountyService;
import com.makenv.service.ProvinceService;
import com.makenv.service.StationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by wgy on 2016/8/8.
 * 项目加载时加入内存的数据,加入city数据和station数据
 */
@Component
public class InitDataListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger logger = LoggerFactory.getLogger(InitDataListener.class);

    @Autowired
    private StationService stationService;

    @Autowired
    private CityService cityService;

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private CountyService countyService;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

      if(event.getApplicationContext().getParent() == null) {

          CityCacheUtil.newInstance().setCityList(cityService.getAllCity());

          StationCacheUtil.newInstance().setStationList(stationService.getAllStations());

          //ProvinceCacheUtil.newInstance().setProvinceList(provinceService.getAllProvinceList());

          //CountyCacheUtil.newInstance().setCountyList(countyService.getAllCountyList());

      }

    }
}
