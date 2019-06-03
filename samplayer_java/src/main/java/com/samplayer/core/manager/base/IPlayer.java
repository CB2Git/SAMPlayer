package com.samplayer.core.manager.base;


import android.support.annotation.Nullable;

import com.samplayer.listener.IPlayerListener;
import com.samplayer.model.SongInfo;

import java.util.List;

/**
 * 对外提供的播放控制接口
 */
public interface IPlayer {

    /**
     * 设置是否自动连接
     *
     * @param auto true 当执行一个操作但是没有连接的时候会自动连接
     *             注意:<b>没有连接前的操作会被忽略掉<b/>
     */
    void setAutoConnect(boolean auto);

    /**
     * 设置播放列表
     * <p>
     * 会清空当前播放列表并停止当前播放的
     *
     * @param songInfos 新的播放队列
     * @param autoPlay  true 自动播放队列第一首
     */
    void setPlayList(List<SongInfo> songInfos, boolean autoPlay);

    /**
     * 添加播放列表
     */
    void appendPlayList(List<SongInfo> songInfos);

    /**
     * 获取播放列表
     */
    List<SongInfo> getPlayList();

    /**
     * 清空播放列表  会导致停止当前播放并回调{@link IPlayerListener#onStop()}
     */
    void clearPlayList();

    /**
     * 删除指定位置的音乐  如果正在播放  那么会直接停止播放
     */
    boolean removeAt(int index);

    /**
     * 删除指定的音乐 如果正在播放  那么会直接停止播放
     */
    boolean removeItem(SongInfo songInfo);

    /**
     * 播放
     * 如果没有播放  根据循环模式选出一首歌进行播放
     * 如果已经在播放了  不做任何处理  如果是暂停状态 那么会继续开始播
     */
    void play();

    /**
     * 播放指定音乐
     * <p>
     * 如果当前正在播放此音乐则会重新开始播放
     *
     * @param songInfo
     */
    void play(SongInfo songInfo);


    /**
     * 设置播放模式 {@link com.samplayer.model.PlayMode}
     */
    void setPlayMode(int playMode);

    /**
     * 获取当前播放模式{@link com.samplayer.model.PlayMode}
     */
    int getPlayMode();

    /**
     * 当前是否正在播放
     */
    boolean isPlaying();

    /**
     * 切换播放状态
     */
    void toggle();

    /**
     * 下一首
     */
    void next();

    /**
     * 上一首
     */
    void previous();

    /**
     * 播放指定位置的音频
     */
    void skipTo(int index);

    /**
     * 停止播放
     */
    void stop();

    /**
     * 调整播放位置
     *
     * @param ms 播放位置  毫秒
     */
    void seekTo(long ms);

    /**
     * 添加播放器监听  主要是一些回调
     *
     * @param listener
     */
    void addPlayListener(IPlayerListener listener);

    /**
     * 获取当前播放进度 当没有播放或者获取播放进度失败，这个玩意返回0
     *
     * @return 当前播放进度  ms
     */
    long getCurrentPosition();

    /**
     * 获取歌曲时长  当没有播放或者获取时长失败，这个玩意返回0
     *
     * @return 时长 ms
     */
    long getDuration();

    /**
     * 获取当前正在播放的歌曲
     *
     * @return 如果没有播放中的歌曲 则返回null
     */
    @Nullable
    SongInfo getCurrentPlayable();

    /**
     * 删除播放器监听
     *
     * @param listener
     */
    void removeListener(IPlayerListener listener);
}
