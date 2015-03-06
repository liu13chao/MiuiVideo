package com.miui.video.adapter;

import android.content.Context;

import com.miui.video.widget.media.MediaView;
import com.miui.video.widget.media.MediaViewH;

/**
 *@author tangfuling
 *
 */
public class MediaViewGridHAdapter extends MediaViewGridAdapter {

	public MediaViewGridHAdapter(Context context) {
		super(context);
	}

	@Override
	public MediaView createMediaView() {
		return new MediaViewH(mContext);
	}

}
