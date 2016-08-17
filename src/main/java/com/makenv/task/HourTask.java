package com.makenv.task;

import com.makenv.service.AsyncService;
import com.makenv.service.StationDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Created by wgy on 2016/8/9.
 */
public class HourTask extends DateTask implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(HourTask.class);

    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00");

    private RedisTemplate redisTemplate;

    public TaskGroup getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(TaskGroup taskGroup) {
        this.taskGroup = taskGroup;
    }

    private TaskGroup taskGroup;

    public void setStationDetailService(StationDetailService stationDetailService) {
        this.stationDetailService = stationDetailService;
    }

    private StationDetailService stationDetailService;

    public HourTask(LocalDateTime startTime, LocalDateTime endTime) {

        super(startTime,endTime);
    }

    public HourTask(){


    }

    @Override
    public void run() {

        final LocalDateTime startTime = this.getStartTime();

        final LocalDateTime endTime  = this.getEndTime();

        final List list = stationDetailService.selectStationDetailByTimeInterval(formatter.format(startTime),formatter.format(endTime));

        try {

            redisTemplate.executePipelined(new RedisCallback<Object>() {

                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {

                    String key = DateTimeFormatter.ofPattern("yyyy-MM-dd HH").format(startTime);

                    LocalDateTime time = LocalDateTime.of(endTime.getYear(), endTime.getMonth().getValue() + 1, 1, 0, 0);

                    long expireTime = endTime.until(time, ChronoUnit.SECONDS);

                    RedisSerializer keySerializer = redisTemplate.getKeySerializer();

                    RedisSerializer valueSerializer = redisTemplate.getValueSerializer();

                    byte []  bkeys = keySerializer.serialize(key);

                    byte []  bvals = valueSerializer.serialize(list);

                    connection.setEx(bkeys,expireTime,bvals);

                    return null;
                }
            });


        } catch (Exception e) {

            e.printStackTrace();

            logger.info("Excpetion",e);
        }
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
