package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;

import com.miui.video.R;
import com.miui.video.api.ApiConfig;
import com.miui.video.type.TelevisionInfo;

/**
 *@author tangfuling
 *
 */
public class MediaViewTv extends MediaView {

	public MediaViewTv(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaViewTv(Context context) {
		super(context);
	}
	
	@Override
	protected int getContentViewRes() {
		return R.layout.media_view_tv;
	}

	@Override
	protected void refreshContentInfo() {
		super.refreshContentInfo();
		if(mBaseMediaInfo == null) {
			return;
		}
		if(mBaseMediaInfo instanceof TelevisionInfo) {
			TelevisionInfo tvInfo = (TelevisionInfo) mBaseMediaInfo;
			int tvColor = tvInfo.backgroundcolor;
			switch (tvColor) {
			case ApiConfig.COLOR_ORANGE:
				mPoster.setBorderResource(R.drawable.tv_bg_orange);
				break;
			case ApiConfig.COLOR_RED:
				mPoster.setBorderResource(R.drawable.tv_bg_red);
				break;
			case ApiConfig.COLOR_GREEN:
				mPoster.setBorderResource(R.drawable.tv_bg_green);
				break;
			case ApiConfig.COLOR_BLUE:
				mPoster.setBorderResource(R.drawable.tv_bg_blue);
				break;
			default:
				mPoster.setBorderResource(R.drawable.default_border_right_angle);
				break;
			}
		}
	}
}
