package com.samplayer.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.samplayer.listener.IPlayerListener;
import com.samplayer.outconfig.InterceptorConfig;
import com.samplayer.outconfig.NotificationConfig;
import com.samplayer.outconfig.OutConfigFactory;

public class OutConfigInfo {

    @Nullable
    private InterceptorConfig mInterceptorConfig;

    @Nullable
    private NotificationConfig mNotificationConfig;

    private IPlayerListener mPlayerListener;

    public OutConfigInfo() {

    }

    public void inject(Context context) {
        mInterceptorConfig = OutConfigFactory.createInterceptorConfig();
        mNotificationConfig = OutConfigFactory.createNotificationConfig();
        if (mNotificationConfig != null) {
            mNotificationConfig.init(context);
        }
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
