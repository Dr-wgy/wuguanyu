package com.makenv.task;

import com.makenv.domain.StationDetail;
import com.makenv.mapper.StationDetailMapper;
import com.makenv.mapper.StationMapper;
import com.makenv.service.TaskSchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Created by wgy on 2016/12/1.
 */
@Component
public class CollectBaseDataTask {

    @Autowired
    private TaskSchedulerService taskSchedulerService;

    @Autowired
    private StationDetailMapper stationDetailMapper;

    public void doHourTask() {

        String minTimePoint = stationDetailMapper.getMaxTimePoint(); //获取最小时间

        LocalDateTime endHourTime = LocalDateTime.parse(minTimePoint, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        taskSchedulerService.doHourTask(endHourTime);


    }

    public void doHourTask(int hour) {

        String minTimePoint = stationDetailMapper.getMinTimePoint(); //获取最小时间

        LocalDateTime endHourTime = LocalDateTime.parse(minTimePoint, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime startTime = endHourTime.minus(23,ChronoUnit.HOURS);

        for (int curr = 1; curr <= hour; curr++ ) {

            taskSchedulerService.doHourTask(startTime.plus(curr, ChronoUnit.HOURS));

            // startTime.plus(1, ChronoUnit.HOURS);

        }
    }

    public void doDateTask() {

        LocalDateTime nowTime = LocalDateTime.now();

        LocalDateTime yesterday = nowTime.minus(1, ChronoUnit.DAYS);

        yesterday = LocalDateTime.of(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth(), 0, 0, 0);

        taskSchedulerService.doDateTask(yesterday);

    }

}
