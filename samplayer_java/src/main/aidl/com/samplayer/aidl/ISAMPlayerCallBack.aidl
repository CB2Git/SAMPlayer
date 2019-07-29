// ISAMPlayerCallBack.aidl
package com.samplayer.aidl;
import com.samplayer.model.SongInfo;


interface ISAMPlayerCallBack {

    /**
     * 开始Prepare
     * <p>
     * 播放器调用了prepareAsync
     * <p>
     * <i>在这个回调下面更新ui比较好  因为prepare->start在网络不好的时候可能需要一段时间</i>
     *
     * @param songInfo 当前播放的音乐信息
     */
    void onPrepareStart(inout SongInfo songInfo);

    /**
     * 音乐播放回调  当切换音乐的时候会回调 但是可能还没有开始播放
     *
     * @param songinfo 当前播放的音乐信息
     */
    void onPlayableStart(inout SongInfo songinfo);

    /**
     * 音乐播放进度回调
     * @param songinfo 当前正在播放的歌曲
     * @param second   当前播放了多少秒
     * @param duration 歌曲时长多少秒
     */
    void onProgressChange(inout SongInfo songinfo,long second, long duration);

    /**
     * 歌曲缓存百分比
     * @param percent  百分比 0-100
     */
    void onBufferProgress(int percent);

    /**
     * 歌曲是否在缓存中
     * @param inBuffer true 缓冲中
     */
    void onInBuffer(boolean inBuffer);

    /**
     * 当前歌曲播放完毕
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
     */
    void onStop();

    /**
    * 正在被拦截器处理
    * */
    void inInterceptorProcess(inout SongInfo songinfo);

    /**
     * 发生了错误
     * @param what
     * @param extra
     */
    void onError(int what, int extra);
}
