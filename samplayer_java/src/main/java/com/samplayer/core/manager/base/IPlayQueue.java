package com.samplayer.core.manager.base;

import android.support.annotation.Nullable;

import com.samplayer.model.SongInfo;

import java.util.List;

/**
 * 播放队列相关操作
 */
public interface IPlayQueue {

    /**
     * 设置当前循环模式
     *
     * @param circulationMode 单曲播放 {@link com.samplayer.core.manager.circulation.SingleCirculationMode}<br/>
     *                        循环播放 {@link com.samplayer.core.manager.circulation.OrderCirculationMode}<br/>
     *                        随机播放 {@link com.samplayer.core.manager.circulation.ShuffleCirculationMode}
     */
    void setCirculationMode(ICirculationMode circulationMode);


    /**
     * 获取当前播放模式
     *
     * @return
     */
    ICirculationMode getCirculationMode();

    /**
     * 设置播放列表
     * <p>
     * 清空当前播放列表并停止当前播放
     *
     * @param songInfos 新的播放队列
     */
    void setPlayList(List<SongInfo> songInfos);

    /**
     * 添加播放列表
     */
    void appendPlayList(List<SongInfo> songInfos);

    /**
     * 清空播放列表
     */
    void clearPlayList();

    /**
     * 获取播放列表
     */
    List<SongInfo> getPlayList();

    /**
     * 删除指定位置的音乐
     * <p>
     *
     * @return 被删除的音乐信息  如果为null 表示删除失败或者不存在
     */
    @Nullable
    SongInfo removeAt(int index);

    /**
     * 删除指定的音乐
     * <p>
     *
     * @return 被删除的音乐信息  如果为null 表示删除失败或者不存在
     */
    @Nullable
    SongInfo removeItem(SongInfo songInfo);

    /**
     * 下一首
     *
     * @return
     */
    @Nullable
    SongInfo next();

    /**
     * 上一首
     *
     * @return
     */
    @Nullable
    SongInfo previous();

    /**
     * 跳转播放哪一首
     *
     * @param index
     * @return
     */
    @Nullable
    SongInfo skipTo(int index);


    /**
     * 跳转到播放指定歌曲
     *
     * @param songInfo 指定播放的歌曲  如果不在播放列表中，那么此歌曲加入到播放列表当前序列的下面
     * @return 在播放队列中的序列
     */
    int skipToItem(SongInfo songInfo);

    /**
     * 获取当前播放的音乐信息
     *
     * @return
     */
    @Nullable
    SongInfo getCurrent();

}
