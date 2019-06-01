package com.samplayer.core.manager.circulation;

import com.samplayer.core.manager.base.ICirculationMode;
import com.samplayer.model.PlayMode;

import java.util.Random;

/**
 * 随机播放
 */
public class ShuffleCirculationMode implements ICirculationMode {

    private Random mRandom;

    public ShuffleCirculationMode() {
        mRandom = new Random();
    }

    @Override
    public int next(int currentIndex, int size) {
        //没有播放过
        if (currentIndex == ICirculationMode.UN_POSITION) {
            //但是有数据
            if (size > 0) {
                return mRandom.nextInt(size);
            }
            //没数据直接返回
            return currentIndex;
        } else {
            //只有一个
            if (size == 1) {
                return currentIndex;
            }
            int nextInt;
            do {
                nextInt = mRandom.nextInt(size);
            } while (nextInt == currentIndex);
            return nextInt;
        }
    }

    @Override
    public int previous(int currentIndex, int size) {
        return next(currentIndex, size);
    }


    @Override
    public int getID() {
        return PlayMode.SHUFFLE;
    }
}
