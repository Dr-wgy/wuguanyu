package com.makenv.dao;

import com.makenv.cache.CityCacheUtil;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.vo.CityParamVo;
import com.makenv.vo.CityVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/9/1.
 */
@Repository
public class StationDetailMultipleDao {

    @Autowired
    private StationDetailMapper stationDetailMapper;

    public List<Map<String, Object>> getLastTimeSpanResultDataByJING_JIN_JI(Map<String, Object> map) {

        return stationDetailMapper.getLastTimeSpanResultDataByJING_JIN_JI(map);
    }
    public List<Map<String, Object>> getAllCurrentPlaceByJING_JIN_JI(CityParamVo cityParamVo) {

        List<String>  list = stationDetailMapper.getRepeatStationCodes();

        Map map = new HashMap<String,Object>();

        map.put("repeatStationCodes",list);

        return stationDetailMapper.getAllCurrentPlaceByJING_JIN_JI(cityParamVo,map);
    }
}
