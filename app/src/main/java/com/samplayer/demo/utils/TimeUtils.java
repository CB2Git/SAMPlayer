package com.samplayer.demo.utils;

import java.util.Locale;

public class TimeUtils {

    public static String formatSecond(long secondCount) {
        long second = secondCount % 60;
        long minute = (secondCount - second) / 60;
        return String.format(Locale.CHINA, "%02d:%02d", minute, second);
    }
}
