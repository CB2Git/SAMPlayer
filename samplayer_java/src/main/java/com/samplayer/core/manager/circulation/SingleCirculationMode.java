package com.samplayer.core.manager.circulation;

import com.samplayer.core.manager.base.ICirculationMode;
import com.samplayer.model.PlayMode;

/**
 * 单曲循环
 */
public class SingleCirculationMode implements ICirculationMode {
    @Override
    public int next(int currentIndex, int size) {
        //没有播放过但是有数据  从第一个开始放
        if (currentIndex == ICirculationMode.UN_POSITION && size > 0) {
            return 0;
        }

        //越界也处理
        if (currentIndex >= size) {
            return 0;
        }
        return currentIndex;
    }

    @Override
    public int previous(int currentIndex, int size) {
        return next(currentIndex, size);
    }

    @Override
    public int getID() {
        return PlayMode.SINGLE;
    }
}
