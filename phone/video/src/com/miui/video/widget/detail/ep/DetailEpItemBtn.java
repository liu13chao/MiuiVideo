package com.miui.video.widget.detail.ep;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;

public class DetailEpItemBtn extends FrameLayout {

	private Context mContext;
	
	private TextView mBtn;
	private View mLoadView;
	
	private int mWidth;
	private int mHeight;
	private int mLoadWidth;
	private int mLoadHeight;
	
	private int mColorNormal;
	private int mColorSelected;
	
	//data
	private SetInfoStatusEp mItem;
	
	public DetailEpItemBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public DetailEpItemBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public DetailEpItemBtn(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public SetInfoStatusEp getData() {
		return mItem;
	}
	
	public void setData(SetInfoStatusEp item) {
		this.mItem = item;
		refresh();
	}

	//init
	private void init() {
		mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.detail_ep_multy_btn_width);
		mHeight = mContext.getResources().getDimensionPixelSize(R.dimen.detail_ep_multy_btn_height);
		mLoadWidth = mContext.getResources().getDimensionPixelSize(R.dimen.detail_ep_item_load_width);
		mLoadHeight = mContext.getResources().getDimensionPixelSize(R.dimen.detail_ep_item_load_height);
		
		mColorNormal = mContext.getResources().getColor(R.color.p_80_black);
		mColorSelected = mContext.getResources().getColor(R.color.orange);
		
		mBtn = new TextView(mContext);
		mBtn.setClickable(false);
		mBtn.setTextColor(mColorNormal);
		mBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_size_42));
		mBtn.setBackgroundResource(R.drawable.com_btn_bg);
		mBtn.setGravity(Gravity.CENTER);
		LayoutParams btnParams = new LayoutParams(mWidth, mHeight);
		addView(mBtn, btnParams);
		
		mLoadView = View.inflate(mContext, R.layout.progressblacksmall, null);
		mLoadView.setVisibility(View.INVISIBLE);
		LayoutParams loadViewParams = new LayoutParams(mLoadWidth, mLoadHeight);
		loadViewParams.gravity = Gravity.CENTER;
		addView(mLoadView, loadViewParams);
	}
	
	private void refresh() {
		if(mItem == null) {
			return;
		}
		mBtn.setText(mItem.episode +"");
		if(mItem.isLoading) {
			mLoadView.setVisibility(View.VISIBLE);
		} else {
			mLoadView.setVisibility(View.INVISIBLE);
		}
		boolean isSelected = mItem.isSelected;
		mBtn.setSelected(isSelected);
		if(isSelected) {
			mBtn.setTextColor(mColorSelected);
		} else {
			mBtn.setTextColor(mColorNormal);
		}
	}
}
