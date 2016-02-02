package it.dedonatis.emanuele.drugstore.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat EUR_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static Date fromDbStringToDate(String yyyyMMdd) {
        try {
            if(yyyyMMdd != null)
                return DB_DATE_FORMAT.parse(yyyyMMdd);
            else
                return null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String fromDateToDbString(Date date) {
        if (date == null) {
            return "";
        } else {
            return DB_DATE_FORMAT.format(date);
        }
    }

    public static String fromDateToEurString(Date date) {
        if (date == null) {
            return "";
        } else {
            return EUR_DATE_FORMAT.format(date);
        }
    }

}
