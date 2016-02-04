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

    public static Date fromEurStringToDate(String dd_MM_yyyy) {
        try {
            if(dd_MM_yyyy != null)
                return EUR_DATE_FORMAT.parse(dd_MM_yyyy);
            else
                return null;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String fromEurStringToDbString(String dd_MM_yyyy){
        return fromDateToDbString(fromEurStringToDate(dd_MM_yyyy));
    }

    public static Date fromPickerToDate(int year, int monthOfYear, int dayOfMonth) {
        return DateUtils.fromDbStringToDate(year + String.format("%2d", (monthOfYear + 1)) + dayOfMonth);
    }

    public static String fromPickerToEurString(int year, int monthOfYear, int dayOfMonth) {
        return String.format("%02d", dayOfMonth) + "/" + String.format("%02d", (monthOfYear+1)) + "/" + year;
    }


}
