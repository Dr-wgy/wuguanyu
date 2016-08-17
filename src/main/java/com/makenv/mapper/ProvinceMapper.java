package com.makenv.mapper;

import com.makenv.domain.Province;

import java.util.List;
import java.util.Map;

public interface ProvinceMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Province record);

    int insertSelective(Province record);

    Province selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Province record);

    int updateByPrimaryKey(Province record);

    List<Map<String,Object>> getAllProvinces();
}