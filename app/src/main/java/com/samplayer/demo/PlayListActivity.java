package com.samplayer.demo;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.samplayer.SAMPlayer;
import com.samplayer.demo.databinding.ActivityPlayListBinding;
import com.samplayer.model.SongInfo;

import java.util.List;

public class PlayListActivity extends AppCompatActivity {

    private ActivityPlayListBinding mBinding;

    private ListAdapter mMyAdapter = new ListAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_list);


        mBinding.playList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.playList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.playList.setAdapter(mMyAdapter);

        if (!SAMPlayer.getInstance().getServiceSession().isConnect()) {
            Toast.makeText(this, "服务没有连接", Toast.LENGTH_SHORT).show();
        } else {
            List<SongInfo> playList = SAMPlayer.getInstance().getPlayer().getPlayList();
            mMyAdapter.setNewData(playList);
        }

        mMyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        mMyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });

    }

    private class ListAdapter extends BaseQuickAdapter<SongInfo, BaseViewHolder> {

        public ListAdapter() {
            super(R.layout.item_play_list);
        }

        @Override
        protected void convert(BaseViewHolder helper, SongInfo item) {
            helper.setText(R.id.item_song_name, item.getSongName());
            helper.addOnClickListener(R.id.item_del);
        }
    }

    public static void start(Context context) {
        Bundle args = new Bundle();
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.putExtras(args);
        context.startActivity(intent);
    }
}
