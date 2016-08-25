package com.makenv;

import com.makenv.config.RootConfig;
import org.apache.catalina.LifecycleState;
import org.springframework.boot.actuate.metrics.export.MetricExportProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/18.
 */
public class MyTest {

    public static void main(String[] args) {

        String key="key:virtualSite";

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RootConfig.class);

        RedisTemplate redistemplate  = context.getBean("redisTemplate", RedisTemplate.class);


        //redistemplate.execute()

        BoundHashOperations<String,String,List> boundHashOperations = redistemplate.boundHashOps(key);

        /*boundHashOperations*/

       if(boundHashOperations.hasKey("haha")){


           System.out.println("youkey");

       }
       else {

           System.out.println("wukey");

       }



      /*  HashOperations<String,String,List> opertion = redistemplate.opsForHash();

        String felid = "beijingNorth";

        Object obj = opertion.get(key,felid);

        System.out.println(obj);*/

        /*List<String> list = new ArrayList<String>();

        list.add("1000A");

        list.add("1000b");

        opertion.put(key,felid,list);*/
    }
}
