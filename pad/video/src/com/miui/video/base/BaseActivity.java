package com.miui.video.base;

import miui.app.Activity;
import android.app.ActionBar;
import android.os.Bundle;

public class BaseActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initStyle();
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		if(actionBar != null) {
			actionBar.hide();
		}
	}
	
	private void initStyle() {
		setTheme(miui.R.style.Theme_Dark_NoTitle);
	}
}
