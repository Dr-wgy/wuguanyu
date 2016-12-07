package com.makenv.mapper;

import com.makenv.domain.GroupRegion;

import java.util.List;

public interface GroupRegionMapper {

    int deleteByPrimaryKey(String groupyid);

    int insert(GroupRegion record);

    int insertSelective(GroupRegion record);

    GroupRegion selectByPrimaryKey(String groupyid);

    int updateByPrimaryKeySelective(GroupRegion record);

    int updateByPrimaryKey(GroupRegion record);

    List<GroupRegion> getAllGroupRegionCode();
}