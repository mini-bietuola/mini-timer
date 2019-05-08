package com.netease.mini.bietuola.web.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

    public static int getDayOfMonth(Date date) {
        return getDayOfMonth(date, null, 0);
    }

    public static int getDayOfMonth(Date date, Integer field, int offset) {
        return getCalendar(date, field, offset).get(Calendar.DAY_OF_MONTH);
    }

    public static Date getDate(Date date, Integer field, int offset) {
        return getCalendar(date, field, offset).getTime();
    }

    public static Calendar getCalendar(Date date, Integer field, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (offset != 0) {
            calendar.add(field, offset);
        }
        return calendar;
    }

    public static Date parseDate(String stringDate, String pattern, Date defaultValue) {
        try {
            if (StringUtils.isNotBlank(stringDate)) {
                return new SimpleDateFormat(pattern).parse(stringDate);
            }
        } catch (ParseException e) {
            log.error("", e);
        }
        return defaultValue;
    }

    public static Date getMonday(int weekOffset) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -((5 + c.get(Calendar.DAY_OF_WEEK)) % 7) + 7 * weekOffset);
        return DateUtils.truncate(c.getTime(), Calendar.DAY_OF_MONTH);
    }

    public static Date parse(String text, String pattern) throws ParseException {
        return new SimpleDateFormat(pattern).parse(text);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String format(long time, String pattern) {
        return format(new Date(time), pattern);
    }
    
    public static long getDayZeroTime(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
    
    public static long getTodayStart(){
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }
    
    public static long getTodayEnd(){
        return getTodayStart() + DateUtils.MILLIS_PER_DAY;
    }
    
    public static long parseStrTime2Long(String time) throws ParseException {
        SimpleDateFormat secFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        secFormatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return secFormatter.parse(time).getTime();
    }

    /**
     * 取得time偏移day天的时间
     */
    public static long getTimeOffsetDays(long time, int day) {
        return time + DateUtils.MILLIS_PER_DAY * day;
    }

    public static int getDayDiff(long time, long baseTime) {
        long diff = getDayZeroTime(time) - getDayZeroTime(baseTime);
        return (int) (diff / DateUtils.MILLIS_PER_DAY);
    }

    public static long getLastDayTimeOfCurMonth(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY,23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        return calendar.getTimeInMillis();
    }

    public static long getCurMinuteStart(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

}
