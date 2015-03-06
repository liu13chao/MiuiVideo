package com.miui.video.adapter;


import android.content.Context;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */
public class MediaListVAdapter extends MediaListAdapter {

	public MediaListVAdapter(Context context) {
		super(context);
	}

	@Override
	public int getNumColumns() {
		return 3;
	}

	@Override
	public int getItemViewRes() {
		return R.layout.media_list_v;
	}


}
