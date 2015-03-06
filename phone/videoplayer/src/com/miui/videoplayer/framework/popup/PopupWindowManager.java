package com.miui.videoplayer.framework.popup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class PopupWindowManager {
	private static PopupWindowManager mInstance = new PopupWindowManager();
	private List<WeakReference<ManagedPopupWindow>> mWeakPopupWindowList = new ArrayList<WeakReference<ManagedPopupWindow>>(); 
	
	private PopupWindowManager() {
	}

	public static PopupWindowManager getInstance() {
		return mInstance;
	}
	
	public void addShowingPopupWindow(ManagedPopupWindow popupWindow) {
		WeakReference<ManagedPopupWindow> wr = getWeakReference(popupWindow);
		if (wr == null) {
			WeakReference<ManagedPopupWindow> newWeakReferPopupWindow = new WeakReference<ManagedPopupWindow>(popupWindow);
//			Log.e("added : ", popupWindow + "");
			mWeakPopupWindowList.add(newWeakReferPopupWindow);
		}
	}
	
	
	public void removeShowingPopupWindow(ManagedPopupWindow popupWindow) {
		WeakReference<ManagedPopupWindow> wr = getWeakReference(popupWindow);
		if (wr != null) {
//			Log.e("removed : ", wr.get() + "");
			mWeakPopupWindowList.remove(wr);
		}
	}
	
	private WeakReference<ManagedPopupWindow> getWeakReference(ManagedPopupWindow popupWindow) {
		for (WeakReference<ManagedPopupWindow> wr : mWeakPopupWindowList) {
			if (wr != null && wr.get() != null && wr.get().equals(popupWindow)) {
				return wr;
			}
		}
		return null;
	}

	public void dimissAllManagedPopupWindow() {
		for (WeakReference<ManagedPopupWindow> wr : mWeakPopupWindowList) {
			if (wr != null && wr.get() != null && wr.get().isShowing()) {
//				Log.e("popupwindows: ", wr.get() + "");
				wr.get().dismiss(false);
			}
		}
		mWeakPopupWindowList.clear();
	}
	
}
