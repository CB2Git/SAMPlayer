package com.samplayer.model;

/**
 * 播放模式
 */
public class PlayMode {

    /**
     * 未知模式  可能是服务没有连接  所以没有获取到播放模式  默认为{@link PlayMode#ORDER}
     */
    public static final int UN_KNOW = 0;

    /**
     * 顺序播放
     */
    public static final int ORDER = 1;

    /**
     * 单曲播放
     */
    public static final int SINGLE = 2;

    /**
     * 随机播放
     */
    public static final int SHUFFLE = 3;
}
