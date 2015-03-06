package com.miui.video.widget.statusbtn;

import com.miui.video.R;
import com.miui.video.widget.TextViewFocus;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StatusBtnLong extends RelativeLayout{
	
	private Context mContext;
	
	private LinearLayout mLeftView;
	private TextView mLeftTv;
	private TextViewFocus mLeftTvFocus;
	
	private FrameLayout mRightView;
	private ImageView mRightIv;
	private ProgressBar mRightLoading;
	private CircleProgressBar mRightPercentView;
	
	private View mDividerView;
	
	private int mHeight;
	
	private int mTextSize;
	private int mTextColor;
	private int mTextColorDisable;
	private int mLeftTvFocusLeftMargin;
	private int mLeftTvFocusRightMargin;
	private int mRightIvWidth;
	private int mRightIvHeight;
	private int mDividerHeight;
	
	private StatusBtnItem mStatusBtnItem;

	public StatusBtnLong(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public StatusBtnLong(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public StatusBtnLong(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}
	
	public void setStatusBtnItem(StatusBtnItem statusBtnItem) {
		mStatusBtnItem = statusBtnItem;
		refresh();
	}
	
	public StatusBtnItem getStatusBtnItem() {
		return mStatusBtnItem;
	}
	
	//init
	private void init() {
		initDimen();
		initUI();
	}
	
	private void initDimen() {
		Resources res = mContext.getResources();
		mHeight = res.getDimensionPixelSize(R.dimen.detail_variety_item_height);
		
		mTextSize = res.getDimensionPixelSize(R.dimen.detail_variety_font_size);
		mTextColor = res.getColor(R.color.p_80_black);
		mTextColorDisable = res.getColor(R.color.p_50_black);
		mLeftTvFocusLeftMargin = res.getDimensionPixelSize(R.dimen.detail_variety_item_name_left_margin);
		mLeftTvFocusRightMargin = res.getDimensionPixelSize(R.dimen.detail_variety_item_name_right_margin);
		mRightIvWidth = res.getDimensionPixelSize(R.dimen.status_btn_iv_width);
		mRightIvHeight = res.getDimensionPixelSize(R.dimen.status_btn_iv_height);
		mDividerHeight = res.getDimensionPixelSize(R.dimen.video_divider_height);
	}
	
	private void initUI() {
		mLeftView = new LinearLayout(mContext);
		mLeftView.setOrientation(LinearLayout.HORIZONTAL);
		mLeftTv = new TextView(mContext);
		mLeftTv.setTextColor(mTextColor);
		mLeftTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		LinearLayout.LayoutParams leftTvParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLeftView.addView(mLeftTv, leftTvParams);
		mLeftTvFocus = new TextViewFocus(mContext);
		mLeftTvFocus.setTextColor(mTextColor);
		mLeftTvFocus.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		mLeftTvFocus.setSingleLine(true);
		mLeftTvFocus.setEllipsize(TruncateAt.MARQUEE);
		mLeftTvFocus.setMarqueeRepeatLimit(-1);
		LinearLayout.LayoutParams leftTvFocusParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		leftTvFocusParams.leftMargin = mLeftTvFocusLeftMargin;
		leftTvFocusParams.rightMargin = mLeftTvFocusRightMargin;
		mLeftView.addView(mLeftTvFocus, leftTvFocusParams);
		
		mRightView = new FrameLayout(mContext);
		mRightIv = new ImageView(mContext);
		mRightIv.setBackgroundResource(R.drawable.status_btn_play);
		FrameLayout.LayoutParams rightIvParams = new FrameLayout.LayoutParams(mRightIvWidth, mRightIvHeight);
		rightIvParams.gravity = Gravity.CENTER;
		mRightView.addView(mRightIv, rightIvParams);
		mRightLoading = (ProgressBar) View.inflate(mContext, R.layout.progressblacksmall, null);
		FrameLayout.LayoutParams rightPbParams = new FrameLayout.LayoutParams(mRightIvWidth, mRightIvHeight);
		rightPbParams.gravity = Gravity.CENTER;
		mRightView.addView(mRightLoading, rightPbParams);
		
		BitmapDrawable foreDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.status_btn_circle_fore);
		BitmapDrawable backDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.status_btn_circle_back);
		mRightPercentView = new CircleProgressBar(mContext, mRightIvWidth, mRightIvHeight, backDrawable, foreDrawable);
		FrameLayout.LayoutParams rightWaitParams = new FrameLayout.LayoutParams(mRightIvWidth, mRightIvHeight);
		rightWaitParams.gravity = Gravity.CENTER;
		mRightView.addView(mRightPercentView, rightWaitParams);
		
		mDividerView = new View(mContext);
		mDividerView.setBackgroundResource(R.drawable.divider_bg_black_10);
		
		LayoutParams leftViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		leftViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(mLeftView, leftViewParams);
		LayoutParams rightViewParams = new LayoutParams(mRightIvWidth, mRightIvHeight);
		rightViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rightViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
		addView(mRightView, rightViewParams);
		
		LayoutParams dividerViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, mDividerHeight);
		dividerViewParams.topMargin = mHeight;
		addView(mDividerView, dividerViewParams);
		
		setStatusTextOnly();
	}
	
	//packaged method
	private void refresh() {
		refreshClikable();
		refreshText();
		refreshStatus();
		refreshPercent();
	}
	
	private void refreshClikable() {
		if(mStatusBtnItem == null) {
			return;
		}
		setEnabled(mStatusBtnItem.clickable);
		
		if(mStatusBtnItem.clickable) {
			mLeftTv.setTextColor(mTextColor);
			mLeftTvFocus.setTextColor(mTextColor);
		} else {
			mLeftTv.setTextColor(mTextColorDisable);
			mLeftTvFocus.setTextColor(mTextColorDisable);
		}
	}
	
	private void refreshText() {
		if(mStatusBtnItem == null) {
			return;
		}
		mLeftTv.setText(mStatusBtnItem.date);
		mLeftTvFocus.setText(mStatusBtnItem.videoName);
		if(mStatusBtnItem.textColorEnable) {
			mLeftTv.setTextColor(mTextColor);
			mLeftTvFocus.setTextColor(mTextColor);
		} else {
			mLeftTv.setTextColor(mTextColorDisable);
			mLeftTvFocus.setTextColor(mTextColorDisable);
		}
		if(!mStatusBtnItem.clickable) {
			mLeftTv.setTextColor(mTextColorDisable);
			mLeftTvFocus.setTextColor(mTextColorDisable);
		}
	}
	
	private void refreshStatus() {
		if(mStatusBtnItem == null) {
			return;
		}
		
		int uiStatus = mStatusBtnItem.uiStatus;
		switch (uiStatus) {
		case StatusBtn.UI_STATUS_DELETE:
			setStatusDelete();
			break;
			
		case StatusBtn.UI_STATUS_PLAY:
			setStatusPlay();
			break;
		case StatusBtn.UI_STATUS_LOADING:
			setStatusLoading();
			break;
		case StatusBtn.UI_STATUS_CONNECT:
			setStatusConnect();
			break;
		case StatusBtn.UI_STATUS_PAUSE:
			setStatusPause();
			break;
		case StatusBtn.UI_STATUS_WAITING:
			setStatusWaiting();
			break;
		case StatusBtn.UI_STATUS_DOWNLOAD:
			setStatusDownload();
			break;
		case StatusBtn.UI_STATUS_DONE:
			setStatusDone();
			break;
		case StatusBtn.UI_STATUS_ERROR:
			setStatusError();
			break;
			
		case StatusBtn.UI_STATUS_TEXT_ONLY:
			setStatusTextOnly();
			break;

		default:
			setStatusError();
			break;
		}
	}
	
	private void refreshPercent() {
		if(mStatusBtnItem != null) {
			int percent = (int) (mStatusBtnItem.percent);
			mRightPercentView.setProgress(percent);
		}
	}
	
	private void setStatusLoading() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.INVISIBLE);
		
		mRightLoading.setVisibility(View.VISIBLE);
		mRightIv.setVisibility(View.INVISIBLE);
	}
	
	private void setStatusPlay() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.INVISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setBackgroundResource(R.drawable.status_btn_play);
	}
	
	private void setStatusConnect() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.VISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setBackgroundResource(R.drawable.status_btn_puse);
	}
	
	private void setStatusPause() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.VISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setBackgroundResource(R.drawable.status_btn_play);
	}
	
	private void setStatusWaiting() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.VISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setBackgroundResource(R.drawable.status_btn_puse);
	}
	
	private void setStatusDownload() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.VISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		
		mRightIv.setBackgroundResource(R.drawable.status_btn_download_disable);
		if(mStatusBtnItem != null && mStatusBtnItem.clickable) {
			mRightIv.setBackgroundResource(R.drawable.status_btn_download_enable);
		}
	}
	
	private void setStatusDone() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.INVISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setBackgroundResource(R.drawable.status_btn_done);
	}
	
	private void setStatusError() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.INVISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setBackgroundResource(R.drawable.status_btn_error);
	}
	
	private void setStatusDelete() {
		mRightView.setVisibility(View.VISIBLE);
		mRightPercentView.setVisibility(View.INVISIBLE);
		
		mRightLoading.setVisibility(View.INVISIBLE);
		mRightIv.setVisibility(View.VISIBLE);
		mRightIv.setBackgroundResource(R.drawable.status_btn_delete);
	}
	
	private void setStatusTextOnly() {
		mRightView.setVisibility(View.INVISIBLE);
		mRightPercentView.setVisibility(View.INVISIBLE);
	}
}
