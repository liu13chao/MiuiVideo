package com.miui.video.widget.media;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.miui.video.type.BaseMediaInfo;

/**
 *@author tangfuling
 *
 */
public class MediaViewImage extends FrameLayout {
	
	protected Context mContext;
	
	protected BaseMediaInfo mMediaInfo;
	
	public MediaViewImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public MediaViewImage(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public void setSouthText(String text) {
	}
	
	public void setSouthEastText(String text) {
	}
	
	public void setBorderResource(int resid) {
	}
//	
//	public void setPosterResource(int resid) {
//	}
	
	public void setMediaInfo(BaseMediaInfo baseMediaInfo) {
		mMediaInfo = baseMediaInfo;
	}
	
	public void setInEditMode(boolean isInEditMode) {
	}
	
	public void setIsSelected(boolean isSelected) {
	}
}
