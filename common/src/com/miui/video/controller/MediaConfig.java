/**
 *  Copyright(c) 2014 XiaoMi TV Group
 *    
 *  MediaConfig.java
 *
 *  @author tianli(tianli@xiaomi.com)
 *
 *  2014-11-15
 */
package com.miui.video.controller;

import android.net.Uri;

/**
 * @author tianli
 *
 */
public class MediaConfig {

    public static final int LIST_TYPE_VERT_POSTER = 0;
    public static final int LIST_TYPE_HORI_POSTER = 1;
    
    public static final int MEDIA_TYPE_LONG = 0; // long video
    public static final int MEDIA_TYPE_SHORT = 1; //  short video
    
    public static final int PLAY_TYPE_HTML5 = 0; //  进入html5后拿到播放地址播放
    public static final int PLAY_TYPE_SDK = 1; //  使用sdk播放
    public static final int PLAY_TYPE_DIRECT = 2; //  直接用播放地址播放
    
    public static boolean isOfflineUri(Uri uri){
        String OFFLINEMEDIA_DIR = "MIUI/Video/files/";
        final String path = uri != null ? uri.getPath() : "";
        if(path != null && path.contains(OFFLINEMEDIA_DIR)){
            return true;
        }
        return false;
    }
    
}
