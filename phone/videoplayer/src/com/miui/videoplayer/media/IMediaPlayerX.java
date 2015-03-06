/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   IIMediaPlayerX.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-18
 */

package com.miui.videoplayer.media;

import com.duokan.MediaPlayer.MediaInfo;

/**
 * @author tianli
 *
 */
public interface IMediaPlayerX{

    public MediaInfo getMediaInfo();
    public boolean get3dMode();
    public void set3dMode(boolean mode);
}
