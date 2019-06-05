package com.samplayer.demo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.samplayer.SAMPlayer;
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
        //自动连接一次
        SAMPlayer.getInstance(true).getServiceSession().addSessionListener(mServiceSessionListener);
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
        public void inInterceptorProcess(SongInfo info) {
            super.inInterceptorProcess(info);
            //这么做的原因看回调方法上面注释
            onPlayableStart(info);
        }

        @Override
        public void onBufferProgress(int percent) {
            super.onBufferProgress(percent);
            mBinding.homeSongProgress.setSecondaryProgress(mBinding.homeSongProgress.getMax() * percent / 100);
        }

        @Override
        public void onProgressChange(SongInfo info, long second, long duration) {
            super.onProgressChange(info, second, duration);
            //当播放出错或者其他情况  这个可能为0
            if (duration <= 0) {
                return;
            }
            if (info != null) {
                mBinding.homeSongTitle.setText(info.getSongName());
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
            mBinding.homeSongTitle.setText("播放停止了");
            mBinding.homeSongProgress.setMax(0);
            mBinding.homeSongProgress.setProgress(0);
            mBinding.homeSongProgress.setSecondaryProgress(0);
            mBinding.homeSongDuring.setText("00:00/00:00");
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
                SAMPlayer.getInstance().getPlayer().appendPlayList(generateSongInfo2());
                break;
            case R.id.ctrl_show_play_list:
                PlayListActivity.start(MainActivity.this);
                break;
            case R.id.ctrl_scan_local:
                scanLocalMusicInfo();
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

    private void scanLocalMusicInfo() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请给与读取权限", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 10);
            return;
        }

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        BaseColumns._ID,
                        MediaStore.Audio.AudioColumns.IS_MUSIC,
                        MediaStore.Audio.AudioColumns.TITLE,
                        MediaStore.Audio.AudioColumns.ARTIST,
                        MediaStore.Audio.AudioColumns.ALBUM,
                        MediaStore.Audio.AudioColumns.ALBUM_ID,
                        MediaStore.Audio.AudioColumns.DATA,
                        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                        MediaStore.Audio.AudioColumns.SIZE,
                        MediaStore.Audio.AudioColumns.DURATION},
                null,
                null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) {
            return;
        }
        List<SongInfo> mSongs = new ArrayList<>();
        while (cursor.moveToNext()) {
            // 是否为音乐，魅族手机上始终为0
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.IS_MUSIC));
            long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            //12857694 b;
            //321414 ms;
            SongInfo songInfo = new SongInfo();
            songInfo.setSongId(String.valueOf(id));
            songInfo.setSongName(title);
            songInfo.setArtist(artist);
            songInfo.setSongUrl(path);
            mSongs.add(songInfo);
        }
        cursor.close();

        showInfo("扫描结果:size = " + mSongs.size());
        SAMPlayer.getInstance().getPlayer().appendPlayList(mSongs);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            //用户授予了权限
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "授予权限成功", Toast.LENGTH_SHORT).show();
            } else {
                //权限被用户拒绝了，但是并没有选择不再提示，也就是说还可以继续申请
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                    Toast.makeText(this, "请授予权限", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "没有权限", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private List<SongInfo> generateSongInfo() {
        SongInfo info = new SongInfo();
        info.setSongId("1");
        info.setArtist("周杰伦");
        info.setSongName("稻香");
        info.setSongUrl("http://zhangmenshiting.qianqian.com/data2/music/1042bded3e4540ca66d31a7336f8205b/599474129/13908401559631661128.mp3?xcode=7b661633f80d8b461e0a87f01dbb9fb5");


        SongInfo info1 = new SongInfo();
        info1.setSongId("2");
        info1.setArtist("周杰伦");
        info1.setSongName("超人不会飞");
        info1.setSongUrl("http://zhangmenshiting.qianqian.com/data2/music/234c66c6c782f0156c085bd2076900fc/599642326/2173861559638861128.mp3?xcode=7b661633f80d8b4629676627b54cb918");

        SongInfo info2 = new SongInfo();
        info2.setSongId("3");
        info2.setArtist("周杰伦");
        info2.setSongName("蒲公英的约定");
        info2.setSongUrl("http://zhangmenshiting.qianqian.com/data2/music/9b252e201b1519c2c2b69b58e1c56559/599641483/219957248400128.mp3?xcode=b3ca30af58a1ec10a0d51cfc739c1004");


        List<SongInfo> mSongInfo = new ArrayList<>();
        mSongInfo.add(info);
        mSongInfo.add(info1);
        mSongInfo.add(info2);
        return mSongInfo;
    }

    private List<SongInfo> generateSongInfo2() {
        SongInfo info = new SongInfo();
        info.setSongId("11");
        info.setArtist("无法播放1");
        info.setSongName("无法播放1");
        info.setSongUrl("http://zhangmenshiting.qianqian.com/data2/music/cb58e961691ecc1aa6fe49247dccb707/599624578/274085190800128.mp3");


        SongInfo info1 = new SongInfo();
        info1.setSongId("22");
        info1.setArtist("无法播放2");
        info1.setSongName("无法播放2");
        info1.setSongUrl("http:www.acbd.com/232131");

        List<SongInfo> mSongInfo = new ArrayList<>();
        mSongInfo.add(info);
        mSongInfo.add(info1);
        return mSongInfo;
    }

    private void showInfo(String msg) {
        mBinding.homeInfo.setText(String.format("%s\n%s", mBinding.homeInfo.getText(), msg));
    }

}
