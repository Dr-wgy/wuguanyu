package com.makenv.task;

import com.makenv.service.AsyncService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by wgy on 2016/8/9.
 */
public class DateTask extends TimeTask {

    public List getResultList() {
        return resultList;
    }

    private List resultList = Collections.synchronizedList(new ArrayList());

    private List<HourTask> subTasks = Collections.synchronizedList(new ArrayList<HourTask>());

    public List<HourTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(List<HourTask> subTasks) {
        this.subTasks = subTasks;
    }

    public DateTask(LocalDateTime startTime, LocalDateTime endTime) {

        super(startTime, endTime);

    }


    public DateTask(){

    }
}