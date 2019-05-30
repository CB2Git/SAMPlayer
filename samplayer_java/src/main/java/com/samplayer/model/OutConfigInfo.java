package com.samplayer.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.samplayer.core.utils.ReflectInjectUtils;
import com.samplayer.outconfig.InterceptorConfig;
import com.samplayer.outconfig.NotificationConfig;

public class OutConfigInfo {

    private InterceptorConfig mInterceptorConfig;

    private NotificationConfig mNotificationConfig;

    public OutConfigInfo() {

    }

    public void inject(Context context) {
        mInterceptorConfig = ReflectInjectUtils.inject("com.samplayer.demo.config.Interceptor");
        mNotificationConfig = ReflectInjectUtils.inject("com.samplayer.demo.config.NotificationConfig");
        mNotificationConfig.init(context);
    }

    @Nullable
    public InterceptorConfig getInterceptorConfig() {
        return mInterceptorConfig;
    }

    @Nullable
    public NotificationConfig getNotificationConfig() {
        return mNotificationConfig;
    }
}
