package com.makenv.config;

import com.makenv.task.CollectBaseDataTask;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.text.ParseException;

/**
 * Created by wgy on 2016/8/4.
 * 任务调度配置类
 */

@Configuration
/*@EnableScheduling*/
public class TaskSchedulerConfig {


    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    private CollectBaseDataTask collectBaseDataTask;


    @Bean
    public MethodInvokingJobDetailFactoryBean methodInvokingJobDetailDateTask(){

        MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();

        methodInvokingJobDetailFactoryBean.setTargetObject(collectBaseDataTask);

        methodInvokingJobDetailFactoryBean.setTargetMethod("doDateTask");

        return methodInvokingJobDetailFactoryBean;
    }

    @Bean
    public MethodInvokingJobDetailFactoryBean methodInvokingJobDetailHourTask(){

        MethodInvokingJobDetailFactoryBean methodInvokingJobDetailFactoryBean = new MethodInvokingJobDetailFactoryBean();

        methodInvokingJobDetailFactoryBean.setTargetObject(collectBaseDataTask);

        methodInvokingJobDetailFactoryBean.setTargetMethod("doHourTask");

        return methodInvokingJobDetailFactoryBean;
    }



    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean() {

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();

        //bean.setCronExpression("*/5 * * * * ?"); //每天整点运行

        bean.setCronExpression("0 0 0-23 * * ?"); //每天整点运行
        //bean.setCronExpression("0 0 0-23 * * ?"); //每天整点运行

        bean.setJobDetail(methodInvokingJobDetailHourTask().getObject());

        return bean;
    }

    @Bean
    public CronTriggerFactoryBean cronTriggerFactoryBean1() {

        CronTriggerFactoryBean bean = new CronTriggerFactoryBean();

        bean.setCronExpression("0 0 2 * * ?");//每天两点运行

        bean.setJobDetail(methodInvokingJobDetailDateTask().getObject());

        return bean;
    }



    @Bean
    public SchedulerFactoryBean SchedulerFactoryBean(){

        SchedulerFactoryBean schedulerFactoryBean =  new SchedulerFactoryBean();

        schedulerFactoryBean.setTriggers(cronTriggerFactoryBean().getObject(), cronTriggerFactoryBean1().getObject());

        schedulerFactoryBean.setTaskExecutor(threadPoolTaskExecutor);

        return schedulerFactoryBean;
    }


}
