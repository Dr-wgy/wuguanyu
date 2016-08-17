package com.makenv.aop;

import com.makenv.task.DateTask;
import com.makenv.task.HourTask;
import com.makenv.task.Task;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.groups.ConvertGroup;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wgy on 2016/8/10.
 */
/*@Aspect
@Component*/
/*
public class AsnyAop {

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Pointcut("execution(* com.makenv.service.impl.AsyncServiceImpl.executeAsyncTask(..))")
    public void pointCut(){

    }

    */
/*@Before("pointCut()")*//*

    public void before(JoinPoint joinPoint){

        Object obj [] = (Object[])joinPoint.getArgs();

        if(obj[0] instanceof HourTask) {

            HourTask task = (HourTask)obj[0];

            Task parentTask = task.getParentTask();

            if(parentTask instanceof DateTask) {

                System.out.println(new Date() + "\"before:\" + joinPoint" + task + parentTask + "--> length:" + ((DateTask) parentTask).getSubTasks().size());
            }
        }


    }
    @After("pointCut()")
    public void after(JoinPoint joinPoint) {

        Object obj [] = (Object[])joinPoint.getArgs();

        List list = null;

        HourTask task = null;

        if(obj[0] instanceof HourTask) {

            task = (HourTask)obj[0];

            Task parentTask = task.getParentTask();

            if(parentTask instanceof DateTask) {

                list = ((DateTask) parentTask).getSubTasks();

            }


            if(list != null) {

                if(task != null) {

                    synchronized (parentTask) {

                        list.remove(task);
                    }
                }
            }
        }

    }
}
*/
