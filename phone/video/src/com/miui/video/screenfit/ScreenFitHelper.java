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
	
	public static int getHistorySizePerRow() {
		return DKApp.getAppContext().getResources()
				.getInteger(R.integer.history_size_per_row);
	}
	
	public static int getInformationSizePerRow() {
		return DKApp.getAppContext().getResources()
				.getInteger(R.integer.information_size_per_row);
	}
}
