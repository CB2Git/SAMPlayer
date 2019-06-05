package com.samplayer.interceptor;

import android.support.annotation.NonNull;

import com.samplayer.interceptor.ex.InterceptorException;
import com.samplayer.model.SongInfo;

import java.util.List;

/**
 * 责任链
 */
public class RealInterceptorChain implements Interceptor.Chain {

    private List<Interceptor> mInterceptors;

    private int mIndex;

    private SongInfo mSongInfo;

    public RealInterceptorChain(List<Interceptor> interceptors, int index, SongInfo songInfo) {
        mInterceptors = interceptors;
        mIndex = index;
        mSongInfo = songInfo;
    }

    @Override
    public SongInfo before() {
        return mSongInfo;
    }

    @Override
    public void error(int errorCode) throws Exception {
        throw new InterceptorException(errorCode);
    }

    @Override
    public SongInfo proceed(@NonNull SongInfo request) throws Exception {
        if (mIndex >= mInterceptors.size()) {
            return mSongInfo;
        }
        Interceptor interceptor = mInterceptors.get(mIndex);
        RealInterceptorChain next = new RealInterceptorChain(mInterceptors, mIndex + 1, mSongInfo);
        return interceptor.intercept(next);
    }
}
