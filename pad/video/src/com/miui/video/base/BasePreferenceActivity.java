package com.miui.video.base;

import com.miui.video.R;

import android.app.ActionBar;
import android.os.Bundle;
import miui.preference.PreferenceActivity;

public class BasePreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initStyle();
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		if(actionBar != null) {
			actionBar.setTitle(R.string.app_name);
		}
	}
	
	private void initStyle() {
		setTheme(miui.R.style.Theme_Light_Settings);
	}
}
