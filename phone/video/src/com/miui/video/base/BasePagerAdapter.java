package com.miui.video.base;

import miui.view.PagerAdapter;


public abstract class BasePagerAdapter extends PagerAdapter {

	@Override
	public boolean hasActionMenu(int arg0) {
		return false;
	}
}
