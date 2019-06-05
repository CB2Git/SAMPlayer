package com.samplayer;

import android.content.Context;

import com.samplayer.utils.SAMLog;

public class SAMConfig {

    private static final String TAG = "SMAManager";

    private static Context mAppContext;

    /**
     * 初始化
     */
    public static void init(Context context) {
        mAppContext = context;
    }

    /**
     * 是否打印日志
     */
    public static void debug(boolean enableDebug) {
        SAMLog.enable(enableDebug);
    }

    public static Context getAppContext() {
        if (mAppContext == null) {
            SAMLog.w(TAG, "getAppContext：没有初始化");
        }
        return mAppContext;
    }
}
