package com.samplayer.core.manager.circulation;

import com.samplayer.core.manager.base.ICirculationMode;
import com.samplayer.model.PlayMode;

/**
 * 顺序播放
 */
public class OrderCirculationMode implements ICirculationMode {
    @Override
    public int next(int currentIndex, int size) {
        //没有播放过但是有数据  从第一个开始放
        if (currentIndex == ICirculationMode.UN_POSITION) {
            if (size > 0) {
                return 0;
            }
            return currentIndex;
        } else {
            return (currentIndex + 1) % size;
        }
    }

    @Override
    public int previous(int currentIndex, int size) {
        //没有播放过但是有数据  从第一个开始放
        if (currentIndex == ICirculationMode.UN_POSITION) {
            if (size > 0) {
                return 0;
            }
            return currentIndex;
        } else {
            return (currentIndex - 1 + size) % size;
        }
    }

    @Override
    public int getID() {
        return PlayMode.ORDER;
    }
}
