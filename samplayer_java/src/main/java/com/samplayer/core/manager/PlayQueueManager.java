package com.samplayer.core.manager;

import android.support.annotation.Nullable;

import com.samplayer.core.manager.base.ICirculationMode;
import com.samplayer.core.manager.base.IPlayQueue;
import com.samplayer.core.manager.circulation.OrderCirculationMode;
import com.samplayer.model.SongInfo;
import com.samplayer.utils.SAMLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 播放队列管理
 */
public class PlayQueueManager implements IPlayQueue {

    private static final String TAG = "PlayQueueManager";

    private ICirculationMode mICirculationMode;

    private List<SongInfo> mSongInfos = new ArrayList<>();

    private int mIndex = ICirculationMode.UN_POSITION;

    public PlayQueueManager() {
        mICirculationMode = new OrderCirculationMode();
    }

    @Override
    public void setCirculationMode(ICirculationMode circulationMode) {
        mICirculationMode = circulationMode;
    }

    @Override
    public ICirculationMode getCirculationMode() {
        return mICirculationMode;
    }

    @Override
    public void setPlayList(List<SongInfo> songInfos) {
        mSongInfos.clear();
        mSongInfos.addAll(songInfos);
        mIndex = ICirculationMode.UN_POSITION;
    }


    @Override
    public void appendPlayList(List<SongInfo> songInfos) {
        if (songInfos == null || songInfos.size() <= 0) {
            return;
        }
        mSongInfos.addAll(songInfos);
    }

    @Override
    public void insertPlayList(int position, List<SongInfo> songInfos) {
        if (songInfos == null || songInfos.size() <= 0) {
            return;
        }
        mSongInfos.addAll(position, songInfos);
        //如果添加的播放列表在正在播放的序列前面，那么需要更改序列
        if (position <= mIndex) {
            mIndex += songInfos.size();
        }
    }

    @Override
    public void clearPlayList() {
        mSongInfos.clear();
        mIndex = ICirculationMode.UN_POSITION;
    }

    @Override
    public List<SongInfo> getPlayList() {
        return mSongInfos;
    }

    @Nullable
    @Override
    public SongInfo removeAt(int index) {
        //越界直接警告
        if (index < 0 || index >= mSongInfos.size()) {
            SAMLog.w(TAG, "mIndex out of bound");
            return null;
        }
        return removeItem(mSongInfos.get(index));
    }

    @Nullable
    @Override
    public SongInfo removeItem(SongInfo songInfo) {
        int indexOf = mSongInfos.indexOf(songInfo);
        if (indexOf == -1) {
            SAMLog.w(TAG, "removeItem: this list contained the specified element");
            return null;
        }
        //删除列表元素
        SongInfo remove = mSongInfos.remove(indexOf);

        //如果删除的是正在播放的
        if (indexOf == mIndex) {
            //列表被删除空了
            if (mSongInfos.size() == 0) {
                mIndex = ICirculationMode.UN_POSITION;
            }
            //如果不为空 直接取下一个
            else {
                mIndex = mICirculationMode.next(mIndex, mSongInfos.size());
            }
        }
        //如果在当前播放的前面，直接前移就行了
        else if (indexOf < mIndex) {
            mIndex--;
        }
        return remove;
    }

    @Nullable
    @Override
    public SongInfo next() {
        mIndex = mICirculationMode.next(mIndex, mSongInfos.size());
        if (mIndex >= 0 && mIndex < mSongInfos.size()) {
            return mSongInfos.get(mIndex);
        }
        return null;
    }

    @Nullable
    @Override
    public SongInfo skipTo(int index) {
        if (index < 0 || index >= mSongInfos.size()) {
            return null;
        }
        mIndex = index;
        return mSongInfos.get(index);
    }

    @Override
    public int skipToItem(SongInfo songInfo) {
        int indexOf = mSongInfos.indexOf(songInfo);
        //已经在里面了
        if (indexOf != -1) {
            mIndex = indexOf;
        } else {
            //
            if (mIndex == ICirculationMode.UN_POSITION) {
                mSongInfos.add(0, songInfo);
                mIndex = 0;
            } else {
                mSongInfos.add(mIndex + 1, songInfo);
                mIndex = mIndex + 1;
            }
        }
        return mIndex;
    }

    @Nullable
    @Override
    public SongInfo previous() {
        mIndex = mICirculationMode.previous(mIndex, mSongInfos.size());
        if (mIndex >= 0 && mIndex < mSongInfos.size()) {
            return mSongInfos.get(mIndex);
        }
        return null;
    }

    @Override
    public SongInfo getCurrent() {
        if (mIndex >= 0 && mIndex < mSongInfos.size()) {
            return mSongInfos.get(mIndex);
        } else {
            if (mIndex == ICirculationMode.UN_POSITION) {
                return next();
            }
            SAMLog.efmt(TAG, "mIndex越界了:%d:%d", mIndex, mSongInfos.size());
        }
        return null;
    }
}
