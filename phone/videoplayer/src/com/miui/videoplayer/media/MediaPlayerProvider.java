/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MediaPlayerProvider.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-17
 */

package com.miui.videoplayer.media;

import com.miui.videoplayer.media.IMediaPlayer;

/**
 * @author tianli
 *
 */
public interface MediaPlayerProvider {
	public IMediaPlayer createPlayer();
}
