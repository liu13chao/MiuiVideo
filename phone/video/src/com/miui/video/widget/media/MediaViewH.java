package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */
public class MediaViewH extends MediaView {

	public MediaViewH(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaViewH(Context context) {
		super(context);
	}
	
	@Override
	protected int getContentViewRes() {
		return R.layout.media_view_h;
	}
}
