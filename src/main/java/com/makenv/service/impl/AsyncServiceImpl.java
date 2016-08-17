/*
package com.makenv.service.impl;

import ch.qos.logback.classic.selector.servlet.LoggerContextFilter;
import com.makenv.service.AsyncService;
import com.makenv.service.StationDetailService;
import com.makenv.service.StationService;
import com.makenv.task.DateTask;
import com.makenv.task.HourTask;
import com.makenv.task.Task;
import com.makenv.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

*/
/**
 * Created by wgy on 2016/8/9.
 *//*

@Service
@Async
public class AsyncServiceImpl implements AsyncService {

    private final static Logger logger = LoggerFactory.getLogger(AsyncServiceImpl.class);

    @Autowired
    private StationDetailService stationDetailService;

    @Resource(name="stringRedisTemplate")
    private RedisTemplate redisTemplate;

    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");


    //这个方法被thread池执行
    public void executeAsyncTask(Task task) {

        if( task instanceof HourTask) {

            HourTask hourTask = ((HourTask) task);

            final DateTask dateTask = (DateTask)hourTask.getParentTask();

            String startTime = formatter.format(hourTask.getStartTime());

            String endTime = formatter.format(hourTask.getEndTime());

            List list = stationDetailService.selectStationDetailByTimeInterval(startTime,endTime);

            dateTask.getResultList().addAll(list);

            dateTask.getSubTasks().remove(task);

            if(dateTask.getSubTasks().size() == 0) {

                redisTemplate.executePipelined(new RedisCallback<Object>() {

                    @Override
                    public Object doInRedis(RedisConnection connection) throws DataAccessException {

                        List resultList = dateTask.getResultList();

                        if(resultList != null && resultList.size() != 0) {

                            String key = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(dateTask.getStartTime());

                            LocalDateTime endTime = dateTask.getEndTime();

                            LocalDateTime time = LocalDateTime.of(endTime.getYear(), endTime.getMonth().getValue() + 1, 1, 0, 0);

                            long expireTime = endTime.until(time, ChronoUnit.SECONDS);

                            RedisSerializer keySerializer = redisTemplate.getKeySerializer();

                            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();

                            byte []  bkeys = keySerializer.serialize(key);

                            byte []  bvals = valueSerializer.serialize(resultList);

                            connection.setEx(bkeys,expireTime,bvals);

                        }

                        return null;
                    }
                });

            }

        }
    }

}
*/
