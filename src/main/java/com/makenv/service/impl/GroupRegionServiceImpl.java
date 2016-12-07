package com.makenv.service.impl;

import com.makenv.domain.GroupRegion;
import com.makenv.mapper.GroupRegionMapper;
import com.makenv.service.GroupRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Created by wgy on 2016/12/4.
 */
@Service
public class GroupRegionServiceImpl implements GroupRegionService {

    @Autowired
    private GroupRegionMapper groupRegionMapper;


    @Transactional
    public boolean delGroupRegion(String groupId) {

        return groupRegionMapper.deleteByPrimaryKey(groupId) > 0;
    }

    @Transactional
    public GroupRegion addGroupRegion(List<String> regionCodes,String describe) {

        GroupRegion groupRegion = new GroupRegion();

        groupRegion.setGroupId(UUID.randomUUID().toString());

        if(!StringUtils.isEmpty(describe)) {

            groupRegion.setDescription(describe);
        }

        groupRegion.setValue(regionCodes.stream().collect(Collectors.joining(",")));

        if(groupRegionMapper.insertSelective(groupRegion) > 0) {

            return groupRegion;
        }

        return null;
    }

    @Transactional
    public GroupRegion updateGroupRegion(String groupId, List<String> regionCodes, String describe) {

        GroupRegion groupRegion = new GroupRegion();

        groupRegion.setGroupId(groupId);

        if(null != describe) {

            groupRegion.setDescription(describe);
        }

        groupRegion.setValue(regionCodes.stream().collect(Collectors.joining(",")));

        if(groupRegionMapper.updateByPrimaryKeySelective(groupRegion) > 0) {

            return groupRegion;
        }

        return null ;
    }

    @Transactional(readOnly = true)
    public GroupRegion selectGroupRegionByID(String groupId) {

        return groupRegionMapper.selectByPrimaryKey(groupId);
    }

    @Transactional(readOnly = true)
    public List<GroupRegion> getAllGroupRegionCode() {

        return groupRegionMapper.getAllGroupRegionCode();
    }
}
