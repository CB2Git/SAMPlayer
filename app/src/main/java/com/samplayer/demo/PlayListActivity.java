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
import com.samplayer.SAMPlayer;
import com.samplayer.demo.adapter.PlayListAdapter;
import com.samplayer.demo.databinding.ActivityPlayListBinding;
import com.samplayer.listener.SimplePlayListener;
import com.samplayer.model.SongInfo;

import java.util.List;

public class PlayListActivity extends AppCompatActivity {

    private ActivityPlayListBinding mBinding;

    private PlayListAdapter mMyAdapter = new PlayListAdapter();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_list);


        mBinding.playList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.playList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mBinding.playList.setAdapter(mMyAdapter);

        SAMPlayer.getInstance().getPlayer().addPlayListener(mSimplePlayListener);

        //点击item播放
        mMyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SongInfo info = (SongInfo) adapter.getItem(position);
                SAMPlayer.getInstance().getPlayer().skipTo(position);
                //这个也可以
                //SAMPlayer.getInstance().getPlayer().play(info);
            }
        });

        //删除
        mMyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.item_del) {
                    SongInfo info = (SongInfo) adapter.getItem(position);
                    //这个也可以
                    //boolean success = SAMPlayer.getInstance().getPlayer().removeItem(info);
                    boolean success = SAMPlayer.getInstance().getPlayer().removeAt(position);
                    if (success) {
                        refreshPlayList();
                    } else {
                        Toast.makeText(PlayListActivity.this, "貌似删除失败了", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        refreshPlayList();

        //进来的时候刷新播放状态
        SongInfo playable = SAMPlayer.getInstance().getPlayer().getCurrentPlayable();
        mMyAdapter.setCurrentPlay(playable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SAMPlayer.getInstance().getPlayer().removeListener(mSimplePlayListener);
    }

    private SimplePlayListener mSimplePlayListener = new SimplePlayListener() {
        @Override
        public void onPlayableStart(SongInfo songinfo) {
            super.onPlayableStart(songinfo);
            mMyAdapter.setCurrentPlay(songinfo);
        }
    };

    private void refreshPlayList() {
        if (!SAMPlayer.getInstance().getServiceSession().isConnect()) {
            Toast.makeText(this, "服务没有连接", Toast.LENGTH_SHORT).show();
        } else {
            List<SongInfo> playList = SAMPlayer.getInstance().getPlayer().getPlayList();
            mMyAdapter.setNewData(playList);
        }
    }

    public static void start(Context context) {
        Bundle args = new Bundle();
        Intent intent = new Intent(context, PlayListActivity.class);
        intent.putExtras(args);
        context.startActivity(intent);
    }
}
