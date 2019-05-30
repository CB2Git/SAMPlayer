package com.samplayer.demo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.samplayer.SAMPlayer;
import com.samplayer.SMAManager;
import com.samplayer.demo.databinding.ActivityMainBinding;
import com.samplayer.demo.utils.TimeUtils;
import com.samplayer.listener.ServiceSessionListener;
import com.samplayer.listener.SimplePlayListener;
import com.samplayer.model.PlayMode;
import com.samplayer.model.SongInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding mBinding;

    private ServiceSessionListener mServiceSessionListener;

    private boolean mSeekBarInTouchState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init();

        mBinding.homeSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekBarInTouchState = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                SAMPlayer.getInstance().getPlayer().seekTo(progress * 1000);
                mSeekBarInTouchState = false;
            }
        });
    }

    private void init() {
        SMAManager.init(this);
        showInfo("初始化完成");
        mServiceSessionListener = new ServiceSessionListener() {
            @Override
            public void onServiceConnect() {
                showInfo("Service Connect");
            }

            @Override
            public void onServiceDisconnect(boolean isError) {
                showInfo("Service Disconnect,is Error =" + isError);
            }
        };
        SAMPlayer.getInstance().getServiceSession().addSessionListener(mServiceSessionListener);
        showInfo("添加Remote Service监听");
        SAMPlayer.getInstance().getPlayer().addPlayListener(mSimplePlayListener);
        showInfo("添加播放相关监听");
        showInfo("当前是否已经连接:" + SAMPlayer.getInstance().getServiceSession().isConnect());
    }

    private SimplePlayListener mSimplePlayListener = new SimplePlayListener() {
        @Override
        public void onPlayableStart(SongInfo songinfo) {
            super.onPlayableStart(songinfo);
            mBinding.homeSongTitle.setText(songinfo.getSongName());
            mBinding.homeSongProgress.setMax(0);
            mBinding.homeSongProgress.setProgress(0);
            mBinding.homeSongProgress.setSecondaryProgress(0);
            mBinding.homeSongDuring.setText("00:00/00:00");
        }

        @Override
        public void onBufferProgress(int percent) {
            super.onBufferProgress(percent);
            mBinding.homeSongProgress.setSecondaryProgress(mBinding.homeSongProgress.getMax() * percent / 100);
        }

        @Override
        public void onProgressChange(long second, long duration) {
            super.onProgressChange(second, duration);
            //当播放出错或者其他情况  这个可能为0
            if (duration <= 0) {
                return;
            }
            mBinding.homeSongProgress.setMax((int) duration);
            //当手指按在进度条上不自动更新进度
            if (!mSeekBarInTouchState) {
                mBinding.homeSongProgress.setProgress((int) (second));
            }

            mBinding.homeSongDuring.setText(
                    String.format("%s/%s", TimeUtils.formatSecond(second), TimeUtils.formatSecond(duration)));
        }

        @Override
        public void onError(int what, int extra) {
            super.onError(what, extra);
            Toast.makeText(MainActivity.this, "播放失败了哦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStop() {
            super.onStop();
            Toast.makeText(MainActivity.this, "播放停止~", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SAMPlayer.getInstance().getServiceSession().removeSessionListener(mServiceSessionListener);
        SAMPlayer.getInstance().getPlayer().removeListener(mSimplePlayListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_connect:
                SAMPlayer.getInstance().getServiceSession().connect();
                break;
            case R.id.home_disconnect:
                SAMPlayer.getInstance().getServiceSession().disconnect();
                break;
            case R.id.home_connect_state:
                Toast.makeText(this, "连接状态:" + SAMPlayer.getInstance().getServiceSession().isConnect(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.home_release:
                SAMPlayer.getInstance().release();
                break;
            case R.id.ctrl_play:
                SAMPlayer.getInstance().getPlayer().play();
                break;
            case R.id.ctrl_toggle:
                SAMPlayer.getInstance().getPlayer().toggle();
                break;
            case R.id.ctrl_stop:
                SAMPlayer.getInstance().getPlayer().stop();
                break;
            case R.id.ctrl_set_list:
                SAMPlayer.getInstance().getPlayer().setPlayList(generateSongInfo(), true);
                break;
            case R.id.ctrl_clear_list:
                SAMPlayer.getInstance().getPlayer().clearPlayList();
                break;
            case R.id.ctrl_set_list_without_play:
                SAMPlayer.getInstance().getPlayer().setPlayList(generateSongInfo(), false);
                break;
            case R.id.ctrl_next:
                SAMPlayer.getInstance().getPlayer().next();
                break;
            case R.id.ctrl_pro:
                SAMPlayer.getInstance().getPlayer().previous();
                break;
            case R.id.ctrl_play_mode:
                int playMode = SAMPlayer.getInstance().getPlayer().getPlayMode();
                //映射到 1,2,3上面
                playMode = (playMode + 1) % 3 + 1;
                SAMPlayer.getInstance().getPlayer().setPlayMode(playMode);
                String playModeString = "";
                switch (playMode) {
                    case PlayMode.ORDER:
                        playModeString = "顺序播放";
                        break;
                    case PlayMode.SINGLE:
                        playModeString = "单曲循环";
                        break;
                    case PlayMode.SHUFFLE:
                        playModeString = "随机播放";
                        break;
                    default:
                        playModeString = "未知模式，可能是没有连接服务";
                        break;
                }
                mBinding.ctrlPlayMode.setText(playModeString);
                Toast.makeText(this, "当前播放模式:" + playModeString, Toast.LENGTH_SHORT).show();
                break;
            case R.id.ctrl_append_list:
                SAMPlayer.getInstance().getPlayer().appendPlayList(generateSongInfo());
                break;
            case R.id.ctrl_show_play_list:
                PlayListActivity.start(MainActivity.this);
                break;
            case R.id.ctrl_songinfo:
                SongInfo currentPlayable = SAMPlayer.getInstance().getPlayer().getCurrentPlayable();
                if (currentPlayable == null) {
                    showInfo("当前播放为null");
                } else {
                    showInfo("当前播放：" + currentPlayable.getSongName());
                }
                break;
            case R.id.ctrl_duration:
                showInfo("时长:" + SAMPlayer.getInstance().getPlayer().getDuration());
                break;
            case R.id.ctrl_position:
                showInfo("进度:" + SAMPlayer.getInstance().getPlayer().getCurrentPosition());
                break;

        }
    }

    private List<SongInfo> generateSongInfo() {
        SongInfo info = new SongInfo();
        info.setSongId("23333");
        info.setArtist("草蜢");
        info.setSongName("一人一首成名曲 广东精选篇");
        info.setSongUrl("http://zhangmenshiting.qianqian.com/data2/music/6bf95c6103bbcb2a27b929426b36f5ac/599236414/1900592028800128.mp3?xcode=82eb246da25cf3dfb4504f1dc3711450");


        SongInfo info1 = new SongInfo();
        info1.setSongId("23333");
        info1.setArtist("潘悦晨");
        info1.setSongName("心如止水（正式版)");
        info1.setSongUrl("http://zhangmenshiting.qianqian.com/data2/music/8905e6db8fcb1d0c224ae29511d61b24/614044525/61404452132400128.mp3?xcode=b11c6ea9a6bd3e5de61345ed80ef9cd2");

        SongInfo info2 = new SongInfo();
        info2.setSongId("23333");
        info2.setArtist("刘惜君");
        info2.setSongName("不了了知");
        info2.setSongUrl("http://zhangmenshiting.qianqian.com/data2/music/bba972a0bd2389a71b17a3a70bcaca7d/614046065/614045208172800128.mp3?xcode=b11c6ea9a6bd3e5d0d9b8f0c677c76d2");


        List<SongInfo> mSongInfo = new ArrayList<>();
        mSongInfo.add(info);
        mSongInfo.add(info1);
        mSongInfo.add(info2);
        return mSongInfo;
    }

    private void showInfo(String msg) {
        mBinding.homeInfo.setText(String.format("%s\n%s", mBinding.homeInfo.getText(), msg));
    }

}
