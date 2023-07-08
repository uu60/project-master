package com.dekopon.display.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    public static long nextDayMillis(long timeStamp) {
        return (timeStamp / (24 * 60 * 60 * 1000) + 1) * (24 * 60 * 60 * 1000);
    }

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

    public static boolean isISOFormat(String str) {
        return isISOInstantFormat(str) || isISOOffsetDateTimeFormat(str);
    }
}
