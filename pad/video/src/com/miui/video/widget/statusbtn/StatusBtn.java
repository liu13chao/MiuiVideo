package com.miui.video.widget.statusbtn;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
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

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */

public class StatusBtn extends RelativeLayout {
	
	private Context mContext;
	
	private LinearLayout mLeftContainer;
	
	private FrameLayout mLeftView;
	private ImageView mLeftIv;
	private ProgressBar mLeftLoading;
	private CircleProgressBar mLeftPercentView;
	private TextView mLeftTextView;
	
	private FrameLayout mRightContainer;
	private ImageView mRightIv;
	
	private int mStatusBtnLeftIntervalH;
	private int mStatusBtnLeftWidth;
	private int mStatusBtnLeftHeight;
	private int mStatusBtnIvWidth;
	private int mStatusBtnIvHeight;
	private int mStatusBtnFontSize;
	private int mTextColor;
	private int mTextColorDisable;
	
	public static final int UI_STATUS_TEXT_ONLY = 0;
	public static final int UI_STATUS_LOADING = 1;
	public static final int UI_STATUS_PLAY = 2;
	
	public static final int UI_STATUS_CONNECT = 3;
	public static final int UI_STATUS_PAUSE = 4;
	public static final int UI_STATUS_WAITING = 5;
	public static final int UI_STATUS_DOWNLOAD = 6;
	public static final int UI_STATUS_DONE = 7;
	public static final int UI_STATUS_ERROR = 8;
	
	public static final int UI_STATUS_DELETE = 9;
	
	private StatusBtnItem mStatusBtnItem;	

	public StatusBtn(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public StatusBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}
	
	public StatusBtn(Context context, AttributeSet attrs, int defStyle) {
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
		mStatusBtnLeftIntervalH = mContext.getResources().getDimensionPixelSize(R.dimen.status_btn_left_intervalH);
		mStatusBtnLeftWidth = mContext.getResources().getDimensionPixelSize(R.dimen.status_btn_left_width);
		mStatusBtnLeftHeight = mContext.getResources().getDimensionPixelSize(R.dimen.status_btn_left_height);
		mStatusBtnIvWidth = mContext.getResources().getDimensionPixelSize(R.dimen.status_btn_iv_width);
		mStatusBtnIvHeight = mContext.getResources().getDimensionPixelSize(R.dimen.status_btn_iv_height);
		mStatusBtnFontSize = mContext.getResources().getDimensionPixelSize(R.dimen.status_btn_font_size);
		mTextColor = mContext.getResources().getColor(R.color.p_80_black);
		mTextColorDisable = mContext.getResources().getColor(R.color.p_50_black);
	}
	
	private void initUI() {
		mLeftView = new FrameLayout(mContext);
		mLeftIv = new ImageView(mContext);
		mLeftIv.setBackgroundResource(R.drawable.status_btn_play);
		FrameLayout.LayoutParams leftIvParams = new FrameLayout.LayoutParams(mStatusBtnIvWidth, mStatusBtnIvHeight);
		leftIvParams.gravity = Gravity.CENTER;
		mLeftView.addView(mLeftIv, leftIvParams);
		mLeftLoading = (ProgressBar) View.inflate(mContext, R.layout.progressblacksmall, null);
		FrameLayout.LayoutParams leftPbParams = new FrameLayout.LayoutParams(mStatusBtnIvWidth, mStatusBtnIvHeight);
		leftPbParams.gravity = Gravity.CENTER;
		mLeftView.addView(mLeftLoading, leftPbParams);
		
		BitmapDrawable foreDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.status_btn_circle_fore);
		BitmapDrawable backDrawable = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.status_btn_circle_back);
		mLeftPercentView = new CircleProgressBar(mContext, mStatusBtnIvWidth, mStatusBtnIvHeight, backDrawable, foreDrawable);
		FrameLayout.LayoutParams leftPercentParams = new FrameLayout.LayoutParams(mStatusBtnIvWidth, mStatusBtnIvHeight);
		leftPercentParams.gravity = Gravity.CENTER;
		mLeftView.addView(mLeftPercentView, leftPercentParams);
		
		mLeftTextView = new TextView(mContext);
		mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mStatusBtnFontSize);
		mLeftTextView.setTextColor(mTextColor);
		
		mLeftContainer = new LinearLayout(mContext);
		mLeftContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		LinearLayout.LayoutParams leftViewParams = new LinearLayout.LayoutParams(mStatusBtnLeftWidth, mStatusBtnLeftHeight);
		mLeftContainer.addView(mLeftView, leftViewParams);
		LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		textViewParams.gravity = Gravity.CENTER_VERTICAL;
		textViewParams.leftMargin = mStatusBtnLeftIntervalH;
		mLeftContainer.addView(mLeftTextView, textViewParams);
		RelativeLayout.LayoutParams leftContainerParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		leftContainerParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		addView(mLeftContainer, leftContainerParams);
		
		mRightContainer = new FrameLayout(mContext);
		mRightIv = new ImageView(mContext);
		mRightIv.setBackgroundResource(R.drawable.status_btn_delete);
		FrameLayout.LayoutParams rightIvParams = new FrameLayout.LayoutParams(mStatusBtnIvWidth, mStatusBtnIvHeight);
		rightIvParams.gravity = Gravity.CENTER;
		mRightContainer.addView(mRightIv, rightIvParams);
		RelativeLayout.LayoutParams rightContainerParams = new RelativeLayout.LayoutParams(mStatusBtnLeftWidth, mStatusBtnLeftHeight);
		rightContainerParams.addRule(RelativeLayout.CENTER_VERTICAL);
		rightContainerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addView(mRightContainer, rightContainerParams);
		
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
	}
	
	private void refreshText() {
		if(mStatusBtnItem == null) {
			return;
		}
		mLeftTextView.setText(mStatusBtnItem.text);
		if(mStatusBtnItem.textColorEnable) {
			mLeftTextView.setTextColor(mTextColor);
		} else {
			mLeftTextView.setTextColor(mTextColorDisable);
		}
		if(!mStatusBtnItem.clickable) {
			mLeftTextView.setTextColor(mTextColorDisable);
		}
	}
	
	private void refreshStatus() {
		if(mStatusBtnItem == null) {
			return;
		}
		
		int uiStatus = mStatusBtnItem.uiStatus;
		switch (uiStatus) {
		case UI_STATUS_DELETE:
			setStatusDelete();
			break;
			
		case UI_STATUS_PLAY:
			setStatusPlay();
			break;
		case UI_STATUS_LOADING:
			setStatusLoading();
			break;
		case UI_STATUS_CONNECT:
			setStatusConnect();
			break;
		case UI_STATUS_PAUSE:
			setStatusPause();
			break;
		case UI_STATUS_WAITING:
			setStatusWaiting();
			break;
		case UI_STATUS_DOWNLOAD:
			setStatusDownload();
			break;
		case UI_STATUS_DONE:
			setStatusDone();
			break;
		case UI_STATUS_ERROR:
			setStatusError();
			break;
			
		case UI_STATUS_TEXT_ONLY:
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
			mLeftPercentView.setProgress(percent);
		} 
	}
	
	private void setStatusLoading() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.INVISIBLE);
		
		mLeftLoading.setVisibility(View.VISIBLE);
		mLeftIv.setVisibility(View.INVISIBLE);
		
		if(mStatusBtnItem != null && mStatusBtnItem.showIconOnly) {
			mLeftTextView.setVisibility(View.GONE);
		} else {
			mLeftTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void setStatusPlay() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.INVISIBLE);
		
		mLeftLoading.setVisibility(View.INVISIBLE);
		mLeftIv.setVisibility(View.VISIBLE);
		mLeftIv.setBackgroundResource(R.drawable.status_btn_play);
		
		if(mStatusBtnItem != null && mStatusBtnItem.showIconOnly) {
			mLeftTextView.setVisibility(View.GONE);
		} else {
			mLeftTextView.setVisibility(View.VISIBLE);
		}
	}
	
	private void setStatusConnect() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.VISIBLE);
		
		mLeftLoading.setVisibility(View.INVISIBLE);
		mLeftIv.setVisibility(View.VISIBLE);
		mLeftIv.setBackgroundResource(R.drawable.status_btn_puse);
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	private void setStatusPause() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.VISIBLE);
		
		mLeftLoading.setVisibility(View.INVISIBLE);
		mLeftIv.setVisibility(View.VISIBLE);
		mLeftIv.setBackgroundResource(R.drawable.status_btn_play);
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	private void setStatusWaiting() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.VISIBLE);
		
		mLeftLoading.setVisibility(View.INVISIBLE);
		mLeftIv.setVisibility(View.VISIBLE);
		mLeftIv.setBackgroundResource(R.drawable.status_btn_puse);
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	private void setStatusDownload() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.VISIBLE);
		
		mLeftLoading.setVisibility(View.INVISIBLE);
		mLeftIv.setVisibility(View.VISIBLE);
		
		mLeftIv.setBackgroundResource(R.drawable.status_btn_download_disable);
		if(mStatusBtnItem != null && mStatusBtnItem.clickable) {
			mLeftIv.setBackgroundResource(R.drawable.status_btn_download_enable);
		}
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	private void setStatusDone() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.INVISIBLE);
		
		mLeftLoading.setVisibility(View.INVISIBLE);
		mLeftIv.setVisibility(View.VISIBLE);
		mLeftIv.setBackgroundResource(R.drawable.status_btn_done);
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	private void setStatusError() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
		mLeftPercentView.setVisibility(View.INVISIBLE);
		
		mLeftLoading.setVisibility(View.INVISIBLE);
		mLeftIv.setVisibility(View.VISIBLE);
		mLeftIv.setBackgroundResource(R.drawable.status_btn_error);
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	private void setStatusDelete() {
		mRightContainer.setVisibility(View.VISIBLE);
		mLeftView.setVisibility(View.GONE);
		mLeftPercentView.setVisibility(View.GONE);
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	private void setStatusTextOnly() {
		mRightContainer.setVisibility(View.INVISIBLE);
		mLeftView.setVisibility(View.GONE);
		mLeftPercentView.setVisibility(View.GONE);
		
		mLeftTextView.setVisibility(View.VISIBLE);
	}
	
	//self def class
	public interface OnStatusBtnClickListener {
		public void onStatusBtnClick(StatusBtn statusBtn);
	}
}
