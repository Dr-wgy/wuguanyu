package com.makenv.service.impl;

import com.makenv.cache.RedisCache;
import com.makenv.service.ScheduledTaskService;
import com.makenv.service.StationDetailService;
import com.makenv.task.HourTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by wgy on 2016/8/9.
 */
 @Service
 public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    private final static Logger logger = LoggerFactory.getLogger(ScheduledTaskServiceImpl.class);


    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Resource(name="redisTemplate")
    private RedisTemplate redisTemplate;


    @Autowired
    private RedisCache redisCache;

    @Resource
    private StationDetailService stationDetailService;


    @Override
    /*@Scheduled(fixedRate = 50000)*/
    public void reportCurrentTime() {



        //检查是否有都出来的id

    }

/*    @Override
    @Scheduled(cron = "0 0 0 ? * *")*/
    public void fixTimeExecution() {

        LocalDateTime endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(0, 0, 0));

        LocalDateTime startTime = endTime.minus(1, ChronoUnit.DAYS);

        while(startTime.isBefore(endTime)){

            LocalDateTime temp = startTime.plus(1, ChronoUnit.HOURS);

            HourTask hourTask = new HourTask(startTime,temp);

            hourTask.setRedisTemplate(redisTemplate);

            hourTask.setStationDetailService(stationDetailService);

            executor.execute(hourTask);

            startTime = temp;
        }

    }

    /*@Scheduled(cron = "0 0 2 ? * *")
    public void fixTimeExecutionInBaseData() {


    }*/
}
