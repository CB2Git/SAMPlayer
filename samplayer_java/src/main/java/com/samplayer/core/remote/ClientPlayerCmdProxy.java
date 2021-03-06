package com.samplayer.core.remote;

import android.os.RemoteException;

import com.samplayer.aidl.ISAMPlayerCallBack;
import com.samplayer.aidl.ISAMPlayerService;
import com.samplayer.core.remote.player.cmd.CmdHandlerHelper;
import com.samplayer.model.PlayMode;
import com.samplayer.model.SongInfo;
import com.samplayer.outconfig.TimerConfig;

import java.util.Collections;
import java.util.List;


public class ClientPlayerCmdProxy extends ISAMPlayerService.Stub {

    private static final String TAG = "InternalPlayer";

    private SAMPlayerService mSAMPlayerService;

    public ClientPlayerCmdProxy(SAMPlayerService service) {
        mSAMPlayerService = service;
    }

    @Override
    public void setPlayList(List<SongInfo> songInfos, boolean autoPlay) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.setPlayList(songInfos, autoPlay);
        }
    }

    @Override
    public void appendPlayList(List<SongInfo> songInfos) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.appendPlayList(songInfos);
        }
    }

    @Override
    public void insertPlayList(int position, List<SongInfo> songInfos) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.insertPlayList(position, songInfos);
        }
    }

    @Override
    public List<SongInfo> getPlayList() throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.getPlayList();
        }
        return Collections.emptyList();
    }

    @Override
    public void clearPlayList() throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.clearPlayList();
        }
    }

    @Override
    public boolean removeAt(int index) throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.removeAt(index);
        }
        return false;
    }

    @Override
    public boolean removeItem(SongInfo songInfo) throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.removeItem(songInfo);
        }
        return false;
    }

    @Override
    public void play() throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.play();
        }
    }

    @Override
    public void pause() throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.pause();
        }
    }

    @Override
    public void playItem(SongInfo songInfo) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.playItem(songInfo);
        }
    }

    @Override
    public void playStartAt(SongInfo songInfo, long ms) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.playStartAt(songInfo, ms);
        }
    }

    @Override
    public void setPlayMode(int playMode) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.setPlayMode(playMode);
        }
    }

    @Override
    public int getPlayMode() throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.getPlayMode();
        }
        return PlayMode.UN_KNOW;
    }

    @Override
    public boolean isPlaying() throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.isPlaying();
        }
        return false;
    }

    @Override
    public void toggle() throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.toggle();
        }
    }

    @Override
    public void next() throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.next();
        }
    }

    @Override
    public void previous() throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.previous();
        }
    }

    @Override
    public void skipTo(int index) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.skipTo(index);
        }
    }

    @Override
    public void stop() throws RemoteException {
        if (mSAMPlayerService != null) {
            //mSAMPlayerService.stop();
            //exo播放器如果不在主线程回调就会继续调用onComplete导致无法停止播放
            CmdHandlerHelper.sendCmd(CmdHandlerHelper.CMD_STOP);
        }
    }

    @Override
    public void seekTo(long ms) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.seekTo(ms);
        }
    }

    @Override
    public long getCurrentPosition() throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.getDuration();
        }
        return 0;
    }

    @Override
    public SongInfo getCurrentPlayable() throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.getCurrentPlayable();
        }
        return null;
    }

    @Override
    public void setPlayCallback(ISAMPlayerCallBack callback) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.setPlayCallback(callback);
        }
    }

    @Override
    public void timer(TimerConfig timerConfig) throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.timer(timerConfig);
        }
    }

    @Override
    public TimerConfig getTimerConfig() throws RemoteException {
        if (mSAMPlayerService != null) {
            return mSAMPlayerService.getTimerConfig();
        }
        return null;
    }

    @Override
    public void cancelTimer() throws RemoteException {
        if (mSAMPlayerService != null) {
            mSAMPlayerService.cancelTimer();
        }
    }
}
