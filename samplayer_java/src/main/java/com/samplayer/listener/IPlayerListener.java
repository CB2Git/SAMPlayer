package com.samplayer.listener;

import android.support.annotation.Nullable;

import com.samplayer.model.SongInfo;

import java.util.List;

public interface IPlayerListener {

    /**
     * 开始Prepare
     * <p>
     * 播放器调用了prepareAsync
     * <p>
     * <i>在这个回调下面更新ui比较好  因为prepare->start在网络不好的时候可能需要一段时间</i>
     *
     * @param songInfo 当前播放的音乐信息
     */
    void onPrepareStart(SongInfo songInfo);

    /**
     * 音乐播放回调  当切换音乐的时候会回调 但是可能还没有开始播放 准备状态
     * 如果开始播放了则回调{@link IPlayerListener#onStart()}
     *
     * @param songinfo 当前播放的音乐信息
     */
    void onPlayableStart(SongInfo songinfo);

    /**
     * 音乐播放进度回调
     *
     * @param songinfo 当前正在播放的歌曲 可能为空
     * @param second   当前播放了多少秒
     * @param duration 歌曲时长多少秒  <h1>注意:如果歌曲播放出错或者获取时长失败duration会为0</h1>
     */
    void onProgressChange(@Nullable SongInfo songinfo, long second, long duration);

    /**
     * 歌曲缓存百分比
     *
     * @param percent 百分比 0-100
     */
    void onBufferProgress(int percent);

    /**
     * 歌曲是否在缓冲中
     *
     * @param inBuffer true 缓冲中
     */
    void onInBuffer(boolean inBuffer);

    /**
     * 当前歌曲播放完毕或者出现错误了
     */
    void onComplete();

    /**
     * 歌曲开始播放了
     */
    void onStart();

    /**
     * 歌曲暂停了
     */
    void onPause();

    /**
     * 停止播放
     * <p>
     * 当重新设置播放列表的时候这个也会回调一次{@link com.samplayer.core.manager.base.IPlayer#setPlayList(List, boolean)}
     */
    void onStop();

    /**
     * 拦截器接管了 正在处理
     * <p>
     * 由于拦截器中可以进行耗时操作，所以如果你使用了拦截器 请使用这个回调去更新歌曲信息并给与用户加载中的提示
     * <p>
     * 如果拦截器中出现错误，会回调{@link IPlayerListener#onError(int, int)}
     * 如果拦截器处理完毕 会回调{@link IPlayerListener#onPlayableStart(SongInfo)}
     *
     * @param info 正在被拦截器处理的歌曲信息
     */
    void inInterceptorProcess(SongInfo info);

    /**
     * 发生了错误
     *
     * @param what
     * @param extra
     */
    void onError(int what, int extra);
}
