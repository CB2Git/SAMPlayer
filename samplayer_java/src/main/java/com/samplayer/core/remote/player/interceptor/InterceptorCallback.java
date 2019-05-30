package com.samplayer.core.remote.player.interceptor;

import com.samplayer.model.SongInfo;

/**
 * 拦截器用来通知播放器  可以在非主线程调用
 */
public interface InterceptorCallback {

    /**
     * 处理完毕可以播放
     *
     * @param info
     */
    void onContinue(SongInfo info);

    /**
     * 直接报错
     *
     * @param errorCode 错误码
     */
    void onError(int errorCode);

    /**
     * 开始处理了
     */
    void inProcess();
}
