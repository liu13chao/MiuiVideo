/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   PlayEvent.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-8-4
 */

package com.miui.videoplayer.playservice;

import com.pplive.sdk.PlayStat;

/**
 * @author tianli
 *
 */
public class PlayEvent {

//	private String mSource;
//	private String mVid;

	PlayStat mPPtvStat = null;

	public PlayEvent(String source, String vid){
//		mSource = source;
//		mVid = vid;
//		if(Constants.SOURCE_NEW_PPTV.equals(source)){
//			mPPtvStat = new PlayStat();
//			mPPtvStat.onSetDataSource(vid);
//		}
	}

	public void onEvent(String key, String value){
//		if(Constants.KEY_BUFFERING_START.equals(key)){
//			onBufferStart();
//		}else if(Constants.KEY_BUFFERING_END.equals(key)){
//			onBufferEnd();
//		}else if(Constants.KEY_SEEK.equals(key)){
//			onSeek();
//		}else if(Constants.KEY_ONPREPARED.equals(key)){
//			onPrepared();
//		}else if(Constants.KEY_RESUME.equals(key)){
//			onResume();
//		}else if(Constants.KEY_PAUSE.equals(key)){
//			onPause();
//		}else if(Constants.KEY_PREPARE.equals(key)){
//			onPrepare();
//		}else if(Constants.KEY_LOADING_END.equals(key)){
//			onLoadingEnd();
//		}
	}

	public void onBufferStart(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//			mPPtvStat.onBufferingBegin();
//		}
	}
	
	public void onBufferEnd(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//			mPPtvStat.onBufferingEnd();
//		}
	}

	public void onPrepare(){
	}
	
	public void onLoadingEnd(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//			mPPtvStat.onPlayStart();
//		}
	}

	public void onPrepared(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//			mPPtvStat.onPrepared();
//		}
	}

	public void onResume(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//		}
	}

	public void onPause(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//		}
	}

	public void onSeek(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//			mPPtvStat.onSeekTo();
//		}
	}

	public void onRelease(){
//		if(Constants.SOURCE_NEW_PPTV.equals(mSource)){
//			mPPtvStat.onPlayStop();
//			mPPtvStat.onRelease();
//		}
	}
}
