package com.miui.video;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.miui.video.base.BaseTitleActivity;
import com.miui.video.util.DKLog;

public class AboutActivity extends BaseTitleActivity {
	
	private final String TAG = "AboutActivity";
	private TextView mVideoVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}
	
	//init
	private void init() {
		setTopTitle(R.string.about);
		mVideoVersion = (TextView) findViewById(R.id.about_video_version);
		
		String version = getPackageVersion();
		String str = getResources().getString(R.string.video_version);
		str = String.format(str, version);
		mVideoVersion.setText(str);
	}
	
	private String getPackageVersion() {
		PackageManager packageManager = getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = packageManager.getPackageInfo(getPackageName(), PackageManager.PERMISSION_GRANTED);
		} catch (Exception e) {
			DKLog.e(TAG, e.getLocalizedMessage());
		}
		
		if(packageInfo != null) {
			return packageInfo.versionName;
		}
		return "";
	}
	
	@Override
	protected int getContentViewRes() {
		return R.layout.about;
	}
}
