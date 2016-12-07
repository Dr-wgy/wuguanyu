package com.makenv.resultAppend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wgy on 2016/11/25.
 */
public class RankResultByInterval {

    private List<Map<String,Object>> sourceList;

    public RankResultByInterval(List sourceList){

        this.sourceList = sourceList;

    }

    public RankResultByInterval(){


    }


    public Object getResult(List list,String key) {


        return null;
    }

    public Object getResult() {

        Map<String,Object> map = new HashMap<String,Object>();

        if(sourceList != null && sourceList.size() != 0) {

            List xList = new ArrayList();

            List yList = new ArrayList();

            sourceList.stream().forEach(eachMap ->{

                xList.add(eachMap.get("regionName"));

                yList.add(eachMap);

            });

            map.put("x", xList);

            map.put("y",yList);

        }

        return map;
    }
}
