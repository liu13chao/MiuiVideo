package com.miui.videoplayer.views;

import java.util.Map;

import android.app.Activity;
import android.net.Uri;

import com.miui.videoplayer.framework.airkan.AirkanManager.AirkanExistDeviceInfo;
import com.miui.videoplayer.framework.history.PlayHistoryManager.PlayHistoryEntry;
import com.miui.videoplayer.framework.ui.LocalMediaPlayerControl;
import com.miui.videoplayer.framework.ui.LocalVideoPlaySizeAdjustable;

// For switching duokan codec and origin codec, will remove
public interface ITempVideoView extends LocalMediaPlayerControl, LocalVideoPlaySizeAdjustable{
	void onActivityStart();
	void onActivityPause();
	void onActivityStop();
	void setInput(String uri, Activity activity);
	String getPlayingUri();
	void setVideoURI(Uri uri);
	int getAdjustWidth();
	int getAdjustHeight();
	boolean isPaused();
	void setInput(String uri, Activity activity, AirkanExistDeviceInfo airkanExistDeviceInfo); 
	void setInput(String[] uris, int playIndex, Activity activity);
	void setTitleMap(Map<String, PlayHistoryEntry> map);
	void onActivityCreate();
	void onActivityDestroy();
	void setDirectAirkanUri(Uri uri);
	void onNewIntent();
	void checkNetwork(Uri uri);
}
