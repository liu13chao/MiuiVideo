package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.miui.video.R;

public abstract class AbstractVerticalSeekbarPopupWindows extends PopupWindow {

	private int mSeekbarLength;
	private int mVerticalSpace;
	private int mSeekbarPadding;
	
	private SeekBar mSeekbar;
	private TextView progressTextView;
	private ImageView titleImageView;
	
	private int mImageHeight;
	
	private int backgroundImageHeight;
	
	private boolean mShowProgressNumber = true;
	private Context mContext;
	
	public AbstractVerticalSeekbarPopupWindows(View contentView, int width, Context context) {
		super(contentView);
		
		int verticalBarGroupHeight = (int) context.getResources().getDimension(R.dimen.popup_vertical_seekbar_group_height);
		
		this.setWidth(width);
		this.setHeight(verticalBarGroupHeight);
		
		this.mContext = context;
		
		setupWindows();
		layoutSeekbarGroup();
	}

	private void setupWindows() {
		this.setFocusable(false);
		this.setTouchable(false);
		
		mSeekbarLength = (int) mContext.getResources().getDimension(R.dimen.popup_vertical_seekbar_length);
		mVerticalSpace = (int) mContext.getResources().getDimension(R.dimen.popup_vertical_seekbar_vertical_space);
		mSeekbarPadding = (int) mContext.getResources().getDimension(R.dimen.popup_vertical_seekbar_padding);
		
		final View contentView = getContentView();
		titleImageView = (ImageView) contentView.findViewById(R.id.content_imageview);
		progressTextView = (TextView) contentView.findViewById(R.id.progress_textview);

		mSeekbar = (SeekBar) contentView.findViewById(R.id.seek_bar);
		mSeekbar.setClickable(false);
		mSeekbar.setFocusable(false);
		
		mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			private int originalProgress; 
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				  originalProgress = seekBar.getProgress(); 
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) { 
	                seekBar.setProgress(originalProgress); 
	            } else {
	            	mSeekbar.setPressed(true);
//	            	updateProgressTextView(progress);
	            }
			}
		});
		
		BitmapDrawable bitmapDrawable = (BitmapDrawable) titleImageView.getDrawable();
		mImageHeight = bitmapDrawable.getBitmap().getHeight();
		
		BitmapDrawable bd = (BitmapDrawable) progressTextView.getBackground();
		backgroundImageHeight = bd.getBitmap().getHeight();
		
		mShowProgressNumber = true;
	}
	
	public SeekBar getSeekbar() {
		return mSeekbar;
	}
	
	protected ImageView getTitleImageView() {
		return titleImageView;
	}

	private void updateProgressTextView(int progress) {
		progressTextView.setText(progress + "");
		int max = mSeekbar.getMax();
		float percent = progress / (float) max;
		int newTopMargin = mImageHeight + mVerticalSpace + (int) ((1 - percent) * (mSeekbarLength - mSeekbarPadding * 2))
				- backgroundImageHeight;

		LayoutParams lp = new LayoutParams(progressTextView.getLayoutParams());
		lp.topMargin = newTopMargin;
		progressTextView.setLayoutParams(lp);
	}
	
	public void setMaxSeekbarValue(int maxValue) {
		mSeekbar.setMax(maxValue);
	}

	public void updateSeekbarValue(int currentVolume) {
		mSeekbar.setProgress(currentVolume);
		updateProgressTextView(currentVolume);
	}
	
	protected TextView getProgressTextView() {
		return progressTextView;
	}
	
	public void setShowProgressNumber(boolean showProgressNumber) {
		this.mShowProgressNumber = showProgressNumber;
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		updateProgressNumberVisiblity();
		super.showAtLocation(parent, gravity, x, y);
	}

	@Override
	public void showAsDropDown(View anchor) {
		updateProgressNumberVisiblity();
		super.showAsDropDown(anchor);
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		updateProgressNumberVisiblity();
		super.showAsDropDown(anchor, xoff, yoff);
	}

	public void updateProgressNumberVisiblity() {
		progressTextView.setVisibility(View.VISIBLE);
	}
	
	public boolean isAlwaysShowing() {
		return !mShowProgressNumber;
	}
	
	@Override
	public void dismiss() {
		setShowProgressNumber(true);
		super.dismiss();
	}

	protected int getSeekbarLength() {
		return mSeekbarLength;
	}

	protected int getVerticalSpace() {
		return mVerticalSpace;
	}

	
	protected abstract void layoutSeekbarGroup();
}
