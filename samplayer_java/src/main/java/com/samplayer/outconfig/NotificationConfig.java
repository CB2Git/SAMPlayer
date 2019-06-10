package com.samplayer.outconfig;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.samplayer.core.remote.player.cmd.CmdHandlerHelper;
import com.samplayer.model.SongInfo;
import com.samplayer.utils.SAMLog;

/**
 * 用于客户端配置自己的通知栏
 * <br/>
 * <b>注意:这个回调是运行在播放进程的！！！！</b><br/>
 * <b>注意:这个回调是运行在播放进程的！！！！</b><br/>
 * <b>注意:这个回调是运行在播放进程的！！！！</b>
 */
public abstract class NotificationConfig extends BroadcastReceiver {

    private static final String TAG = "NotificationConfig";

    public static final int NOTIFICATION_ID = 100567;

    /**
     * 播放
     */
    public static final String ACTION_SAM_PLAY = "ACTION_SAM_PLAY";

    /**
     * 暂停
     */
    public static final String ACTION_SAM_PAUSE = "ACTION_SAM_PAUSE";

    /**
     * 切换播放状态
     */
    public static final String ACTION_SAM_TOGGLE = "ACTION_SAM_TOGGLE";

    /**
     * 下一曲
     */
    public static final String ACTION_SAM_NEXT = "ACTION_SAM_NEXT";

    /**
     * 上一曲
     */
    public static final String ACTION_SAM_PREVIOUS = "ACTION_SAM_PREVIOUS";

    /**
     * 停止播放
     */
    public static final String ACTION_SAM_STOP = "ACTION_SAM_STOP";

    private Context mContext;

    /**
     * 显示歌曲信息
     *
     * @param context Service
     * @param info    歌曲信息
     */
    public abstract Notification buildNotification(Context context, SongInfo info);


    /**
     * 播放状态改变
     *
     * @param isPlaying true 正在播放
     */
    public abstract void onPlayStateChange(boolean isPlaying);

    /**
     * 处理action
     *
     * @param action
     */
    protected void handleAction(String action) {

    }

    /**
     * 注册一个ACTION 在{@link NotificationConfig#handleAction(String)}中处理
     *
     * @param actions
     */
    protected void addAction(String... actions) {
        IntentFilter filter = new IntentFilter();
        for (String item : actions) {
            filter.addAction(item);
        }
        mContext.registerReceiver(this, filter);
    }

    public void init(Context context) {
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SAM_PLAY);
        filter.addAction(ACTION_SAM_PAUSE);
        filter.addAction(ACTION_SAM_TOGGLE);
        filter.addAction(ACTION_SAM_NEXT);
        filter.addAction(ACTION_SAM_PREVIOUS);
        filter.addAction(ACTION_SAM_STOP);
        mContext.registerReceiver(this, filter);
    }

    /**
     * 当Service Destroy的时候会被调用
     */
    public void release() {
        mContext.unregisterReceiver(this);
    }

    /**
     * 更新通知  辅助功能而已
     *
     * @param context
     * @param notification
     */
    protected void updateNotification(Context context, Notification notification) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NotificationConfig.NOTIFICATION_ID, notification);
    }


    /**
     * 获取点击广播 辅助功能而已
     *
     * @param context
     * @param action
     */
    protected PendingIntent getPendingBroadcastWithAction(Context context, String action) {
        Intent intent = new Intent(action);
        return PendingIntent.getBroadcast(context, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SAMLog.i(TAG, action);
        if (ACTION_SAM_PLAY.equals(action)) {
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PLAY);
        } else if (ACTION_SAM_PAUSE.equals(action)) {
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PAUSE);
        } else if (ACTION_SAM_TOGGLE.equals(action)) {
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_TOGGLE);
        } else if (ACTION_SAM_NEXT.equals(action)) {
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_NEXT);
        } else if (ACTION_SAM_PREVIOUS.equals(action)) {
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PREVIOUS);
        } else if (ACTION_SAM_STOP.equals(action)) {
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_STOP);
        } else {
            handleAction(action);
        }
    }
}
