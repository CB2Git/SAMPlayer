package com.samplayer.core.manager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.session.MediaSessionCompat;

import com.samplayer.R;

public class MediaStyleNotificationManager {

    private static final String CHANNEL_ID = "MediaStyleChannel";
    private static final String CHANNEL_NAME = "MediaStyle";
    private static final String CHANNEL_DESCRIPTION = "this is quickly Notification";

    private static final int NOTIFICATION_ID = 9526;

    private NotificationCompat.Builder mBuilder;

    public MediaStyleNotificationManager(Context context, MediaSessionCompat.Token token) {

        buildNotificationChannel(context);
        mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
        mBuilder.setStyle(
                new android.support.v4.media.app.NotificationCompat.MediaStyle()
                        // show only play/pause in compact view
                        //.setShowActionsInCompactView(playPauseButtonPosition)
                        .setShowCancelButton(true)
                        //.setCancelButtonIntent(mStopIntent)
                        .setMediaSession(token))
                //.setDeleteIntent(mStopIntent)
                //.setColor(mNotificationColor)
                .setColorized(true)
                .setSmallIcon(R.drawable.ic_sam_notifacation_default)
                //.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setContentTitle("当前正在播放") //歌名
                .setContentText("歌曲信息");
    }


    public void notifyNotification(Context context) {
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void notifyNotification(Context context, String title, String content) {
        mBuilder.setContentTitle(title) //歌名
                .setContentText(content);
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, mBuilder.build());
    }

    public void cancelNotification(Context context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
    }

    private void buildNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(CHANNEL_DESCRIPTION);//设置渠道描述

            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
