package com.miui.video.base;

import miui.app.Activity;

import com.miui.video.R;

import android.app.ActionBar;
import android.os.Bundle;

public class BaseDialog extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		initStyle();
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(R.drawable.dialog_bg);
		ActionBar actionBar = getActionBar();
		if(actionBar != null) {
			actionBar.hide();
		}
	}
	
	private void initStyle() {
		setTheme(miui.R.style.Theme_Dark_Dialog);
	}
}
