package com.newbiest.base.utils;

import com.newbiest.base.exception.ClientException;
import com.newbiest.base.exception.ClientParameterException;
import com.newbiest.base.exception.NewbiestException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 时间工具类
 * Created by guoxunbo on 2017/10/5.
 */
public class DateUtils {

    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
    public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static Date parseDateTime(String dateString) {
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATETIME_PATTERN);
        formatter.setLenient(false);
        try {
            return formatter.parse(dateString);
        } catch(ParseException e) {
            return null;
        }
    }

    public static Date parseDate(String dateString){
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
        formatter.setLenient(false);
        try {
            return formatter.parse(dateString);
        } catch(ParseException e) {
            return null;
        }
    }

    public static Date parseTime(String dateString) {
        if (dateString == null) {
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_TIME_PATTERN);
        formatter.setLenient(false);
        try {
            return formatter.parse(dateString);
        } catch(ParseException e) {
            return null;
        }
    }

    public static Date now() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date plus(Date date, int amountToAdd, String unit) {
        ChronoUnit chronoUnit = getChronoUnit(unit);
        return plus(date, amountToAdd, chronoUnit);
    }

    public static Date plus(Date date, int amountToAdd, ChronoUnit unit) {
        LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return Date.from(localDate.plus(amountToAdd, unit).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date minus(Date date, int amountToSub, String unit) {
        ChronoUnit chronoUnit = getChronoUnit(unit);
        return minus(date, amountToSub, chronoUnit);
    }

    public static Date minus(Date date, int amountToSub, ChronoUnit unit) {
        LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        return Date.from(localDate.minus(amountToSub, unit).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static ChronoUnit getChronoUnit(String unit) {
        if (ChronoUnit.SECONDS.name().equalsIgnoreCase(unit)) {
            return ChronoUnit.SECONDS;
        } else if (ChronoUnit.MINUTES.name().equalsIgnoreCase(unit)) {
            return ChronoUnit.MINUTES;
        } else if (ChronoUnit.HOURS.name().equalsIgnoreCase(unit)) {
            return ChronoUnit.HOURS;
        } else if (ChronoUnit.DAYS.name().equalsIgnoreCase(unit)) {
            return ChronoUnit.DAYS;
        } else if (ChronoUnit.WEEKS.name().equalsIgnoreCase(unit)) {
            return ChronoUnit.WEEKS;
        } else if (ChronoUnit.MONTHS.name().equalsIgnoreCase(unit)) {
            return ChronoUnit.MONTHS;
        } else if (ChronoUnit.YEARS.name().equalsIgnoreCase(unit)) {
            return ChronoUnit.YEARS;
        } else {
            throw new ClientParameterException(NewbiestException.COMMON_UNKNOWN_TIME_UNIT, unit);
        }
    }
}
