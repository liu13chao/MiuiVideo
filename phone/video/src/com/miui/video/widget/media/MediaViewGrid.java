package com.miui.video.widget.media;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;

import com.miui.video.type.BaseMediaInfo;

/**
 *@author tangfuling
 *
 */
public class MediaViewGrid extends FrameLayout {	
	
	public MediaViewGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MediaViewGrid(Context context) {
		super(context);
	}
	
	public void setGroup(List<BaseMediaInfo> group) {
		
	}
	
	public void setGroup(BaseMediaInfo[] group) {
		
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		
	}
	
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		
	}
	
	public void setInEditMode(boolean isInEditMode) {
		
	}
	
	public void setShowSubTitle(boolean showSubTitle) {
		
	}
}
