package com.makenv.cache;

import com.makenv.common.FixedCacheMap;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by wgy on 2016/12/1.
 */
@Component
public class ActualTimeCache {

    //整时数据
    private final FixedCacheMap fixedCacheMap = new FixedCacheMap(24);


    public void put(LocalDateTime endHourTime, Map resultMap) {

        fixedCacheMap.put(endHourTime,resultMap);

    }

   public  Map.Entry<LocalDateTime,Map> getCacheNewMap(){

       return fixedCacheMap.getYougestEntry();
   }

   public Map getAll() {

       return this.fixedCacheMap;
   }

    public Object get(LocalDateTime endHourTime){

        return fixedCacheMap.get(endHourTime);
    }

    public boolean containsKey(LocalDateTime time) {

        return fixedCacheMap.containsKey(time);
    }
}
