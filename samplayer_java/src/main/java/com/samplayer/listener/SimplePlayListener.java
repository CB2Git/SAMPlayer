package com.samplayer.listener;

import com.samplayer.model.SongInfo;
import com.samplayer.utils.SAMLog;

public class SimplePlayListener implements IPlayerListener {

    private static final String TAG = "SimplePlayListener";

    @Override
    public void onPlayableStart(SongInfo songinfo) {
        SAMLog.i(TAG, "onPlayableStart ");
    }

    @Override
    public void onProgressChange(SongInfo info, long second, long duration) {
        SAMLog.ifmt(TAG, "onProgressChange: %d-%d", second, duration);
    }

    @Override
    public void onBufferProgress(int percent) {
        SAMLog.i(TAG, "onBufferProgress: " + percent);
    }

    @Override
    public void onInBuffer(boolean inBuffer) {
        SAMLog.i(TAG, "onInBuffer: " + inBuffer);
    }

    @Override
    public void onComplete() {
        SAMLog.i(TAG, "onComplete");
    }

    @Override
    public void onStart() {
        SAMLog.i(TAG, "onStart");
    }

    @Override
    public void onPause() {
        SAMLog.i(TAG, "onPause ");
    }

    @Override
    public void onStop() {
        SAMLog.i(TAG, "onStop ");
    }

    @Override
    public void inInterceptorProcess(SongInfo info) {
        SAMLog.i(TAG, "inInterceptorProcess ");
    }

    @Override
    public void onError(int what, int extra) {
        SAMLog.wfmt(TAG, "onError (%d,%d)", what, extra);
    }
}
