package com.samplayer.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.samplayer.interceptor.Interceptor;
import com.samplayer.listener.IPlayerListener;
import com.samplayer.outconfig.InterceptorConfig;
import com.samplayer.outconfig.NotificationConfig;
import com.samplayer.outconfig.OutConfigFactory;

import java.util.List;

public class OutConfigInfo {

    /**
     * 拦截器
     */
    @Nullable
    private List<Interceptor> mInterceptors;

    /**
     * 通知栏
     */
    @Nullable
    private NotificationConfig mNotificationConfig;

    /**
     * 播放监听
     */
    @Nullable
    private IPlayerListener mPlayerListener;

    public OutConfigInfo() {

    }

    public void inject(Context context) {
        mInterceptors = OutConfigFactory.createInterceptors();
        mNotificationConfig = OutConfigFactory.createNotificationConfig();
        mPlayerListener = OutConfigFactory.createPlayerListener();
        if (mNotificationConfig != null) {
            mNotificationConfig.init(context);
        }
    }

    @Nullable
    public List<Interceptor> getInterceptors() {
        return mInterceptors;
    }

    @Nullable
    public NotificationConfig getNotificationConfig() {
        return mNotificationConfig;
    }

    @Nullable
    public IPlayerListener getPlayerListener() {
        return mPlayerListener;
    }
}
