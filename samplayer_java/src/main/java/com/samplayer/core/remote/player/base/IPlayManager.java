package com.samplayer.core.remote.player.base;

import android.support.annotation.Nullable;

import com.samplayer.interceptor.Interceptor;
import com.samplayer.model.SongInfo;

import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public interface IPlayManager {

    /**
     * 获取当前播放器
     */
    IMediaPlayer getCurrentPlayer();

    /**
     * 播放指定的音乐
     * <p>
     * 当正在播放此歌曲的时候会重新开始播放
     *
     * @param songInfo 音乐信息
     */
    void play(SongInfo songInfo);

    /**
     * 播放指定的音乐
     *
     * @param songInfo    音乐信息
     * @param allowReplay 是否允许重新开始播放<p><p/>
     *                    true  当正在播放此歌曲的时候会重新开始播放
     *                    false  当正在播放此歌曲的时候不做任何处理  没有播放则会开始播放
     */
    void play(SongInfo songInfo, boolean allowReplay);

    /**
     * 设置拦截器
     */
    void setInterceptor(List<Interceptor> interceptors);

    /**
     * 停止播放
     */
    void stop();

    /**
     * 获取当前播放信息
     *
     * @return 如果返回值为null 则表示当前没有播放
     */
    @Nullable
    SongInfo getCurrentPlayInfo();

    /**
     * 回收资源
     */
    void release();

    /**
     * 设置播放相关监听
     *
     * @param listener
     */
    void setPlayListener(PlayListener listener);

    interface PlayListener {

        /**
         * 开始Prepare
         * <p>
         * 播放器调用了prepareAsync
         *
         * @param songInfo
         */
        void onPrepareStart(SongInfo songInfo);

        /**
         * 异步结束
         */
        void onPrepare(IMediaPlayer iMediaPlayer);

        /**
         * 播放回调
         *
         * @param songInfo
         */
        void onPlayableStart(SongInfo songInfo);

        /**
         * 开始播放回调
         */
        void onStart();

        /**
         * 停止播放的时候的回调
         */
        void onStop();

        /**
         * 开始缓冲
         */
        void onBufferStart();

        /**
         * 正在被拦截器处理
         *
         * @param info 正在被处理的歌曲信息
         */
        void inInterceptorProcess(SongInfo info);

        /**
         * 缓冲结束
         */
        void onBufferEnd();


        void onInfo(int what, int extra);

        /**
         * 当前缓冲了多少
         *
         * @param percent 百分比
         */
        void onBufferingUpdate(int percent);

        /**
         * 当前播放完毕 播放失败也会回调这里
         */
        void onComplete();

        /**
         * 播放失败
         */
        void onError(int what, int extra);
    }
}
