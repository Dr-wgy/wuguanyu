package com.makenv.task;


import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by wgy on 2016/8/9.
 */
public class MonthTask implements Callable<List>  {

    public SpecialDealer<List> getSpecialDealer() {
        return specialDealer;
    }

    public void setSpecialDealer(SpecialDealer<List> specialDealer) {
        this.specialDealer = specialDealer;
    }

    private SpecialDealer<List> specialDealer;

    public MonthTask(SpecialDealer<List> specialDealer){

        this.specialDealer = specialDealer;
    }

    public MonthTask(){


    }
    @Override
    public List call() throws Exception {

        if(specialDealer == null) {

            throw new Exception("处理程序异常");
        }

        return specialDealer.deal();

    }
}
