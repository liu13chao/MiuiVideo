package com.miui.video.widget.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ObserverScrollView extends ScrollView {

	private OnScrollChangedListener mOnScrollChangedListener;
	
	public ObserverScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObserverScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ObserverScrollView(Context context) {
		super(context);
	}
	
	public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
		this.mOnScrollChangedListener = onScrollChangedListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if(mOnScrollChangedListener != null) {
			mOnScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
		}
	}
	
	public interface OnScrollChangedListener {
		public void onScrollChanged(int l, int t, int oldl, int oldt);
	}
}
