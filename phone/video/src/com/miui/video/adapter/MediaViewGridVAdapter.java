package com.miui.video.adapter;

import android.content.Context;

import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaViewV;

/**
 *@author tangfuling
 *
 */
public class MediaViewGridVAdapter extends MediaViewGridAdapter {

	public MediaViewGridVAdapter(Context context) {
		super(context);
	}

	@Override
	public MediaView createMediaView() {
		return new MediaViewV(mContext);
	}

}
