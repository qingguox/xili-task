package com.xlg.cms.api.utils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-05
 */
public class DateUtils {

    public static final DateTimeFormatter YYYY_MM_DD_HHMMSS = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormat.forPattern("yyyyMMdd");

//    public static long parseLong(String date) throws ParseException {
//        return dft.parse(date).getTime();
//    }
//
//    public static String format(long time) {
//        Date date = new Date();
//        date.setTime(time);
//        return dft.format(date);
//    }

}


