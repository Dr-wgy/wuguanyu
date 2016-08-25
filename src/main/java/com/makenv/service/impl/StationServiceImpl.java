package com.makenv.service.impl;

import com.makenv.domain.City;
import com.makenv.mapper.CityMapper;
import com.makenv.mapper.StationMapper;
import com.makenv.service.StationService;
import com.makenv.vo.StationVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * Created by wgy on 2016/8/6.
 */

@Service
@Transactional
public class StationServiceImpl implements StationService {

    private final static Logger logger = LoggerFactory.getLogger(StationServiceImpl.class);

    @Autowired
    private StationMapper stationMapper;


    @Override
    @Transactional(readOnly = true)
    public List<StationVo> getAllStations() {

        return stationMapper.selectAllStation();
    }

    @Transactional(readOnly = true)
    @Cacheable(value="station",key ="#city",unless = "#result.size()==0")
    public List getStationByCity(String city){

        return stationMapper.getStationByCity(city);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "station",key ="#city")
    public List getStationByCity1(City city) {

        return stationMapper.getStationByCity1(city);
    }

}
