package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */
public class MediaPagerViewH extends MediaPagerViewBase {

	public MediaPagerViewH(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaPagerViewH(Context context) {
		super(context);
	}

	@Override
	protected int getContentViewRes() {
		return R.layout.media_pager_view_h;
	}

}
