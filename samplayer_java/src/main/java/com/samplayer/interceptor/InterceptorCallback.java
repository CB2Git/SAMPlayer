package com.samplayer.interceptor;

import com.samplayer.listener.IPlayerListener;
import com.samplayer.model.SongInfo;

/**
 * 拦截器用来通知播放器  可以在非主线程调用
 */
public interface InterceptorCallback {

    /**
     * 处理完毕可以播放  可以在非主线程调用
     *
     * @param info
     */
    void onContinue(SongInfo info);

    /**
     * 直接报错  会回调{@link IPlayerListener#onError(int, int)}
     *
     * @param errorCode 错误码
     */
    void onError(int errorCode);

    /**
     * 开始处理了  会回调{@link IPlayerListener#inInterceptorProcess(SongInfo)}
     */
    void inProcess(SongInfo info);
}
