package com.makenv.service.impl;

import com.makenv.config.TaskExecutorConfig;
import com.makenv.service.AsyncService;
import com.makenv.service.ScheduledTaskService;
import com.makenv.task.DateTask;
import com.makenv.task.HourTask;
import com.makenv.task.Task;
import com.makenv.task.TaskGroup;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private TaskExecutorConfig taskExecutorConfig;

    @Resource(name="stringRedisTemplate")
    private RedisTemplate redisTemplate;

/*    @Autowired
    private AsyncService asyncService;*/


    @Override
   /* @Scheduled(fixedRate = 50000)*/
    public void reportCurrentTime() {

      /*  //这块逻辑有点问题不知道怎么弄
        if(dateTask.getSubTasks().size() > 0) {

            Thread th =  new CheckThread(dateTask);

            th.setDaemon(true);//设置后台进程避免程序意外时耗性能；

            th.start();

        }*/


    }

    @Override
    @Scheduled(cron = "0 0 0 ? * *")
    public void fixTimeExecution() {

        Executor executor = taskExecutorConfig.getAsyncExecutor();

        LocalDateTime endTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(0, 0, 0));

        LocalDateTime startTime = endTime.minus(1, ChronoUnit.DAYS);

        TaskGroup taskGoup = new TaskGroup();

        //List resultList = dateTask.getResultList();

        while(startTime.isBefore(endTime)){

            LocalDateTime temp = startTime.plus(1, ChronoUnit.HOURS);

            HourTask hourTask = new HourTask(startTime,temp);

           // hourTask.setRedisTemplate(hourTask);

            //hourTask.setParentTask(dateTask);

            //dateTask.getSubTasks().add(hourTask);

            executor.execute(hourTask);

            taskGoup.add(hourTask);

            //asyncService.executeAsyncTask(hourTask);

            startTime = temp;
        }

    }

 /*   class CheckThread extends Thread {

        private Task task;


        public CheckThread(Task task) {

            this.task = task;

        }
        public CheckThread() {

        }



        @Override
        public void run() {


          if(this.task instanceof  DateTask) {

                DateTask dateTask = ((DateTask)task);

                while(true){

                    if(dateTask.getSubTasks().size() == 0) {

                        List list =  dateTask.getResultList();

                        System.out.println(dateTask.getResultList().size());

                        //System.out.println(redisTemplate);

                        break;

                    }

      *//*                    try {
                        TimeUnit.SECONDS.sleep(5);

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                    }*//*
                }
          }
        }
    }*/

}
