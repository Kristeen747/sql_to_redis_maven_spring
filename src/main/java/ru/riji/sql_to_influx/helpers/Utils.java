package ru.riji.sql_to_influx.helpers;

import org.joda.time.format.DateTimeFormat;

import java.time.format.DateTimeFormatter;

public class Utils {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
}
