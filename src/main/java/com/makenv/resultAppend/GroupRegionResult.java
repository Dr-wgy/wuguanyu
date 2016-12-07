package com.makenv.resultAppend;

import com.makenv.cache.CityCacheUtil;
import com.makenv.cache.CountyCacheUtil;
import com.makenv.cache.ProvinceCacheUtil;
import com.makenv.domain.GroupRegion;
import com.makenv.vo.ZtreeModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by wgy on 2016/12/5.
 */
public class GroupRegionResult {

    GroupRegion groupRegion;

    public GroupRegionResult(GroupRegion groupRegion) {

        this.groupRegion = groupRegion;
    }

    public Object getResult() {

        Map resultMap = new HashMap();

        resultMap.put("groupId",groupRegion.getGroupId());

        resultMap.put("describe",groupRegion.getDescription());

        List<String> list = Arrays.asList(groupRegion.getValue().split(","));

        Map map =list.stream().collect(Collectors.groupingBy(str -> str.length()));

        Set resultList = new LinkedHashSet<>();

        List<String> provinceList = null;

        List<String> cityList = null;

        List<String> countyList = null;

        if(map.containsKey(2)) {

            provinceList = (List)map.get(2);
        }

        if(map.containsKey(4)) {

            cityList = (List)map.get(4);
        }

        if(map.containsKey(6)) {

            countyList = (List)map.get(6);
        }

        if(provinceList != null && provinceList.size() != 0) {

            provinceList.stream().forEach(provinceCode -> {

                resultList.add(new ZtreeModel(provinceCode, "0", ProvinceCacheUtil.newInstance().getArea(provinceCode)));

            });
        }

        if(cityList != null && cityList.size() != 0) {

            cityList.stream().forEach(cityCode -> {

                String partentId = cityCode.substring(0,2);

                ZtreeModel currModel = new ZtreeModel(cityCode, partentId, CityCacheUtil.newInstance().getArea(cityCode));

                ZtreeModel ztreeModel = null;

                if(exist(list,partentId)) {

                    ztreeModel = new ZtreeModel(partentId, "0", ProvinceCacheUtil.newInstance().getArea(partentId));


                }
                else {

                    ztreeModel = new ZtreeModel(partentId, "0",ProvinceCacheUtil.newInstance().getArea(partentId),false);

                }

                resultList.add(currModel);

                resultList.add(ztreeModel);

            });

        }

        if(countyList != null && countyList.size() != 0) {

            countyList.stream().forEach(coutyCode -> {

                String partentId = coutyCode.substring(0,4);

                String gradparentId  = coutyCode.substring(0,2);

                ZtreeModel ztreeModel = new ZtreeModel(coutyCode, partentId, CountyCacheUtil.newInstance().getArea(coutyCode));

                resultList.add(ztreeModel);

                ZtreeModel parentZtree = null;

                if(exist(list,partentId)) {

                    parentZtree = new ZtreeModel(partentId, gradparentId, CityCacheUtil.newInstance().getArea(partentId));

                }
                else {

                    parentZtree = new ZtreeModel(coutyCode, partentId, CityCacheUtil.newInstance().getArea(partentId),false);

                }

                resultList.add(parentZtree);

                ZtreeModel grandZtree = null;

                if(exist(list,gradparentId)) {

                    grandZtree = new ZtreeModel(gradparentId, "0", ProvinceCacheUtil.newInstance().getArea(gradparentId));

                }

                else {

                    grandZtree = new ZtreeModel(gradparentId, "0", ProvinceCacheUtil.newInstance().getArea(gradparentId),false);

                }

                resultList.add(grandZtree);


            });

        }

        resultMap.put("value",resultList);

        return resultMap;
    }

    private boolean exist(List<String> list,String parentId) {

        return list.parallelStream().filter(code -> code.equals(parentId)).findFirst().orElse(null) != null;
    }

}
