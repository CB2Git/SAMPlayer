package com.samplayer.core.remote.player.cmd;

import android.os.Handler;
import android.os.Message;

/**
 * 用与接收外部发送的控制命令 然后进行转发
 * 比如{@link AudioFocusManager}发出的焦点事件
 * 比如{@link MediaButtonReceiver}发出的线控事件
 */
public class CmdHandlerHelper {

    public static final int CMD_PLAY = 1;
    public static final int CMD_PAUSE = 2;
    public static final int CMD_NEXT = 3;
    public static final int CMD_PREVIOUS = 4;
    public static final int CMD_TOGGLE = 5;
    public static final int CMD_STOP = 6;
    public static final int CMD_DOWN_VOLUME = 7;
    public static final int CMD_UP_VOLUME = 8;

    private static Handler mHandler;

    /***
     * 用与接收事件的Handler
     */
    public static void init(Handler handler) {
        mHandler = handler;
    }

    public static void sendCmd(int cmd) {
        Message message = mHandler.obtainMessage(cmd);
        mHandler.sendMessage(message);
    }
}
