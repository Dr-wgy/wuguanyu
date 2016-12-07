package com.makenv.service;

import com.makenv.domain.GroupRegion;

import java.util.List;

/**
 * Created by wgy on 2016/12/4.
 */
public interface GroupRegionService {

    boolean delGroupRegion(String groupId);

    GroupRegion addGroupRegion(List<String> regionCodes,String describe);

    GroupRegion updateGroupRegion(String groupId, List<String> regionCodes, String describe);

    GroupRegion selectGroupRegionByID(String groupId);

    List<GroupRegion> getAllGroupRegionCode();
}
