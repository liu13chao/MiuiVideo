package com.miui.videoplayer;

import miui.os.Build;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.miui.videoplayer.common.AndroidUtils;
import com.miui.videoplayer.fragment.VideoFragment;
import com.miui.videoplayer.videoview.IVideoView;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = PhoneStateBroadcastReceiver.class.getSimpleName();
	
	private boolean  mRinging = false;
	private long mRingingTime = 0;
	
	private VideoFragment mController;

	private int mPlayStatePosition;
//	private int mDuration;
//	private IVideoView mVideoView;
	
	private boolean mReplayDone = false;
	private boolean mChangeConnection = false;
	
	public PhoneStateBroadcastReceiver(IVideoView videoview){
//		mVideoView = videoview;
	}
	
	public PhoneStateBroadcastReceiver(VideoFragment controller){
		mController = controller;
	}
	
	public boolean handleOnCompletion(){
    	return handleCmccCase();
	}
	
    public boolean handleOnError(){
    	return handleCmccCase();
	}
    
    private boolean handleCmccCase(){
    	if(needReplay()){
    		if(!mReplayDone){
    			mController.retryplay(mPlayStatePosition);
    		}
    		mReplayDone = true;
        	mChangeConnection = false;
			return true;
		}
		return false;
    }
    
    public boolean needReplay(){
    	IVideoView videoView = mController.getVideoView();
    	if(videoView != null){
			long pos = videoView.getCurrentPosition();
			long duration = videoView.getDuration();
			Log.d(TAG, "needReplay :  currentPosition = " + pos);
			Log.d(TAG, "needReplay :  duration = " + duration);
			if(duration > 0){
				if(Math.abs(duration - pos) < 2000){
		    		return false;
		    	}
			}
		}
    	long time = System.currentTimeMillis();
    	if(Math.abs(mRingingTime - time) < 10000){
    		return true;
    	}
    	return mChangeConnection;
    }
    
    public boolean isCmcc(){
    	try{
    		return Build.IS_CM_CUSTOMIZATION;
    	}catch(Throwable t){
    	}
    	return false;
    }
    
    public void onEnterForeground(){
    	mChangeConnection = isNetworkOffCausedByRinging() ;
    	if (mRinging) {
    	       mRingingTime = System.currentTimeMillis();
    	}
    }
    
    public boolean isNetworkOffCausedByRinging(){
    	return mRinging && isCmcc() && !isNetworkConnected();
    }
    
    private boolean isNetworkConnected(){
    	Activity activity = mController.getActivity();
    	if(activity != null){
        	return AndroidUtils.isNetworkConncected(activity);
    	}
    	return true;
    }
    
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(context == null || intent == null){
			return;
		}
		Log.i(TAG, "PhoneStateBroadcastReceiver intent action : " + intent.getAction());
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(new PhoneStateListener(){
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				super.onCallStateChanged(state, incomingNumber);
				IVideoView videoView = mController.getVideoView();
				if(videoView == null){
					return;
				}
				Uri uri = videoView.getUri();
				if(uri == null || uri.getScheme() == null
						|| !uri.getScheme().equals("rtsp")){
					return;
				}
				switch (state) {
					case TelephonyManager.CALL_STATE_IDLE :
						Log.i(TAG, "TelephonyManager CALL_STATE_IDLE, ring: " + mRinging);
						if (mRinging) {
							mChangeConnection = !isNetworkConnected() && isCmcc();
							mRinging = false;
							mRingingTime = System.currentTimeMillis();
							if(videoView != null){
								videoView.start();
							}
						}
						break;
					case TelephonyManager.CALL_STATE_OFFHOOK :
						Log.i(TAG, "TelephonyManager CALL_STATE_OFFHOOK");
						break;
					case TelephonyManager.CALL_STATE_RINGING :
						Log.i(TAG, "TelephonyManager CALL_STATE_RINGING");
						if (!mRinging) {
							if(videoView != null){
								videoView.pause();
								mPlayStatePosition = videoView.getCurrentPosition();
								mReplayDone = false;
							}
							mRingingTime = System.currentTimeMillis();
							mRinging = true;
						}
						break;
				}
			}

		}, PhoneStateListener.LISTEN_CALL_STATE);
	}
}
