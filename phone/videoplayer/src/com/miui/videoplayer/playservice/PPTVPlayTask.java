/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PPTVPlayTask.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-9-2
 */

package com.miui.videoplayer.playservice;

import android.text.TextUtils;

import com.pplive.sdk.OttSDK;
import com.pplive.sdk.PlayStat;

/**
 * @author tianli
 *
 */
public class PPTVPlayTask extends PlayTask {
	
	PlayStat mStat = null;

	@Override
	public String getPlayUrl() {
//		return OttSDK.getM3u8PlayUrl("pptv://code=YfIHSeIPHLbCSgRoG5Bmdqo5CiYTHgnR&key=10");
		return OttSDK.getM3u8PlayUrl(mSdkInfo);
	}

	@Override
	public void closeTask() {
	    if(!TextUtils.isEmpty(mSdkInfo)){
	        OttSDK.closePlay(mSdkInfo);
            mSdkInfo = null;
	    }
	}

	@Override
	protected void onPause() {
	}

	@Override
	protected void onSeek() {
	}

	@Override
	protected void onBufferStart() {
	}

	@Override
	protected void onBufferEnd() {
	}

	@Override
	protected void onPrepare() {
	}

	@Override
	protected void onLoadingEnd() {
	}

	@Override
	protected void onPrepared() {
	}

	@Override
	protected void onResume() {
	}
}
