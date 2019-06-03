package com.samplayer.demo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.samplayer.demo.R;
import com.samplayer.model.SongInfo;

import java.util.List;

public class PlayListAdapter extends BaseQuickAdapter<SongInfo, BaseViewHolder> {

    private SongInfo mSongInfo;

    public PlayListAdapter() {
        super(R.layout.item_play_list);
    }

    public void setCurrentPlay(SongInfo info) {
        mSongInfo = info;
        notifyDataSetChanged();
    }

    @Override
    public void setNewData(@Nullable List<SongInfo> data) {
        super.setNewData(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SongInfo item) {
        helper.setText(R.id.item_song_name, item.getSongName());

        helper.setGone(R.id.item_song_state, item.equals(mSongInfo));
        helper.addOnClickListener(R.id.item_del);
    }
}
