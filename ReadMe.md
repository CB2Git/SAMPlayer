####  SAMPlayer

> Simple Android Music Player:一个简单易用的音乐播放封装库，详细的文档以及代码注释，人性化的回调接口，支持多个业务场景，基于ijk实现



集成:

```
allprojects {
    repositories {
    	maven { url 'https://jitpack.io' }
    }
}
```

最新版本号:[![](https://jitpack.io/v/CB2Git/SAMPlayer.svg)](https://jitpack.io/#CB2Git/SAMPlayer)

IJK版本号:0.0.8

```
//必须
implementation "com.github.CB2Git.SAMPlayer:samplayer_java:XXX"
implementation "tv.danmaku.ijk.media:ijkplayer-java:XXX"


//可选 缓存相关
implementation "com.github.CB2Git.SAMPlayer:samplayer_cache:XXX"
implementation "com.danikula:videocache:XXX"


//可选 exo播放器
implementation "com.github.CB2Git.SAMPlayer:samplayer_exo:XXX"
implementation "tv.danmaku.ijk.media:ijkplayer-exo:XXX"


//可选 ijk播放器
implementation "com.github.CB2Git.SAMPlayer:samplayer_ijk_player:XXX"
//任选一种so
implementation "tv.danmaku.ijk.media:ijkplayer-armv7a:XXX"
implementation "tv.danmaku.ijk.media:ijkplayer-armv5:XXX"
implementation "tv.danmaku.ijk.media:ijkplayer-x86:XXX"
//implementation "tv.danmaku.ijk.media:ijkplayer-arm64:XXX"
//implementation "tv.danmaku.ijk.media:ijkplayer-x86_64:XXX"
```



之所以需要手动集成ijk的原因是jitpack生成pom文件中没有携带依赖信息，如果有人知道如何解决可以提交pr



使用文档

+ [基础功能](https://github.com/CB2Git/SAMPlayer/blob/master/md/基础功能.md)
+ [播放拦截器](https://github.com/CB2Git/SAMPlayer/blob/master/md/播放拦截器.md)
+ [一句话添加缓存功能](https://github.com/CB2Git/SAMPlayer/blob/master/md/添加缓存.md)
+ [通知栏](https://github.com/CB2Git/SAMPlayer/blob/master/md/通知栏.md)
+ [定时停止播放](https://github.com/CB2Git/SAMPlayer/blob/master/md/定时停止播放.md)
+ [播放历史以及流水上报](https://github.com/CB2Git/SAMPlayer/blob/master/md/播放历史以及流水上报.md)

