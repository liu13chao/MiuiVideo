package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 *@author tangfuling
 *
 */
public class GridViewEx extends GridView {

	public GridViewEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public GridViewEx(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public GridViewEx(Context context) {
		super(context);
		
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
		ViewGroup.LayoutParams params = getLayoutParams();
		params.height = getMeasuredHeight();
	}
}
