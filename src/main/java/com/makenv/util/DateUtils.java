package com.makenv.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wgy on 2016/8/8.
 */

public class DateUtils {

    static final String formatPattern = "yyyy-MM-dd";

    static final String formatPattern_Short = "yyyyMMdd";


    /**
     * 获取当前日期
     * @return
     */
    public static String getCurrentDate(){
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(new Date());
    }

    /**
     * 获取制定毫秒数之前的日期
     * @param timeDiff
     * @return
     */
    public static String getDesignatedDate(long timeDiff){
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        long nowTime = System.currentTimeMillis();
        long designTime = nowTime - timeDiff;
        return format.format(designTime);
    }

    /**
     *
     * 获取前几天的日期
     */
    public static String getPrefixDate(String count){
        Calendar cal = Calendar.getInstance();
        int day = 0-Integer.parseInt(count);
        cal.add(Calendar.DATE,day);   // int amount   代表天数
        Date datNew = cal.getTime();
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(datNew);
    }
    /**
     * 日期转换成字符串
     * @param date
     * @return
     */
    public static String dateToString(Date date,String formatPattern){
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(date);
    }

    public static String dateToString(Date date){
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        return format.format(date);
    }
    /**
     * 字符串转换日期
     * @param str
     * @return
     */
    public static Date stringToDate(String str){
        //str =  " 2008-07-10 19:20:00 " 格式
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        if(!str.equals("")&&str!=null){
            try {
                return format.parse(str);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Date stringToDate(String str,String formatPattern){
        //str =  " 2008-07-10 19:20:00 " 格式
        SimpleDateFormat format = new SimpleDateFormat(formatPattern);
        if(!str.equals("")&&str!=null){
            try {
                return format.parse(str);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return null;
    }

    private static Map<String,String> getFirstday_Lastday_Month(int year,int month){

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR,year);

        calendar.set(Calendar.MONTH,month);

        return getFirstday_Lastday_Month(calendar.getTime());

    }

    private static Map<String, String> getFirstday_Lastday_Month(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        Date theDate = calendar.getTime();

        //上个月第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first).append(" 00:00:00");
        day_first = str.toString();

        //上个月最后一天
        calendar.add(Calendar.MONTH, 1);    //加一个月
        calendar.set(Calendar.DATE, 1);        //设置为该月第一天
        calendar.add(Calendar.DATE, -1);    //再减一天即为上个月最后一天
        String day_last = df.format(calendar.getTime());
        StringBuffer endStr = new StringBuffer().append(day_last).append(" 23:00:00");
        day_last = endStr.toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("first", day_first);
        map.put("last", day_last);
        return map;
    }

    /**
     * @param beforeDateTime
     * @param afterDateTime
     * @return
     */
    public static List<String> GetHourDate(String beforeDateTime, String afterDateTime,String timePattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(timePattern);

        List<String> list = new ArrayList<String>();

        try {

            Date beforeDate = sdf.parse(beforeDateTime);

            Date afterDate = sdf.parse(afterDateTime);

            list.add(beforeDateTime);

            while (afterDate.after(beforeDate)) {

                Calendar cla = Calendar.getInstance();

                cla.setTime(beforeDate);

                cla.add(Calendar.HOUR_OF_DAY, 1);

                beforeDate = cla.getTime();

                String dateString = sdf.format(cla.getTime());

                list.add(dateString);
            }


        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }

    //java中怎样计算两个时间如：“21:57”和“08:20”相差的分钟数、小时数 java计算两个时间差小时 分钟 秒 .
    public void timeSubtract(){
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date begin = null;
        Date end = null;
        try {
            begin = dfs.parse("2004-01-02 11:30:24");
            end = dfs.parse("2004-03-26 13:31:40");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long between = (end.getTime() - begin.getTime()) / 1000;// 除以1000是为了转换成秒

        long day1 = between / (24 * 3600);
        long hour1 = between % (24 * 3600) / 3600;
        long minute1 = between % 3600 / 60;
        long second1 = between % 60;
        System.out.println("" + day1 + "天" + hour1 + "小时" + minute1 + "分"
                + second1 + "秒");
    }
}

