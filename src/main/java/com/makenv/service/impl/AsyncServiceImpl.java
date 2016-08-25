package com.makenv.service.impl;

import com.makenv.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by wgy on 2016/8/23.
 */
@Service
public class AsyncServiceImpl implements AsyncService {


    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    public <T> Future<T> executeAsyncTask(Callable<T> callable) {

        return threadPoolTaskExecutor.submit(callable);

    }

    public Object executeAsyncTask(Callable callable,boolean asyns) throws Exception{

        if(asyns) {

            return executeAsyncTask(callable);
        }
        else {

            return callable.call();
        }
    }

    public List  executeAsyncTask(List<Callable> callableList,boolean asyns){

        if(asyns) {

            return executeAsyncTask(callableList);
        }
        else {

            List list = new ArrayList();


            callableList.forEach(callable -> {

                try {

                    list.add(callable.call());

                } catch (Exception e) {

                    e.printStackTrace();
                }


            });

            return list;
        }
    }

    public  List<Future> executeAsyncTask(List<Callable> callableList) {

        List<Future> futures = new ArrayList<Future>();

        callableList.forEach(callable -> {

            futures.add(threadPoolTaskExecutor.submit(callable));

        });

        return futures;
    }
}
