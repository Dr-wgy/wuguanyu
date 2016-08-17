package com.makenv.service;

import com.makenv.task.Task;
import com.makenv.task.TimeTask;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wgy on 2016/8/6.
 */
public interface AsyncService {

    void executeAsyncTask(Task Task);
}
