package com.miui.video.widget.media;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.miui.video.R;
import com.miui.video.util.MediaViewHelper;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;

/**
 *@author tangfuling
 *
 */

public class MediaViewRows extends LinearLayout {

	//UI
	private MediaViewRow[] mMediaViewRows;
	
	private ArrayList<Object[]> mMediaViewContentList = new ArrayList<Object[]>();
	private Object[] mMediaViewContents;
	private List<Object> mSelectedMedias;
	private boolean mInEditMode = false;
	
	private int mIntervalV;
	private int mSizePerRow;
	
	private Context mContext;
	private OnMediaClickListener mOnMediaClickListener;
	private OnMediaLongClickListener mOnMediaLongClickListener;
	
	public MediaViewRows(Context context) {
		super(context);
		this.mContext = context;
		initDefaultDimen();
		
		init();
	}
	
	public MediaViewRows(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initDefaultDimen();
		
		init();
	}
	
	public MediaViewRows(Context context, Object[] mediaViewContents, List<Object> selectedMedias,
			int intervalV, int sizePerRow) {
		super(context);
		this.mContext = context;
		this.mMediaViewContents = mediaViewContents;
		this.mSelectedMedias = selectedMedias;
		this.mIntervalV = intervalV;
		
		init();
	}
	
	public void setShowText(boolean showText) {
		if(mMediaViewRows == null) {
			return;
		}
		for(int i = 0; i < mMediaViewRows.length; i++) {
			if(mMediaViewRows[i] != null) {
				mMediaViewRows[i].setShowText(showText);
			}
		}
	}
	
	public void setShowMask(boolean showMask) {
		if(mMediaViewRows == null) {
			return;
		}
		for(int i = 0; i < mMediaViewRows.length; i++) {
			if(mMediaViewRows[i] != null) {
				mMediaViewRows[i].setShowMask(showMask);
			}
		}
	}
	
//	public void setInEditMode(boolean inEditMode) {
//		if(mMediaViewRows == null) {
//			return;
//		}
//		for(int i = 0; i < mMediaViewRows.length; i++) {
//			if(mMediaViewRows[i] != null) {
//				mMediaViewRows[i].setInEditMode(inEditMode);
//			}
//		}
//	}
	
	public void setInfoViewColor(int nameViewColor, int statusViewColor) {
		if(mMediaViewRows == null) {
			return;
		}
		for(int i = 0; i < mMediaViewRows.length; i++) {
			if(mMediaViewRows[i] != null) {
				mMediaViewRows[i].setInfoViewColor(nameViewColor, statusViewColor);
			}
		}
	}
	
	public void setMediaViewSize(int width, int height) {
		if(mMediaViewRows == null) {
			return;
		}
		for(int i = 0; i < mMediaViewRows.length; i++) {
			if(mMediaViewRows[i] != null) {
				mMediaViewRows[i].setMediaViewSize(width, height);
			}
		}
	}
	
	public void setMediaViewContents(Object[] mediaViewContents) {
		setMediaViewContents(mediaViewContents, null, mInEditMode);
	}
	
	public void setMediaViewContents(Object[] mediaViewContents, List<Object> selectedMedias, boolean isEditMode) {		
		this.mMediaViewContents = mediaViewContents;
		this.mSelectedMedias = selectedMedias;
		this.mInEditMode = isEditMode;
		refresh();
	}
	
	public void setParams(Object[] mediaViewContents, List<Object> selectedMedias,
			int intervalV, int sizePerRow) {
		this.mMediaViewContents = mediaViewContents;
		this.mSelectedMedias = selectedMedias;
		this.mIntervalV = intervalV;
		refresh();
	}
	
	public OnMediaClickListener getOnMediaClickListener() {
		return mOnMediaClickListener;
	}

	public void setOnMediaClickListener(
			OnMediaClickListener onMediaClickListener) {
		this.mOnMediaClickListener = onMediaClickListener;
		refreshMediaClickListener();
	}
	
	public void setOnMediaLongClickListener(
			OnMediaLongClickListener onMediaLongClickListener) {
		this.mOnMediaLongClickListener = onMediaLongClickListener;
		refreshMediaLongClickListener();
	}

	//init
	private void initDefaultDimen() {
		this.mIntervalV = mContext.getResources().getDimensionPixelSize(R.dimen.media_view_rows_default_intervalV);
	}
	
	private void init() {
		setOrientation(VERTICAL);
		refresh();
	}
	
	//packaged method
	private void refresh() {
		refreshSizePerRow();
		refreshMediaViewList();
		refreshMediaViewRows();
	}
	
	private void refreshSizePerRow() {
		Object obj = null;
		if(mMediaViewContents != null && mMediaViewContents.length > 0) {
			obj = mMediaViewContents[0];
		}
		mSizePerRow = MediaViewHelper.getSizePerRow(obj);
	}
	
	private void refreshMediaViewList() {
		mMediaViewContentList.clear();
		if(mMediaViewContents == null || mMediaViewContents.length == 0) {
			return;
		}
		int rows = (int) Math.ceil(mMediaViewContents.length / (float)mSizePerRow);
		for(int i = 0; i < rows; i++) {
			int sizePerRow = 0;
			if((i + 1) * mSizePerRow <= mMediaViewContents.length) {
				sizePerRow = mSizePerRow;
			} else {
				sizePerRow = mMediaViewContents.length - i * mSizePerRow;
			}
			if(sizePerRow > 0) {
				Object[] mediaViewContents = new Object[sizePerRow];
				for(int j = 0; j < sizePerRow; j++) {
					int curIndex = i * mSizePerRow + j;
					if(curIndex < mMediaViewContents.length) {
						mediaViewContents[j] = mMediaViewContents[curIndex];
					}
				}
				mMediaViewContentList.add(mediaViewContents);
			}
		}
	}
	
	private void refreshMediaViewRows() {
		generateMediaViewRows();
		if(mMediaViewRows != null) {
			removeAllViews();
			for(int i = 0; i < mMediaViewRows.length; i++) {
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if(i != mMediaViewRows.length - 1) {
					params.bottomMargin = mIntervalV;
				}
				if(mMediaViewRows[i] != null) {
					mMediaViewRows[i].refresh();
					mMediaViewRows[i].setLayoutParams(params);
					addView(mMediaViewRows[i]);
				}
			}
		}
	}
	
	private void refreshMediaClickListener() {
		if(mMediaViewRows == null) {
			return;
		}
		for(int i = 0; i < mMediaViewRows.length; i++) {
			mMediaViewRows[i].setOnMediaClickListener(mOnMediaClickListener);
		}
	}
	
	private void refreshMediaLongClickListener() {
		if(mMediaViewRows == null) {
			return;
		}
		for(int i = 0; i < mMediaViewRows.length; i++) {
			mMediaViewRows[i].setOnMediaLongClickListener(mOnMediaLongClickListener);
		}
	}
	
	private void generateMediaViewRows() {
		boolean isRowsCountChanged = false;
		if(mMediaViewRows == null || (mMediaViewRows.length != mMediaViewContentList.size())) {
			isRowsCountChanged = true;
		}
		
		if(isRowsCountChanged) {
			removeAllViews();
			mMediaViewRows = new MediaViewRow[mMediaViewContentList.size()];
			for(int i = 0; i < mMediaViewRows.length; i++) {
				mMediaViewRows[i] = new MediaViewRow(mContext, mMediaViewContentList.get(i), mSelectedMedias, mInEditMode);
				mMediaViewRows[i].setOnMediaClickListener(mOnMediaClickListener);
				mMediaViewRows[i].setOnMediaLongClickListener(mOnMediaLongClickListener);
			}
		} else {
			for(int i = 0; i < mMediaViewRows.length; i++) {
				mMediaViewRows[i].setMediaViewContents(mMediaViewContentList.get(i), mSelectedMedias, mInEditMode);
			}
		}
	}
}
