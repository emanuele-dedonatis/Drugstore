package it.dedonatis.emanuele.drugstore.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mumu on 07/01/16.
 */
public class DateUtils {
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static Date fromString(String source, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(source);
    }

    public static Date parseDateTime(String dateStr) {
        try {
            return DATE_TIME_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Date parseDate(String dateStr) {
        try {
            return DATE_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        } else {
            return DATE_TIME_FORMAT.format(date);
        }
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        } else {
            return DATE_FORMAT.format(date);
        }
    }

    public static String intToString(int yyyyMMdd) {
        Date date = null;
        try {
            date = DATE_FORMAT.parse(yyyyMMdd + "");
        } catch (ParseException e) {
            date = null;
        }
        return formatDate(date);
    }

    public static String intToHour(int hhmm) {
        String startTime = "00:00";
        int minutes = 120;
        int h = minutes / 60 + Integer.valueOf(startTime.substring(0,1));
        int m = minutes % 60 + Integer.valueOf(startTime.substring(3,4));
        String newtime = h+":"+m;
        return newtime;
    }
}
