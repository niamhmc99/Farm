package com.example.farm.dueDateAlarm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class UtilHelper {


    //Calculating insemination end of delivery date
    public static String getDeliveryDate(String dateInput) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date;
        try {
            date = sdf.parse(dateInput);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,283);
            dateInput = sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateInput;
    }

    //(Calculating notification date as string
    public static String getNotifyDateStr(String dateInput) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date;
        try {
            date = sdf.parse(dateInput);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,276); // 283 - 7 days = one week before expected due date
            dateInput = sdf.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateInput;
    }

    //Calculating notification date
    public static Date getNotifyDate(String dateInput) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getDefault());
        Date date = null;
        try {
            date = sdf.parse(dateInput);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,276);
            date = calendar.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


}
