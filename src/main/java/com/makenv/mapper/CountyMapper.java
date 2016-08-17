package com.makenv.mapper;

import com.makenv.domain.County;

import java.util.List;
import java.util.Map;

public interface CountyMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(County record);

    int insertSelective(County record);

    County selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(County record);

    int updateByPrimaryKey(County record);

    List<Map<String,Object>> getAllCountyList();
}