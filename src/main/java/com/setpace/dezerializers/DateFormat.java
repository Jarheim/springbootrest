package com.setpace.dezerializers;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateFormat {
    public static final DateTimeFormatter yearMonthDayHourMinuteSecond = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    public static final DateTimeFormatter dateTimeOptionalFraction;
    public static final DateTimeFormatter dateTimeZone = DateTimeFormatter.ISO_ZONED_DATE_TIME;
    public static final DateTimeFormatter yearMonthDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter hourMinuteSecond = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter hourMinute = DateTimeFormatter.ofPattern("HH:mm");

    static {
        DateTimeFormatter fractions = new DateTimeFormatterBuilder()
                .appendPattern(".")
                .appendFraction(ChronoField.MICRO_OF_SECOND, 1, 6, false)
                .toFormatter();
        dateTimeOptionalFraction = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendOptional(fractions)
                .toFormatter();
    }
}
