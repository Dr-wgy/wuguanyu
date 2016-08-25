package com.makenv.service;


import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by wgy on 2016/8/6.
 */
public interface AsyncService {

    public <T> Future<T> executeAsyncTask(Callable<T> callable);

    public Object executeAsyncTask(Callable callable,boolean asyns) throws Exception;

    public List  executeAsyncTask(List<Callable> callableList,boolean asyns);

    public  List<Future> executeAsyncTask(List<Callable> callableList);

}
