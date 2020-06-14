package com.abclinic.server.common.utils;

import com.abclinic.server.common.constant.Constant;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author tmduc
 * @package com.abclinic.server.common.utils
 * @created 5/3/2020 10:02 AM
 */
public class DateTimeUtils {
    public static LocalDateTime getCurrent() {
        return LocalDateTime.now();
    }

    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public static LocalTime getCurrentTime() {
        return LocalTime.now();
    }

    public static LocalDateTime plusSeconds(LocalDateTime dateTime, long seconds) {
        return dateTime.plusSeconds(seconds);
    }

    public static LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT);
        return LocalDate.parse(date, formatter);
    }

    public static LocalDateTime parseDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.DATE_TIME_FORMAT);
        return LocalDateTime.parse(dateTime, formatter);
    }

    public static int getDistanceByYear(LocalDate from, LocalDate to) {
        return to.getYear() - from.getYear();
    }

    public static int getDistanceByYear(LocalDate from) {
        return getDistanceByYear(from, getCurrentDate());
    }

    public static long getDistanceByHour(LocalDateTime from, LocalDateTime to) {
        return ChronoUnit.HOURS.between(from, to);
    }

    public static int secondsToHours(long time) {
        return (int) (time / Constant.SEC_OF_MIN / Constant.MIN_OF_HOUR);
    }

    public static int secondsToDays(long time) {
        return secondsToHours(time) / Constant.HOUR_OF_DAY;
    }

    public static int secondsToWeeks(long time) {
        return secondsToDays(time) / Constant.DAY_OF_WEEK;
    }

    public static int secondsToMonths(long time) {
        YearMonth yearMonth = YearMonth.now();
        return secondsToDays(time) / yearMonth.lengthOfMonth();
    }

    public static Date toDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }
}
