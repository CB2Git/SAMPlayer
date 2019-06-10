package com.samplayer.outconfig;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 定时播放的配置
 */
public class TimerConfig implements Parcelable {

    public static final String ACTION_TIMER_START = "start";

    public static final String ACTION_TIMER_UPDATE = "update";

    public static final String ACTION_TIMER_COMPLETE = "complete";

    public static final String ACTION_TIMER_STOP = "stop";

    public static final String KEY_TIMER = "residue_time";

    /**
     * 当时间到了以后继续播放完当前正在播放的  推荐使用这个模式
     */
    public static final int MODE_RELAX = 0;

    /**
     * 绝对模式  当时间到了以后就直接关闭播放
     */
    public static final int MODE_ABS = 1;

    private long second;

    private int mode = MODE_RELAX;

    public TimerConfig(long second) {
        this(second, MODE_RELAX);
    }

    public TimerConfig(long second, int mode) {
        this.second = second;
        this.mode = mode;
    }

    protected TimerConfig(Parcel in) {
        second = in.readLong();
        mode = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(second);
        dest.writeInt(mode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TimerConfig> CREATOR = new Creator<TimerConfig>() {
        @Override
        public TimerConfig createFromParcel(Parcel in) {
            return new TimerConfig(in);
        }

        @Override
        public TimerConfig[] newArray(int size) {
            return new TimerConfig[size];
        }
    };

    public long getSecond() {
        return second;
    }

    public int getMode() {
        return mode;
    }

    public void setSecond(long second) {
        this.second = second;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "TimerConfig{" +
                "second=" + second +
                ", mode=" + mode +
                '}';
    }
}
