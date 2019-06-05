package com.samplayer.outconfig;

import android.support.annotation.Nullable;

import com.samplayer.interceptor.Interceptor;
import com.samplayer.listener.IPlayerListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于初始化配置
 */
public class OutConfigFactory {

    private static List<Class<? extends Interceptor>> sInterceptor = new ArrayList<>();

    private static Class<? extends NotificationConfig> sNotificationConfig;

    private static Class<? extends IPlayerListener> sPlayerListener;

    /**
     * 设置拦截器
     */
    public static void addInterceptor(Class<? extends Interceptor> interceptor) {
        sInterceptor.add(interceptor);
    }

    /**
     * 设置通知栏
     */
    public static void setNotificationConfig(Class<? extends NotificationConfig> config) {
        sNotificationConfig = config;
    }

    /**
     * 此监听将回调在播放进程，当你需要记录播放历史、上报流水时，推荐使用这个 因为ui进程可能被杀死
     */
    public static void setRemotePlayerListener(Class<? extends IPlayerListener> playerListener) {
        OutConfigFactory.sPlayerListener = playerListener;
    }

    public static List<Interceptor> createInterceptors() {
        if (sInterceptor == null) {
            return null;
        }

        List<Interceptor> interceptors = new ArrayList<>();

        for (Class<? extends Interceptor> item : sInterceptor) {
            Interceptor interceptor = create(item);
            if (interceptor != null) {
                interceptors.add(interceptor);
            }
        }
        return interceptors;
    }

    @Nullable
    public static NotificationConfig createNotificationConfig() {
        return create(sNotificationConfig);
    }

    @Nullable
    public static IPlayerListener createPlayerListener() {
        return create(sPlayerListener);
    }

    private static <T> T create(Class<? extends T> cls) {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
