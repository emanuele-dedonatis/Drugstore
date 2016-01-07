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
}
