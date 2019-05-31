package com.samplayer.outconfig;

import com.samplayer.core.remote.player.interceptor.InterceptorCallback;
import com.samplayer.model.SongInfo;

/**
 * 用与客户端配置拦截器
 * <br/>
 * <b>注意:这个回调是运行在播放进程的！！！！</b><br/>
 * <b>注意:这个回调是运行在播放进程的！！！！</b><br/>
 * <b>注意:这个回调是运行在播放进程的！！！！</b>
 * <p>
 * 会回调{@link com.samplayer.listener.IPlayerListener#inInterceptorProcess()}
 */
public interface InterceptorConfig {

    /**
     * 拦截器被调用  你可以在这里修改播放地址等
     *
     * @param info     准备被播放的歌曲
     * @param callback 回调  可以在非主线程里面回调
     */
    void action(SongInfo info, InterceptorCallback callback);
}
