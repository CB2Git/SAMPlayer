package com.samplayer.core.remote.player.cmd;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.samplayer.outconfig.TimerConfig;

/**
 * 定时停止播放
 */
public class TimerHandler extends Handler {

    private static final int MSG_TIMER_UPDATE = 1;

    private TimerConfig mTimerConfig;

    private long mStartTime = 0;

    private OnTimerListener mOnTimerListener;

    private boolean isComplete = false;

    public TimerHandler(TimerConfig timerConfig) {
        super(Looper.getMainLooper());
        mTimerConfig = timerConfig;
    }

    public TimerConfig getTimerConfig() {
        return mTimerConfig;
    }

    public void setOnTimerListener(OnTimerListener onTimerListener) {
        mOnTimerListener = onTimerListener;
    }

    public void startTimer() {
        mStartTime = System.currentTimeMillis() / 1000;
        removeCallbacksAndMessages(null);

        if (mOnTimerListener != null) {
            mOnTimerListener.onTimerStart(mTimerConfig);
        }

        sendEmptyMessage(MSG_TIMER_UPDATE);
    }

    public void endTimer() {
        mStartTime = 0;
        removeCallbacksAndMessages(null);

        if (mOnTimerListener != null && !isComplete) {
            mOnTimerListener.onTimerStop(mTimerConfig);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == MSG_TIMER_UPDATE) {
            long time = System.currentTimeMillis() / 1000 - mStartTime;
            if (time < mTimerConfig.getSecond()) {
                if (mOnTimerListener != null) {
                    mOnTimerListener.onTimerUpdate(mTimerConfig, mTimerConfig.getSecond() - time);
                }
                isComplete = false;
                sendEmptyMessageDelayed(MSG_TIMER_UPDATE, 900);
            } else {
                isComplete = true;
                if (mOnTimerListener != null) {
                    mOnTimerListener.onTimerComplete(mTimerConfig);
                }
            }
        }
    }

    public interface OnTimerListener {

        /**
         * 开始倒计时
         */
        void onTimerStart(TimerConfig config);

        /**
         * 计时更新
         *
         * @param residueSecond 剩余的秒数
         */
        void onTimerUpdate(TimerConfig config, long residueSecond);

        /**
         * 时间完成
         */
        void onTimerComplete(TimerConfig config);

        /**
         * 即时被停止了
         */
        void onTimerStop(TimerConfig config);
    }
}
