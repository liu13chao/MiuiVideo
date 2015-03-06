package com.miui.video.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.miui.video.model.AppSingleton;
import com.miui.video.storage.DeviceBrowseTask.DeviceBrowseCompleteListener;
import com.miui.video.util.Util;

public class DLNAMediaManager extends AppSingleton {
	
	private List<MediaUpdateListener> mListeners = new ArrayList<MediaUpdateListener>();
	
	private HashMap<String, List<MediaItem>> mDeviceMediasMap = new HashMap<String, List<MediaItem>>();

	public void browseDevice(BaseDevice baseDevice) {
		if(baseDevice == null) {
			return;
		}
		
		DeviceBrowseTask deviceBrowseTask = new DeviceBrowseTask(baseDevice);
		deviceBrowseTask.addListener(mDeviceBrowseCompleteListener);
		deviceBrowseTask.execute();
	}
	
	public List<MediaItem> getMediaItems(String deviceName) {
		if(Util.isEmpty(deviceName)) {
			return null;
		}
		return mDeviceMediasMap.get(deviceName);
	}
	
	public void addListener(MediaUpdateListener listener) {
		if(listener != null && !mListeners.contains(listener)) {
			mListeners.add(listener);
		}
	}
	
	public void removeListener(MediaUpdateListener listener) {
		if(listener != null) {
			mListeners.remove(listener);
		}
	}
	
	private void notifyMediaUpdate() {
		for(int i = 0; i < mListeners.size(); i++) {
			MediaUpdateListener listener = mListeners.get(i);
			if(listener != null) {
				listener.onMediaUpdate();
			}
		}
	}
	
	//data callback
	private DeviceBrowseCompleteListener mDeviceBrowseCompleteListener = new DeviceBrowseCompleteListener() {
		
		@Override
		public void onDeviceBrowseComplete(BaseDevice baseDevice,
				List<MediaItem> mediaItems) {
			if(baseDevice != null && mediaItems != null) {
				String name = baseDevice.getName();
				if(!Util.isEmpty(name)) {
					mDeviceMediasMap.put(name, mediaItems);
				}
			}
			notifyMediaUpdate();
		}
	};
	
	//self def class
	public interface MediaUpdateListener {
		public void onMediaUpdate();
	}
}
