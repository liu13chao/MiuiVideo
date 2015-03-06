package com.miui.video.widget.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.widget.filter.FilterViewFilter.ButtonTag;

/**
 *@author tangfuling
 *
 */

public class FilterViewMenuItem extends LinearLayout {
	
	private Context mContext;
	private TextView mTextView;
	private int mTextSize;
	
	private ButtonTag mTag;
	
	public FilterViewMenuItem(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public FilterViewMenuItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public FilterViewMenuItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}
	
	protected ButtonTag getData() {
		return mTag;
	}
	
	protected void setData(ButtonTag tag) {
		if(tag == null) {
			return;
		}
		this.mTag = tag;
		StringBuilder sb = new StringBuilder();
		sb.append(tag.parentChannelName);
		sb.append(":");
		sb.append(tag.channelName);
		mTextView.setText(sb.toString());
	}
	
	//init
	private void init() {
		initDimen();
		initUI();
	}
	
	private void initDimen() {
		mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.font_size_13);
	}
	
	private void initUI() {
		setBackgroundResource(R.drawable.filter_view_menu_item_bg);
		mTextView = new TextView(mContext);
		mTextView.setTextColor(getResources().getColor(R.color.p_80_white));
		mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		
		LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.gravity = Gravity.CENTER;
		addView(mTextView, textParams);
	}
}
