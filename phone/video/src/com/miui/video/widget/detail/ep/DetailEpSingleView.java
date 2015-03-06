package com.miui.video.widget.detail.ep;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.type.AppRecommandInfo;
import com.miui.video.type.MediaDetailInfo;

public class DetailEpSingleView extends LinearLayout {

	private Context mContext;
	
	private int mRecommendHeight;
	private int mItemHeight;
	private int mItemLeftRightMargin;
	//data
	private MediaDetailInfo mMediaDetailInfo; 
	
	public DetailEpSingleView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initDimen();
		initEnv();
	}

	
	public DetailEpSingleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initDimen();
		initEnv();
	}

	public DetailEpSingleView(Context context) {
		super(context);
		this.mContext = context;
		initDimen();
		initEnv();
	}
	
	protected void setData(MediaDetailInfo mediaDetailInfo) {
		this.mMediaDetailInfo = mediaDetailInfo;
		initUI();
	}
	
	//init
	
	private void initEnv(){
		setBackgroundResource(R.drawable.com_bg_white_corner);
		setPadding(mItemLeftRightMargin, 0, mItemLeftRightMargin, 0);
	}
	
	private void initDimen() {
		mRecommendHeight = mContext.getResources().getDimensionPixelSize(R.dimen.detail_ep_single_item_recommend_height);
		mItemHeight = mContext.getResources().getDimensionPixelSize(R.dimen.detail_ep_single_item_height);
		mItemLeftRightMargin = mContext.getResources().getDimensionPixelSize(R.dimen.detail_ep_single_item_left_right_margin);
		setOrientation(VERTICAL);
	}
	
	private void initUI() {
		removeAllViews();
		if(mMediaDetailInfo == null || mMediaDetailInfo.recommend_miui == null
				|| mMediaDetailInfo.recommend_miui.length == 0) {
			setVisibility(View.GONE);
			return;
		}
		TextView hotRecommendTv = createTextView();
		hotRecommendTv.setText(R.string.hot_recommend);
		LayoutParams hotRecommendParams = new LayoutParams(LayoutParams.MATCH_PARENT, mRecommendHeight);
		addView(hotRecommendTv, hotRecommendParams);
		
		AppRecommandInfo[] appRecommandInfos = mMediaDetailInfo.recommend_miui;
		for(int i = 0; i < appRecommandInfos.length; i++) {
			addDividerView();
			DetailEpSingleItemView detailEpSingleItemView = new DetailEpSingleItemView(mContext);
			detailEpSingleItemView.setData(mMediaDetailInfo.mediaid, appRecommandInfos[i]);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
			addView(detailEpSingleItemView, params);
		}
	}
	
	//packaged method
	private TextView createTextView() {
		TextView tv = new TextView(mContext);
		tv.setGravity(Gravity.CENTER_VERTICAL);
		tv.setTextColor(mContext.getResources().getColor(R.color.orange));
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.font_size_40));
		return tv;
	}
	
	private void addDividerView() {
		View dividerView = new View(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 
				mContext.getResources().getDimensionPixelSize(R.dimen.video_common_divider_height));
		dividerView.setBackgroundResource(R.drawable.com_3_black);
		addView(dividerView, params);
	}
}
