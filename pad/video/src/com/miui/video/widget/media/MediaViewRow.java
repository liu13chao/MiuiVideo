package com.miui.video.widget.media;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.miui.video.R;
import com.miui.video.local.Favorite;
import com.miui.video.local.PlayHistory;
import com.miui.video.util.DKLog;
import com.miui.video.util.MediaViewHelper;
import com.miui.video.widget.media.MediaView.OnMediaClickListener;
import com.miui.video.widget.media.MediaView.OnMediaLongClickListener;

/**
 *@author tangfuling
 *
 */

public class MediaViewRow extends FrameLayout {
	private final static String TAG = MediaViewRow.class.getSimpleName();

	//UI
	private MediaView[] mMediaViews;
	private int mIntervalH;
	
	private boolean mInEditMode = false;
	
	//the reflection
	private Canvas mMirrorCanvas;
	
	private Context mContext;
	private LinearLayout mLinearLayout;
	
	private OnMediaClickListener onMediaClickListener;
	private OnMediaLongClickListener onMediaLongClickListener;
	
	public MediaViewRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initDefaultDimen();
		
		init();
	}
	
	public MediaViewRow(Context context) {
		super(context);
		this.mContext = context;
		initDefaultDimen();
		
		init();
	}
	
	public MediaViewRow(Context context, Object[] mediaViewContents, List<Object> selectedContents, boolean isEditMode) {
		super(context);
		this.mContext = context;
		this.mInEditMode = isEditMode;
		initDefaultDimen();
		generateMediaViews(mediaViewContents, selectedContents);
		init();
	}
	
	public MediaViewRow(Context context, Object[] mediaViewContents, List<Object> selectedContents, 
			int intervalH) {
		super(context);
		this.mContext = context;
		initDefaultDimen();
		
		this.mIntervalH = intervalH;
		generateMediaViews(mediaViewContents, selectedContents);
		init();
	}
	
	public MediaViewRow(Context context, int intervalH) {
		super(context);
		this.mContext = context;
		initDefaultDimen();
		
		this.mIntervalH = intervalH;
		init();
	}
	
	public void setParams(Object[] mediaViewContents, List<Object> selectedContents, 
			int intervalH) {
		generateMediaViews(mediaViewContents, selectedContents);
		this.mIntervalH = intervalH;
		refresh();
	}
	
	public void setMediaViews(MediaView[] mediaViews) {
		this.mMediaViews = mediaViews;
		refresh();
	}
	
	public MediaView[] getMediaViews() {
		return mMediaViews;
	}
	
	public void setShowText(boolean showText) {
		if(mMediaViews == null) {
			return;
		}
		for(int i = 0; i < mMediaViews.length; i++) {
			if(mMediaViews[i] != null) {
				mMediaViews[i].setShowText(showText);
			}
		}
	}
	
	public void setShowMask(boolean showMask) {
		if(mMediaViews == null) {
			return;
		}
		for(int i = 0; i < mMediaViews.length; i++) {
			if(mMediaViews[i] != null) {
				mMediaViews[i].setShowMask(showMask);
			}
		}
	}
	
	public void setInfoViewColor(int nameViewColor, int statusViewColor) {
		if(mMediaViews == null) {
			return;
		}
		for(int i = 0; i < mMediaViews.length; i++) {
			if(mMediaViews[i] != null) {
				mMediaViews[i].setInfoViewColor(nameViewColor, statusViewColor);
			}
		}
	}
	
	public void setMediaViewSize(int width, int height) {
		if(mMediaViews == null) {
			return;
		}
		for(int i = 0; i < mMediaViews.length; i++) {
			if(mMediaViews[i] != null) {
				mMediaViews[i].setMediaViewSize(width, height);
			}
		}
	}
	
	public void setMediaViewContents(Object[] mediaViewContents) {
		setMediaViewContents(mediaViewContents, null, mInEditMode);
	}
	
	public void setMediaViewContents(Object[] mediaViewContents, List<Object> selectedContents, boolean isEditMode) {
		this.mInEditMode = isEditMode;
		generateMediaViews(mediaViewContents, selectedContents);
		refresh();
	}
	
//	public void setInEditMode(boolean inEditMode) {
//		if(mMediaViews == null) {
//			return;
//		}
//		for(int i = 0; i < mMediaViews.length; i++) {
//			if(mMediaViews[i] != null) {
//				mMediaViews[i].setInEditMode(inEditMode);
//			}
//		}
//	}
	
	public OnMediaClickListener getOnMediaClickListener() {
		return onMediaClickListener;
	}

	public void setOnMediaClickListener(
			OnMediaClickListener onMediaClickListener) {
		this.onMediaClickListener = onMediaClickListener;
		refreshMediaClickListener();
	}
	
	public void setOnMediaLongClickListener(
			OnMediaLongClickListener onMediaLongClickListener) {
		this.onMediaLongClickListener = onMediaLongClickListener;
		refreshMediaLongClickListener();
	}
	
	//the reflection
	public void setMirrorCanvas(Canvas mirrorCanvas) {
		this.mMirrorCanvas = mirrorCanvas;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(mMirrorCanvas != null){
			mMirrorCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			super.dispatchDraw(mMirrorCanvas);
		}
	}

	//init
	private void initDefaultDimen() {
		mIntervalH = mContext.getResources().getDimensionPixelSize(R.dimen.media_view_row_default_intervalH);
	}
	
	private void init() {
		mLinearLayout = new LinearLayout(mContext);
		mLinearLayout.setPadding(0, 0, 0, 0);
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		addView(mLinearLayout, params);
		refresh();
	}
	
	//packaged method
	public void refresh() {
		refreshMediaViews();
	}
	
	private void refreshMediaViews() {
		if(mMediaViews != null) {
			mLinearLayout.removeAllViews();
			for(int i = 0; i < mMediaViews.length; i++) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				if(i != 0) {
					params.leftMargin = mIntervalH;
				}
				if(mMediaViews[i] != null) {
					mMediaViews[i].refresh();
					mLinearLayout.addView(mMediaViews[i], params);
				}
			}
		}
	}
	
	private void refreshMediaClickListener() {
		if(mMediaViews == null) {
			return;
		}
		for(int i = 0; i < mMediaViews.length; i++) {
			mMediaViews[i].setOnMediaClickListener(onMediaClickListener);
		}
	}
	
	private void refreshMediaLongClickListener() {
		if(mMediaViews == null) {
			return;
		}
		for(int i = 0; i < mMediaViews.length; i++) {
			mMediaViews[i].setOnMediaLongClickListener(onMediaLongClickListener);
		}
	}
	
	private void generateMediaViews(Object[] mediaViewContents, List<Object> selectedContents) {
		if(mediaViewContents == null || mediaViewContents.length == 0) {
			mMediaViews = null;
			return;
		}

		boolean isMediaContentMixed = MediaViewHelper.isMediaContentMixed(mediaViewContents);
		boolean isRowSizeChanged = false;
		if(mMediaViews == null || (mMediaViews.length != mediaViewContents.length)) {
			isRowSizeChanged = true;
		}
		
		if(isRowSizeChanged) {
			mMediaViews = new MediaView[mediaViewContents.length];
			for(int i = 0; i < mMediaViews.length; i++) {
				int mediaType = MediaViewHelper.getTypeOf(mediaViewContents[i], isMediaContentMixed);
				boolean isSelected = false;
				if(selectedContents != null && selectedContents.contains(mediaViewContents[i])) {
					isSelected = true;
				}
				DKLog.d(TAG, "generateMediaViews: " + isSelected);
				mMediaViews[i] = new MediaView(mContext, mediaType, mediaViewContents[i], isSelected, mInEditMode);
				mMediaViews[i].setInEditMode(mInEditMode);
				mMediaViews[i].setOnMediaClickListener(onMediaClickListener);
				mMediaViews[i].setOnMediaLongClickListener(onMediaLongClickListener);
			}
		} else {
			for(int i = 0; i < mMediaViews.length; i++) {
				int mediaType = MediaViewHelper.getTypeOf(mediaViewContents[i], isMediaContentMixed);
				boolean isSelected = false;
				if(selectedContents != null && selectedContents.contains(mediaViewContents[i])) {
					isSelected = true;
				}
				DKLog.d(TAG, "generateMediaViews: " + isSelected);
				mMediaViews[i].setMediaType(mediaType);
				mMediaViews[i].setContentInfo(mediaViewContents[i], isSelected, mInEditMode);
				mMediaViews[i].setInEditMode(mInEditMode);
				mMediaViews[i].setOnMediaClickListener(onMediaClickListener);
				mMediaViews[i].setOnMediaLongClickListener(onMediaLongClickListener);
			}
		}
	}
	
}
