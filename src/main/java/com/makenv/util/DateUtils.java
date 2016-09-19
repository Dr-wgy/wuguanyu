package com.makenv.util;

import com.makenv.condition.StationDetailCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by wgy on 2016/8/8.
 */

public class DateUtils {

    private final static Logger logger = LoggerFactory.getLogger(DateUtils.class);

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

    public static String dateFormat(LocalDateTime dateTime){

        String dateTimeString = "";

        dateTimeString = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00:00").format(dateTime);

        return  dateTimeString;
    }

    public static String dateFormat(LocalDateTime dateTime,String formatPattern){

        String dateTimeString = "";

        dateTimeString = DateTimeFormatter.ofPattern(formatPattern).format(dateTime);

        return dateTimeString;

    }

    public static String dateFormat(LocalDate dateTime,String formatPattern){

        String dateTimeString = "";

        dateTimeString = DateTimeFormatter.ofPattern(formatPattern).format(dateTime);

        return dateTimeString;

    }

    public static Map initCondition(Integer year,Integer month,Integer date,String tunit,Integer timeSpan){

        Map map = new HashMap();

        if(timeSpan == null) {

            timeSpan = 12;
        }

        LocalDateTime endTime = null;

        LocalDateTime startTime = null;

        ChronoUnit unit = null;

        if(year != null && month !=null) {

            if(date!= null) {

                endTime =  LocalDateTime.of(year, month, date + 1, 0, 0);
            }

            else {

                endTime =  LocalDateTime.of(year, month+1, 1, 0, 0);
            }

        }else {

            throw new RuntimeException("时间异常 parameter year month");
        }

        switch (tunit) {

            case "m":

                startTime = endTime.minus(timeSpan, ChronoUnit.MONTHS);

                unit = ChronoUnit.MONTHS;

                break;

            case "d":

                startTime = endTime.minus(timeSpan, ChronoUnit.DAYS);

                unit = ChronoUnit.DAYS;

                break;

            default:

                break;

        }

        map.put("startTime",startTime);

        map.put("endTime",endTime);

        map.put("chronoUnit",unit);

        return map;
    }

    public static Map initCondition(StationDetailCondition stationDetailCondition){

      return initCondition(stationDetailCondition.getYear(),
              stationDetailCondition.getMonth(),stationDetailCondition.getDate(),
              stationDetailCondition.getTunit(),stationDetailCondition.getTimeSpan());
    }

    public static LocalDateTime convertDateToLocaleDateTime(Date date){

        if(date != null) {

            return LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());

        }

        return  null;

    }

    public static LocalDateTime convertStringToLocalDateTime(String pm_25_jjj_startTime, String pattern) {

        return LocalDateTime.parse(pm_25_jjj_startTime,DateTimeFormatter.ofPattern(pattern));

    }

    public static LocalDateTime convertTimeStampToLocalDateTime(Timestamp timestamp) {

        Instant instant = Instant.ofEpochMilli(timestamp.getTime());

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static LocalDate convertStringToLocalDate(String timePoint, String pattern) {
        return LocalDate.parse(timePoint, DateTimeFormatter.ofPattern(pattern));
    }
}

