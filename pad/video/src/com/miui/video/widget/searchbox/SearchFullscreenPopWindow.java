package com.miui.video.widget.searchbox;

import com.miui.video.R;
import com.miui.video.util.DKLog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.FrameLayout.LayoutParams;

public class SearchFullscreenPopWindow extends PopupWindow {
	
	private static String TAG = SearchFullscreenPopWindow.class.getName(); 
	
	public SearchFullscreenPopWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.search_fullscreen, null), 
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	}
	
	public void show(View anchor) {
		try {
			showAtLocation(anchor, Gravity.TOP, 0, 0);
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
	}
}
