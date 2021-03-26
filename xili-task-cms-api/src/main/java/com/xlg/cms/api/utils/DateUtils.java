package com.xlg.cms.api.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author wangqingwei <wangqingwei@kuaishou.com>
 * Created on 2021-03-05
 */
public class DateUtils {

    private static SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static long parseLong(String date) throws ParseException {
        return dft.parse(date).getTime();
    }


    public static String format(long time) {
        Date date = new Date();
        date.setTime(time);
        return dft.format(date);
    }



}
