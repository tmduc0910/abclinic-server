package com.abclinic.server.common.utils;

import com.abclinic.server.common.constant.Constant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    public static LocalDate parseDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT);
        return LocalDate.parse(date, formatter);
    }

    public static int getDistanceByYear(LocalDate from, LocalDate to) {
        return to.getYear() - from.getYear();
    }

    public static int getDistanceByYear(LocalDate from) {
        return getDistanceByYear(from, getCurrentDate());
    }
}
