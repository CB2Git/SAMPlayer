package com.samplayer.core.utils;

import java.lang.reflect.Constructor;

public class ReflectInjectUtils {

    public static <T> T inject(String clsName) {
        try {
            Class<?> aClass = Class.forName(clsName);
            Constructor<?> declaredConstructor = aClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            Object instance = declaredConstructor.newInstance();
            return (T) instance;
        } catch (Exception e) {
        }
        return null;
    }
}
