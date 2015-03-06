package com.miui.videoplayer.views;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.miui.videoplayer.framework.airkan.AirkanManager.AirkanExistDeviceInfo;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;

public class DuokanVideoView extends OriginVideoView implements ITempVideoView{
	private DuoKanMediaController mDuoKanMediaController;
	private Context mContext;
	
	public DuokanVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public DuokanVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public DuokanVideoView(Context context) {
		super(context);
		this.mContext = context;
	}
	
	@Override
	public void stopLocalPlayForAirkan() {
		stopLocalPlayForMediaSwitch();
	}

	@Override
	public void startLocalPlayForAirkan(Uri videoUri) {
		startLocalPlayForMediaSwitch(videoUri);
	}
	
	@Override
	public void stopLocalPlayForMediaSwitch() {
		this.setVideoURI(null);
		this.setVisibility(View.INVISIBLE);
	}

	@Override
	public void startLocalPlayForMediaSwitch(Uri uri) {
		this.setVisibility(View.VISIBLE);
		this.setVideoURI(uri);
		start();
	}

	public void setInput(String uri, Activity activity) {
		String[] uris = new String[] {uri};
		setInput(uris, 0, activity);
	}
	
	public void setInput(String uri, Activity activity, AirkanExistDeviceInfo airkanExistDeviceInfo) {
		String[] uris = new String[] {uri};
		setInput(uris, 0, activity, airkanExistDeviceInfo);
	}
	
	public void setInput(String[] uris, int playIndex, Activity activity) {
		
		attachDuoKanMediaController();
		mDuoKanMediaController.setInput(uris, playIndex, activity);
		mDuoKanMediaController.setLocalMediaPlayerControl(this);
		mDuoKanMediaController.setVideoSizeAdjustable(this);
	}
	
	private void attachDuoKanMediaController() {
//		mDuoKanMediaController = (DuoKanMediaController) ((View) this.getParent()).findViewById(R.id.test_all_in_one_media_controller);
		
		mDuoKanMediaController = new DuoKanMediaController(mContext);
		ViewGroup vp = (ViewGroup) this.getParent();
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		vp.addView(mDuoKanMediaController, lp);
		super.setDuokanMediaController(mDuoKanMediaController);
	}

	public void setInput(String[] uris, int playIndex, Activity activity, AirkanExistDeviceInfo airkanExistDeviceInfo) {
		attachDuoKanMediaController();
		mDuoKanMediaController.setAirkanExistDeviceInfo(airkanExistDeviceInfo);
		mDuoKanMediaController.setInput(uris, playIndex, activity);
		mDuoKanMediaController.setLocalMediaPlayerControl(this);
		mDuoKanMediaController.setVideoSizeAdjustable(this);
	}
	
	public void onActivityStart() {
		mDuoKanMediaController.onActivityStart();
	}

	public void onActivityPause() {
		mDuoKanMediaController.onActivityPause();
	}

	public void onActivityStop() {
		mDuoKanMediaController.onActivityStop();
	}
	
	public String getPlayingUri() {
		return mDuoKanMediaController.getPlayingUri();
	}

	public void setDirectAirkanUri(Uri uri) {
		mDuoKanMediaController.setDirectAirkanUri(uri);
	}

//	@Override
//	public void onScreenOrientationChanged(int orientation) {
//		mDuoKanMediaController.onScreenOrientationChanged(orientation);
//	}

	@Override
	public void onActivityCreate() {
		mDuoKanMediaController.onActivityCreate();
		mDuoKanMediaController.bindAirkanService();
	}

	@Override
	public void onActivityDestroy() {
		mDuoKanMediaController.onActivityDestroy();
		mDuoKanMediaController.unbindAirkanService();
	}

	@Override
	public void setTitleMap(Map<String, PlayHistoryEntry> map) {
		mDuoKanMediaController.setTitleMap(map);
	}

	@Override
	public void onNewIntent() {
		mDuoKanMediaController.onNewIntent();
	}

	@Override
	public void checkNetwork(Uri uri) {
		mDuoKanMediaController.checkNetwork(uri);
	}

	public void set3dMode(boolean mode) {
		
	}
	
	public boolean get3dMode() {
		return false;
	}
}
