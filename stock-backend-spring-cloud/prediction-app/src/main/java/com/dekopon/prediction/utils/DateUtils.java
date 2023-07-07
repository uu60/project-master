package com.dekopon.prediction.utils;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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
}
