package com.samplayer.outconfig;

import android.support.annotation.Nullable;

import com.samplayer.listener.IPlayerListener;

/**
 * 用于初始化配置
 */
public class OutConfigFactory {

    private static Class<? extends InterceptorConfig> sInterceptorConfig;

    private static Class<? extends NotificationConfig> sNotificationConfig;

    private static Class<? extends IPlayerListener> sPlayerListener;

    /**
     * 设置拦截器
     */
    public static void setInterceptorConfig(Class<? extends InterceptorConfig> config) {
        sInterceptorConfig = config;
    }

    /**
     * 设置通知栏
     */
    public static void setNotificationConfig(Class<? extends NotificationConfig> config) {
        sNotificationConfig = config;
    }

    public static void setPlayerListener(Class<? extends IPlayerListener> playerListener) {
        OutConfigFactory.sPlayerListener = playerListener;
    }

    @Nullable
    public static InterceptorConfig createInterceptorConfig() {
        if (sInterceptorConfig != null) {
            try {
                return sInterceptorConfig.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static NotificationConfig createNotificationConfig() {
        if (sNotificationConfig != null) {
            try {
                return sNotificationConfig.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    public static IPlayerListener createPlayerListener() {
        if (sPlayerListener != null) {
            try {
                return sPlayerListener.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
