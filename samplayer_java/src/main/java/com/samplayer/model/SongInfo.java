package com.samplayer.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 面向用户的音频信息  from:https://github.com/EspoirX/StarrySky
 * <p>
 * note：由音乐id唯一确定是不是同一首歌 {@link SongInfo#equals(Object)}
 */
public class SongInfo implements Parcelable {
    private String songId = ""; //音乐id
    private String songName = ""; //音乐标题
    private String songCover = ""; //音乐封面
    private String songHDCover = ""; //专辑封面(高清)
    private String songSquareCover = ""; //专辑封面(正方形)
    private String songRectCover = ""; //专辑封面(矩形)
    private String songRoundCover = ""; //专辑封面(圆形)
    private String songNameKey = "";
    private Bitmap songCoverBitmap;
    private String songUrl = ""; //音乐播放地址
    private String songInterceptorUrl = ""; //进过拦截器修改以后的播放地址
    private String genre = ""; //类型（流派）
    private String type = ""; //类型
    private String size = "0"; //音乐大小
    private long duration = -1; //音乐长度
    private String artist = ""; //音乐艺术家
    private String artistKey = "";
    private String artistId = ""; //音乐艺术家id
    private String downloadUrl = ""; //音乐下载地址
    private String site = ""; //地点
    private int favorites = 0; //喜欢数
    private int playCount = 0; //播放数
    private int trackNumber = -1; //媒体的曲目号码（序号：1234567……）
    private String language = "";//语言
    private String country = ""; //地区
    private String proxyCompany = "";//代理公司
    private String publishTime = "";//发布时间
    private String year = ""; //录制音频文件的年份
    private String modifiedTime = ""; //最后修改时间
    private String description = ""; //音乐描述
    private String versions = ""; //版本
    private String mimeType = "";

    private String albumId = "";    //专辑id
    private String albumName = "";  //专辑名称
    private String albumNameKey = "";
    private String albumCover = ""; //专辑封面
    private String albumHDCover = ""; //专辑封面(高清)
    private String albumSquareCover = ""; //专辑封面(正方形)
    private String albumRectCover = ""; //专辑封面(矩形)
    private String albumRoundCover = ""; //专辑封面(圆形)
    private String albumArtist = "";     //专辑艺术家
    private int albumSongCount = 0;      //专辑音乐数
    private int albumPlayCount = 0;      //专辑播放数

    private String channelId = "";  //分类id
    private String channelName = "";    //分类名字
    private String channelCover = "";   //分类封面

    private Map<String, String> songExtra = new HashMap<>();    //如果上面的字段还不满足，这里可以额外附加数据

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelCover() {
        return channelCover;
    }

    public void setChannelCover(String channelCover) {
        this.channelCover = channelCover;
    }

    public Map<String, String> getSongExtra() {
        return songExtra;
    }

    public void setSongExtra(Map<String, String> songExtra) {
        this.songExtra = songExtra;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongCover() {
        return songCover;
    }

    public void setSongCover(String songCover) {
        this.songCover = songCover;
    }

    public String getSongHDCover() {
        return songHDCover;
    }

    public void setSongHDCover(String songHDCover) {
        this.songHDCover = songHDCover;
    }

    public String getSongSquareCover() {
        return songSquareCover;
    }

    public void setSongSquareCover(String songSquareCover) {
        this.songSquareCover = songSquareCover;
    }

    public String getSongRectCover() {
        return songRectCover;
    }

    public void setSongRectCover(String songRectCover) {
        this.songRectCover = songRectCover;
    }

    public String getSongRoundCover() {
        return songRoundCover;
    }

    public void setSongRoundCover(String songRoundCover) {
        this.songRoundCover = songRoundCover;
    }

    public String getSongNameKey() {
        return songNameKey;
    }

    public void setSongNameKey(String songNameKey) {
        this.songNameKey = songNameKey;
    }

    public Bitmap getSongCoverBitmap() {
        return songCoverBitmap;
    }

    public void setSongCoverBitmap(Bitmap songCoverBitmap) {
        this.songCoverBitmap = songCoverBitmap;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistKey() {
        return artistKey;
    }

    public void setArtistKey(String artistKey) {
        this.artistKey = artistKey;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getFavorites() {
        return favorites;
    }

    public void setFavorites(int favorites) {
        this.favorites = favorites;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProxyCompany() {
        return proxyCompany;
    }

    public void setProxyCompany(String proxyCompany) {
        this.proxyCompany = proxyCompany;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumNameKey() {
        return albumNameKey;
    }

    public void setAlbumNameKey(String albumNameKey) {
        this.albumNameKey = albumNameKey;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public String getAlbumHDCover() {
        return albumHDCover;
    }

    public void setAlbumHDCover(String albumHDCover) {
        this.albumHDCover = albumHDCover;
    }

    public String getAlbumSquareCover() {
        return albumSquareCover;
    }

    public void setAlbumSquareCover(String albumSquareCover) {
        this.albumSquareCover = albumSquareCover;
    }

    public String getAlbumRectCover() {
        return albumRectCover;
    }

    public void setAlbumRectCover(String albumRectCover) {
        this.albumRectCover = albumRectCover;
    }

    public String getAlbumRoundCover() {
        return albumRoundCover;
    }

    public void setAlbumRoundCover(String albumRoundCover) {
        this.albumRoundCover = albumRoundCover;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public int getAlbumSongCount() {
        return albumSongCount;
    }

    public void setAlbumSongCount(int albumSongCount) {
        this.albumSongCount = albumSongCount;
    }

    public int getAlbumPlayCount() {
        return albumPlayCount;
    }

    public void setAlbumPlayCount(int albumPlayCount) {
        this.albumPlayCount = albumPlayCount;
    }

    public String getSongInterceptorUrl() {
        return songInterceptorUrl;
    }

    public void setSongInterceptorUrl(String songInterceptorUrl) {
        this.songInterceptorUrl = songInterceptorUrl;
    }

    public SongInfo() {
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SongInfo songInfo = (SongInfo) obj;
        return Objects.equals(songId, songInfo.getSongId());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songId);
        dest.writeString(this.songName);
        dest.writeString(this.songCover);
        dest.writeString(this.songHDCover);
        dest.writeString(this.songSquareCover);
        dest.writeString(this.songRectCover);
        dest.writeString(this.songRoundCover);
        dest.writeString(this.songNameKey);
        dest.writeParcelable(this.songCoverBitmap, flags);
        dest.writeString(this.songUrl);
        dest.writeString(this.songInterceptorUrl);
        dest.writeString(this.genre);
        dest.writeString(this.type);
        dest.writeString(this.size);
        dest.writeLong(this.duration);
        dest.writeString(this.artist);
        dest.writeString(this.artistKey);
        dest.writeString(this.artistId);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.site);
        dest.writeInt(this.favorites);
        dest.writeInt(this.playCount);
        dest.writeInt(this.trackNumber);
        dest.writeString(this.language);
        dest.writeString(this.country);
        dest.writeString(this.proxyCompany);
        dest.writeString(this.publishTime);
        dest.writeString(this.year);
        dest.writeString(this.modifiedTime);
        dest.writeString(this.description);
        dest.writeString(this.versions);
        dest.writeString(this.mimeType);
        dest.writeString(this.albumId);
        dest.writeString(this.albumName);
        dest.writeString(this.albumNameKey);
        dest.writeString(this.albumCover);
        dest.writeString(this.albumHDCover);
        dest.writeString(this.albumSquareCover);
        dest.writeString(this.albumRectCover);
        dest.writeString(this.albumRoundCover);
        dest.writeString(this.albumArtist);
        dest.writeInt(this.albumSongCount);
        dest.writeInt(this.albumPlayCount);
        dest.writeString(this.channelId);
        dest.writeString(this.channelName);
        dest.writeString(this.channelCover);
        dest.writeInt(this.songExtra.size());
        for (Map.Entry<String, String> entry : this.songExtra.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public void readFromParcel(Parcel in) {
        this.songId = in.readString();
        this.songName = in.readString();
        this.songCover = in.readString();
        this.songHDCover = in.readString();
        this.songSquareCover = in.readString();
        this.songRectCover = in.readString();
        this.songRoundCover = in.readString();
        this.songNameKey = in.readString();
        this.songCoverBitmap = in.readParcelable(Bitmap.class.getClassLoader());
        this.songUrl = in.readString();
        this.songInterceptorUrl = in.readString();
        this.genre = in.readString();
        this.type = in.readString();
        this.size = in.readString();
        this.duration = in.readLong();
        this.artist = in.readString();
        this.artistKey = in.readString();
        this.artistId = in.readString();
        this.downloadUrl = in.readString();
        this.site = in.readString();
        this.favorites = in.readInt();
        this.playCount = in.readInt();
        this.trackNumber = in.readInt();
        this.language = in.readString();
        this.country = in.readString();
        this.proxyCompany = in.readString();
        this.publishTime = in.readString();
        this.year = in.readString();
        this.modifiedTime = in.readString();
        this.description = in.readString();
        this.versions = in.readString();
        this.mimeType = in.readString();
        this.albumId = in.readString();
        this.albumName = in.readString();
        this.albumNameKey = in.readString();
        this.albumCover = in.readString();
        this.albumHDCover = in.readString();
        this.albumSquareCover = in.readString();
        this.albumRectCover = in.readString();
        this.albumRoundCover = in.readString();
        this.albumArtist = in.readString();
        this.albumSongCount = in.readInt();
        this.albumPlayCount = in.readInt();
        this.channelId = in.readString();
        this.channelName = in.readString();
        this.channelCover = in.readString();
        int songExtraSize = in.readInt();
        this.songExtra = new HashMap<String, String>(songExtraSize);
        for (int i = 0; i < songExtraSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.songExtra.put(key, value);
        }
    }

    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel source) {
            SongInfo songInfo = new SongInfo();
            songInfo.readFromParcel(source);
            return songInfo;
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };
}

