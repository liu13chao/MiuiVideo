package com.miui.video.adapter;

import android.content.Context;

import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaViewTv;

/**
 *@author tangfuling
 *
 */
public class MediaViewGridTvAdapter extends MediaViewGridAdapter {

	public MediaViewGridTvAdapter(Context context) {
		super(context);
	}

	@Override
	public MediaView createMediaView() {
		return new MediaViewTv(mContext);
	}

}
