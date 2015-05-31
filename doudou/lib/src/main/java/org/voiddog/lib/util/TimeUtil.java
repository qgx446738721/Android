package org.voiddog.lib.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 * Created by Dog on 2015/5/30.
 */
public class TimeUtil {
    public static String getTimeStringByMillis(int millis){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date d1 = new Date(millis);
        return dateFormat.format(d1);
    }

    public static String getTimeStringBySeconds(int second){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date d1 = new Date(second*1000l);
        return dateFormat.format(d1);
    }
}
