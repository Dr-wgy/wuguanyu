package com.makenv.mapper;

import com.makenv.domain.City;

import java.util.List;
import java.util.Map;

public interface CityMapper {
    int deleteByPrimaryKey(Integer cityid);

    int insert(City record);

    int insertSelective(City record);

    City selectByPrimaryKey(Integer cityid);

    int updateByPrimaryKeySelective(City record);

    int updateByPrimaryKey(City record);

    List<Map<String,Object>> selectAllCity();
}