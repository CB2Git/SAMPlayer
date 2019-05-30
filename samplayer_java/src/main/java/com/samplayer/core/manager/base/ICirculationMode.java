package com.samplayer.core.manager.base;

/**
 * 循环模式
 */
public interface ICirculationMode {

    /**
     * 默认位置
     */
    int UN_POSITION = -10000;

    /**
     * 下一首的序列
     *
     * @param currentIndex 当前位置
     * @param size         总数
     * @return 下一个位置
     */
    int next(int currentIndex, int size);

    /**
     * 上一首的序列
     *
     * @param currentIndex 当前位置
     * @param size         总数
     * @return 上一首的位置
     */
    int previous(int currentIndex, int size);

    /**
     * 获取当前播放模式的id
     *
     * @return {@link com.samplayer.model.PlayMode}
     */
    int getID();
}
