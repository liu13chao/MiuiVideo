package com.miui.video.adapter;

import android.content.Context;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */
public class MediaListHAdapter extends MediaListAdapter {

	public MediaListHAdapter(Context context) {
		super(context);
	}

	@Override
	public int getNumColumns() {
		return 2;
	}

	@Override
	public int getItemViewRes() {
		return R.layout.media_list_h;
	}

}
