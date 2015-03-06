package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */
public class MediaPagerViewV extends MediaPagerViewBase {

	public MediaPagerViewV(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaPagerViewV(Context context) {
		super(context);
	}

	@Override
	protected int getContentViewRes() {
		return R.layout.media_pager_view_v;
	}

}
