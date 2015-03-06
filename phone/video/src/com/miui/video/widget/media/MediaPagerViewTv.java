package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */
public class MediaPagerViewTv extends MediaPagerViewBase {

	public MediaPagerViewTv(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaPagerViewTv(Context context) {
		super(context);
	}

	@Override
	protected int getContentViewRes() {
		return R.layout.media_pager_view_tv;
	}

}
