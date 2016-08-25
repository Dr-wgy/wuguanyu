package com.makenv.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2016/8/15.
 */
public class TaskGroup {

    public List<Task> getList() {
        return list;
    }

    public void setList(List<Task> list) {
        this.list = list;
    }

    private List<Task> list = new ArrayList<Task>();

    public void add(Task hourTask) {

        this.list.add(hourTask);
    }
}
