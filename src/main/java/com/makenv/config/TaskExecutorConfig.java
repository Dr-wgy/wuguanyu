package com.makenv.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Created by wgy on 2016/8/3.
 */
@Configuration
public class TaskExecutorConfig{

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //线程池维护线程的最少数量
        taskExecutor.setCorePoolSize(10);
        //线程维护线程的最大数量
        taskExecutor.setMaxPoolSize(100);
        //线程池所使用的缓冲队列容量
        taskExecutor.setQueueCapacity(500);

        taskExecutor.initialize();

        return taskExecutor;
    }
}
