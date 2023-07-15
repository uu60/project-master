package com.dekopon.prediction.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * @author dekopon
 * @since 2023/7/8 14:49
 */
public class DateUtils {
    public static boolean isISOInstantFormat(String str) {
        try {
            DateTimeFormatter.ISO_INSTANT.parse(str);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isISOOffsetDateTimeFormat(String str) {
        try {
            DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(str);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }
}
