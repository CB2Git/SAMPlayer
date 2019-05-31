package com.samplayer.demo.config;

import com.samplayer.core.remote.player.interceptor.InterceptorCallback;
import com.samplayer.model.SongInfo;
import com.samplayer.outconfig.InterceptorConfig;
import com.samplayer.utils.SAMLog;

public class OutInterceptor implements InterceptorConfig {

    private static final String TAG = "Interceptor";

    @Override
    public void action(SongInfo info, InterceptorCallback callback) {
        SAMLog.i(TAG, info.getSongUrl());
        callback.inProcess();
        callback.onContinue(info);
//
//        callback.onError(23333);
    }
}
