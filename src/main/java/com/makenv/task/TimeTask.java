package com.makenv.task;

import com.makenv.service.AsyncService;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public abstract class TimeTask implements Task {

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    protected LocalDateTime startTime;

    protected LocalDateTime endTime;

    protected Task parentTask;

    protected List subTasks;

    public TimeTask(LocalDateTime startTime,LocalDateTime endTime){

        this.startTime = startTime;

        this.endTime = endTime;
    }

    public TimeTask(){


    }
}
