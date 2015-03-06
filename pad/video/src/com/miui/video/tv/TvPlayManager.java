package com.miui.video.tv;

import com.miui.video.type.TelevisionInfo;
import com.miui.video.util.DKLog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class TvPlayManager {
	private static final String TAG = TvPlayManager.class.getName();
	
	public static void playChannel(Context context, TelevisionInfo tvInfo, String tvEntry) {
		if(tvInfo == null) {
			return;
		}
		
		int tvPlayId = tvInfo.epgid;
		DKLog.e(TAG, "start tv palying..." +tvPlayId);
		Intent intent = new Intent();
		Bundle bundle = new Bundle(); 
		bundle.putSerializable(TvPlayerActivity.KEY_TV_INFO, tvInfo);
		bundle.putString(TvPlayerActivity.KEY_TV_ENTRY, tvEntry);
		intent.putExtras(bundle);
		intent.setClass(context, TvPlayerActivity.class);
		context.startActivity(intent);
	}
}
