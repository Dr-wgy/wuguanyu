package com.makenv.service.impl;

import com.makenv.cache.RedisCache;
import com.makenv.config.TaskExecutorConfig;
import com.makenv.service.AsyncService;
import com.makenv.service.ScheduledTaskService;
import com.makenv.service.StationDetailService;
import com.makenv.task.DateTask;
import com.makenv.task.HourTask;
import com.makenv.task.Task;
import com.makenv.task.TaskGroup;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

/*    @Autowired
    private AsyncService asyncService;*/


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
}
