package com.miui.videoplayer.common;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class DisplayInformationFetcher {
	public static final int SCREEN_LAND = 0;
	public static final int SCREEN_PORT = 1;
	
	private static DisplayInformationFetcher sInstance;
	
	private Display mDefaultDisplay;
	
	private DisplayInformationFetcher(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		mDefaultDisplay = wm.getDefaultDisplay();
	}

	public static DisplayInformationFetcher getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new DisplayInformationFetcher(context);
		}
		return sInstance;
	}
	
	@SuppressLint("NewApi")
	public int getScreenWidth() {
		Point point = new Point();
		mDefaultDisplay.getSize(point);
		return point.x;
	}
	
	@SuppressLint("NewApi")
	public int getScreenHeight() {
		Point point = new Point();
		mDefaultDisplay.getSize(point);
		return point.y;
	}
	
	public int getScreenOrientation() {
		int rotation = mDefaultDisplay.getRotation();
		if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) {
			return SCREEN_PORT;
		} else if (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
			return SCREEN_LAND;
		}
		return SCREEN_LAND;
	}
	
	public DisplayMetrics getDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		mDefaultDisplay.getMetrics(dm);
		return dm;
	}
	
}
