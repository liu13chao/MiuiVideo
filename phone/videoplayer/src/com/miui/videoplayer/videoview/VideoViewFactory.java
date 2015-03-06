/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   VideoViewFactory.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-30
 */

package com.miui.videoplayer.videoview;


import android.app.Activity;

/**
 * @author tianli
 *
 */
public abstract class VideoViewFactory {
	public abstract IVideoView create(Activity context);
}
