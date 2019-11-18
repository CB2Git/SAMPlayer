package com.samplayer.core.manager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;

import com.samplayer.SAMConfig;
import com.samplayer.core.remote.player.cmd.CmdHandlerHelper;
import com.samplayer.model.SongInfo;
import com.samplayer.utils.SAMLog;

/**
 * MediaSession管理类
 */
public class MediaSessionManager {

    private static final String TAG = "MediaSessionManager";

    //session对象
    private MediaSessionCompat mMediaSessionCompat;

    //playback对象
    private PlaybackStateCompat.Builder mPlaybackBuildCompat;

    public MediaSessionManager(Context context) {
        initMediaSession(context);
    }

    /**
     * 更新播放状态
     *
     * @param isPlaying
     */
    public void updatePlaybackState(boolean isPlaying) {

        if (mMediaSessionCompat == null) {
            SAMLog.w(TAG, "mMediaSessionCompat not init");
            return;
        }

        if (isPlaying) {
            mPlaybackBuildCompat.setState(PlaybackStateCompat.STATE_PLAYING,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    SystemClock.elapsedRealtime());
        } else {
            mPlaybackBuildCompat.setState(PlaybackStateCompat.STATE_PAUSED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    SystemClock.elapsedRealtime());
        }
        mMediaSessionCompat.setPlaybackState(mPlaybackBuildCompat.build());
    }

    /**
     * 获取Session的Token
     *
     * @return 如果没有初始化好那么会返回一个null
     */
    @Nullable
    public MediaSessionCompat.Token getSessionToken() {

        if (mMediaSessionCompat == null) {
            SAMLog.w(TAG, "mMediaSessionCompat not init");
            return null;
        }

        return mMediaSessionCompat.getSessionToken();
    }

    /**
     * 更新当前音乐播放信息
     *
     * @param songInfo
     */
    public void updateMediaCenterInfo(SongInfo songInfo) {

        if (mMediaSessionCompat == null) {
            SAMLog.w(TAG, "mMediaSessionCompat not init");
            return;
        }
        setActive(true);
        MediaMetadataCompat mediaMetadataCompat = toMediaMetadata(songInfo);
        mMediaSessionCompat.setMetadata(mediaMetadataCompat);
    }

    public void setActive(boolean active) {
        //必须设置为true，这样才能开始接收各种信息
        if (mMediaSessionCompat == null) {
            initMediaSession(SAMConfig.getAppContext());
        }
        mMediaSessionCompat.setActive(active);
    }

    public void release() {
        if (mMediaSessionCompat != null) {
            mMediaSessionCompat.setActive(false);
            mMediaSessionCompat.release();
            mMediaSessionCompat = null;
        }
    }

    private void initMediaSession(Context context) {
        mMediaSessionCompat = new MediaSessionCompat(context, TAG);

        //点击通知栏跳转
        Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        //https://juejin.im/post/5c36f666e51d45513236f081
        if (launchIntentForPackage != null) {
            launchIntentForPackage.setPackage(null);
        }
        PendingIntent clickIntent = PendingIntent.getActivity(context, 0, launchIntentForPackage, PendingIntent.FLAG_UPDATE_CURRENT);

        mMediaSessionCompat.setSessionActivity(clickIntent);
        //指明支持的按键信息类型
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        //监听的事件（播放,暂停,上一曲,下一曲,停止播放）
        mPlaybackBuildCompat = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE
                        | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        | PlaybackStateCompat.ACTION_STOP);
        mMediaSessionCompat.setPlaybackState(mPlaybackBuildCompat.build());


        mMediaSessionCompat.setCallback(getSessionCallback());

        setActive(true);
    }

    private MediaSessionCompat.Callback getSessionCallback() {
        return new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PLAY);
            }

            @Override
            public void onPause() {
                super.onPause();
                CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PAUSE);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_NEXT);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_PREVIOUS);
            }

            @Override
            public void onStop() {
                super.onStop();
                CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_STOP);
            }
        };
    }

    /**
     * SongInfo 转 MediaMetadataCompat
     */
    private synchronized static MediaMetadataCompat toMediaMetadata(SongInfo info) {
        String albumTitle = "";
        if (!TextUtils.isEmpty(info.getAlbumName())) {
            albumTitle = info.getAlbumName();
        } else if (!TextUtils.isEmpty(info.getSongName())) {
            albumTitle = info.getSongName();
        }
        String songCover = "";
        if (!TextUtils.isEmpty(info.getSongCover())) {
            songCover = info.getSongCover();
        } else if (!TextUtils.isEmpty(info.getAlbumCover())) {
            songCover = info.getAlbumCover();
        }
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, info.getSongId());
        builder.putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, info.getSongUrl());
        if (!TextUtils.isEmpty(albumTitle)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, albumTitle);
        }
        if (!TextUtils.isEmpty(info.getArtist())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, info.getArtist());
        }
        if (info.getDuration() != -1) {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, info.getDuration());
        }
        if (!TextUtils.isEmpty(info.getGenre())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_GENRE, info.getGenre());
        }
        if (!TextUtils.isEmpty(songCover)) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, songCover);
        }
        if (!TextUtils.isEmpty(info.getSongName())) {
            builder.putString(MediaMetadataCompat.METADATA_KEY_TITLE, info.getSongName());
        }
        if (info.getTrackNumber() != -1) {
            builder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, info.getTrackNumber());
        }
        builder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, info.getAlbumSongCount());
        return builder.build();
    }
}
