package com.miui.video.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.miui.video.R;

public class BannerIndicateView extends LinearLayout {

	private Context mContext;
	private int mIndicateCount;
	
	public BannerIndicateView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public BannerIndicateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	public BannerIndicateView(Context context) {
		super(context);
		this.mContext = context;
	}
	
	public void setIndicateCount(int indicateCount) {
		this.mIndicateCount = indicateCount;
		initUI();
	}
	
	public void setCurIndex(int curIndex) {
		for(int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if(i == curIndex) {
				view.setSelected(true);
			} else {
				view.setSelected(false);
			}
		}
	}

	//init
	private void initUI() {
		removeAllViews();
		for(int i = 0; i < mIndicateCount; i++) {
			ImageView imageView = createIndicateView();
			if(i == 0) {
				addView(imageView);
			} else {
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.banner_indicate_view_intervalH);
				addView(imageView, params);
			}
		}
		setCurIndex(0);
	}
	
	//packaged method
	private ImageView createIndicateView() {
		ImageView imageView = new ImageView(mContext);
		imageView.setBackgroundResource(R.drawable.banner_indicate_bg);
		return imageView;
	}
}
