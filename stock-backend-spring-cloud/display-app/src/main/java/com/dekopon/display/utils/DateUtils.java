package com.dekopon.display.utils;

public class DateUtils {
    public static long nextDayMillis(long timeStamp) {
        return (timeStamp / (24 * 60 * 60 * 1000) + 1) * (24 * 60 * 60 * 1000);
    }
}
