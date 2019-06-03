package com.samplayer.demo.config;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.samplayer.demo.R;
import com.samplayer.model.SongInfo;

/**
 * 这个玩意运行在播放进程！！！！
 */
public class OutNotificationConfig extends com.samplayer.outconfig.NotificationConfig {

    private static final String channelId = "SAMPlayerID";
    private static final String channelName = "SAMPlayer";
    private static final String channelDescription = "SAMPlayer";

    private static final String ACTION_MY_XIA_XIE_DE = "xiaxiede";

    private Context mContext;

    private SongInfo mSongInfo;

    private NotificationCompat.Builder mBuilder;


    public OutNotificationConfig() {
    }


    @Override
    public void init(Context context) {
        super.init(context);
        //添加自定义的点击事件
        addAction(ACTION_MY_XIA_XIE_DE);
    }

    @Override
    protected void handleAction(String action) {
        super.handleAction(action);
        if (ACTION_MY_XIA_XIE_DE.equals(action)) {
            Toast.makeText(mContext, "自定义的动作被点击了", Toast.LENGTH_SHORT).show();
            mBuilder.setCustomBigContentView(buildBigView(mContext, mSongInfo, true, true));
            updateNotification(mContext, mBuilder.build());
        }
    }

    @Override
    public Notification buildNotification(Context context, SongInfo info) {

        mContext = context;
        mSongInfo = info;

        buildNotificationChannel(context);
        mBuilder = new NotificationCompat.Builder(context, channelId);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("通知栏")
                .setContentText("通知栏内容")
                .setWhen(System.currentTimeMillis());

        mBuilder.setContent(buildView(context, info, true));
        mBuilder.setCustomBigContentView(buildBigView(context, info, true, false));

        //点击通知栏跳转
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        PendingIntent clickIntent = PendingIntent.getActivity(context, 0, launchIntentForPackage, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(clickIntent);
        return mBuilder.build();
    }

    @Override
    public void onPlayStateChange(boolean isPlaying) {
        mBuilder.setContent(buildView(mContext, mSongInfo, isPlaying));
        mBuilder.setCustomBigContentView(buildBigView(mContext, mSongInfo, isPlaying, false));

        updateNotification(mContext, mBuilder.build());
    }

    private void buildNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(channelDescription);//设置渠道描述

            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private RemoteViews buildView(Context context, SongInfo info, boolean isPlay) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notifycation_layout);
        remoteView.setTextViewText(R.id.ntf_title, info.getSongName());
        remoteView.setTextViewText(R.id.ntf_singer, info.getArtist());
        remoteView.setImageViewResource(R.id.ntf_statue, isPlay ? R.mipmap.ic_pause : R.mipmap.ic_play);


        remoteView.setOnClickPendingIntent(R.id.ntf_statue, getPendingBroadcastWithAction(context, ACTION_SAM_TOGGLE));
        remoteView.setOnClickPendingIntent(R.id.ntf_next, getPendingBroadcastWithAction(context, ACTION_SAM_NEXT));
        remoteView.setOnClickPendingIntent(R.id.ntf_pre, getPendingBroadcastWithAction(context, ACTION_SAM_PREVIOUS));
        remoteView.setOnClickPendingIntent(R.id.ntf_close, getPendingBroadcastWithAction(context, ACTION_SAM_STOP));
        return remoteView;
    }

    private RemoteViews buildBigView(Context context, SongInfo info, boolean isPlay, boolean like) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.notifycation_big_layout);
        remoteView.setTextViewText(R.id.ntf_title, info.getSongName());
        remoteView.setTextViewText(R.id.ntf_singer, info.getArtist());
        remoteView.setTextViewText(R.id.ntf_like, like ? "被点击了" : "点我试试");
        remoteView.setImageViewResource(R.id.ntf_statue_big, isPlay ? R.mipmap.ic_pause : R.mipmap.ic_play);

        //这个地方的id不能和小布局里面一样 不然会导致同一id的只有后设置的一个能响应
        remoteView.setOnClickPendingIntent(R.id.ntf_statue_big, getPendingBroadcastWithAction(context, ACTION_SAM_TOGGLE));
        remoteView.setOnClickPendingIntent(R.id.ntf_next_big, getPendingBroadcastWithAction(context, ACTION_SAM_NEXT));
        remoteView.setOnClickPendingIntent(R.id.ntf_pre_big, getPendingBroadcastWithAction(context, ACTION_SAM_PREVIOUS));
        remoteView.setOnClickPendingIntent(R.id.ntf_close_big, getPendingBroadcastWithAction(context, ACTION_SAM_STOP));
        remoteView.setOnClickPendingIntent(R.id.ntf_like, getPendingBroadcastWithAction(context, ACTION_MY_XIA_XIE_DE));
        return remoteView;
    }
}
