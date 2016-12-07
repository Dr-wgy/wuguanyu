package com.makenv.resultAppend;

import java.util.*;

/**
 * Created by wgy on 2016/11/28.
 */
public class RankResultByIntervalInMonth {
    private List<Map<String,Object>> sourceList;



    public RankResultByIntervalInMonth(List list) {

        this.sourceList = list;
    }

    public Object getResult(List list,String key) {


        return null;
    }

    public Object getResult() {

        Map<String,Object> map = new HashMap<String,Object>();

        if(sourceList != null && sourceList.size() != 0) {

            Set xSet = new LinkedHashSet<>();

            Set ySet = new LinkedHashSet();

            HashMap resultMap = new LinkedHashMap<>();

            sourceList.stream().forEach(eachMap ->{

                xSet.add(eachMap.get("timePoint"));

                Object regionName = eachMap.get("regionName");

                List arrayList = resultMap.containsKey(regionName)?(List)resultMap.get(regionName):new ArrayList();

                arrayList.add(eachMap);

                resultMap.put(regionName,arrayList);

            });

            ySet.add(resultMap);

            map.put("x", xSet);

            map.put("y",ySet);

        }

        return map;
    }
}
