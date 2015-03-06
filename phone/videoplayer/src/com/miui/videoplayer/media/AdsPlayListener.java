/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AdPlayListener.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-6-29
 */

package com.miui.videoplayer.media;

/**
 * @author tianli
 *
 */
public interface AdsPlayListener {
	public void onAdsPlayStart();
	public void onAdsPlayEnd();
	public void onAdsDuration(int duration);
    public void onAdsTimeUpdate(int leftSeconds);
}
