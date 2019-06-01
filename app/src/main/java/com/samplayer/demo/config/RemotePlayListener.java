package com.samplayer.demo.config;

import com.samplayer.listener.SimplePlayListener;
import com.samplayer.model.SongInfo;

/**
 * 注意  这个玩意回调在播放进程
 */
public class RemotePlayListener extends SimplePlayListener {
    @Override
    public void onPlayableStart(SongInfo songinfo) {
        super.onPlayableStart(songinfo);
    }

    @Override
    public void onProgressChange(SongInfo info, long second, long duration) {
        super.onProgressChange(info, second, duration);
    }

    @Override
    public void onBufferProgress(int percent) {
        super.onBufferProgress(percent);
    }

    @Override
    public void onInBuffer(boolean inBuffer) {
        super.onInBuffer(inBuffer);
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void inInterceptorProcess() {
        super.inInterceptorProcess();
    }

    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
    }
}
