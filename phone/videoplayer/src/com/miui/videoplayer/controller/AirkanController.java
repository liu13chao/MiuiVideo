/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   AirkanController.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-13
 */

package com.miui.videoplayer.controller;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.videoplayer.fragment.MilinkFragment;
import com.miui.videoplayer.framework.airkan.AirkanManager;
import com.miui.videoplayer.framework.airkan.AirkanManager.AirkanChangedEvent;
import com.miui.videoplayer.framework.airkan.AirkanManager.OnStatusChangedListener;
import com.miui.videoplayer.widget.MilinkView;

/**
 * @author tianli
 *
 */
public class AirkanController {
	
	public static final String TAG = "AirkanController";
	
	public MilinkFragment mMilinkFragment;
	public AirkanManager mAirkanManager;
	private MilinkView mMilinkFrame;
	public Activity mActivity;
	public FrameLayout mAnchor;
    public FullScreenVideoController mVideoController;
	
	public AirkanController(AirkanManager airkanManager, 
	        FullScreenVideoController videoController){
		mAirkanManager = airkanManager;
		mAirkanManager.setAirkanOnChangedListener(new MilinkStatusListener(this));
		mVideoController = videoController;
	}
	
	public void setActivity(Activity activity, FrameLayout anchor){
		mActivity = activity;
		mAnchor = anchor;
	}
	
	public void attachMilinkFragment(MilinkFragment fragment){
	    mMilinkFragment = fragment;
	}
		
	private MilinkView getMilinkView(){
		if(mMilinkFrame == null){
			mMilinkFrame = (MilinkView)LayoutInflater.from(mActivity).inflate(R.layout.vp_airkan_playing, 
			        mAnchor, false);
			mAnchor.addView(mMilinkFrame);
		}
		return mMilinkFrame;
	}
	
	private void enterAirkanMode(){
		getMilinkView().setVisibility(View.VISIBLE);
		if(mVideoController != null && mAirkanManager != null && mMilinkFragment != null){
		      getMilinkView().setPlayingDevice(mAirkanManager.getPlayingDeviceName());
		    mVideoController.attachMediaPlayer(mMilinkFragment, 
		            mAirkanManager.getPlayer());
		    mVideoController.showController();
		}
	}
	
	private void exitAirkanMode(){
		getMilinkView().setVisibility(View.GONE);
		if(mVideoController != null && mAirkanManager != null && mMilinkFragment != null
		        && mMilinkFragment.getLocalFragment() != null){
		    mVideoController.attachMediaPlayer(mMilinkFragment.getLocalFragment(),
		            mMilinkFragment.getLocalPlayer());
		    mVideoController.hideController();
		}
	}
	
	private static class MilinkStatusListener implements OnStatusChangedListener{

	    public WeakReference<AirkanController> mCtrlRef;
	    
	    public MilinkStatusListener(AirkanController controller){
	        mCtrlRef = new WeakReference<AirkanController>(controller);
	    }
	    
		@Override
		public void onStatusChanged(AirkanChangedEvent event) {
		    final AirkanController controller = getController();
		    if(controller == null){
		        return;
		    }
			if(event.getCode() == AirkanChangedEvent.CODE_AIR_KAN_BACK_TO_PHONE ||
			        event.getCode() == AirkanChangedEvent.CODE_AIR_KAN_PLAY_STOPED) {
			    controller.exitAirkanMode();
			} else if(event.getCode() == AirkanChangedEvent.CODE_AIR_KAN_PLAY_TO_DEVICE) {
			    controller.enterAirkanMode();
			}			
		}
		
		public AirkanController getController(){
		    final WeakReference<AirkanController> ref = mCtrlRef;
		    if(ref != null){
		        return ref.get();
		    }
		    return null;
		}
	};
	
	public void reset(){
		if(mMilinkFrame != null){
			mMilinkFrame.setVisibility(View.GONE);
		}
	}

}
