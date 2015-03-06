package com.miui.video.screenfit;

import com.miui.video.DKApp;
import com.miui.video.R;

/**
 *@author tangfuling
 *
 */

public class ScreenFitHelper {
	
	public static int getBannerSizePerRow() {
		return DKApp.getAppContext().getResources()
				.getInteger(R.integer.banner_size_per_row);
	}
	
	public static int getVideoSizePerRow() {
		return DKApp.getAppContext().getResources()
				.getInteger(R.integer.video_size_per_row);
	}
	
	public static int getDetailSizePerRow() {
		return DKApp.getAppContext().getResources()
				.getInteger(R.integer.detail_size_per_row);
	}
	
	public static int getLocalSizePerRow() {
		return DKApp.getAppContext().getResources()
				.getInteger(R.integer.local_size_per_row);
	}
}
