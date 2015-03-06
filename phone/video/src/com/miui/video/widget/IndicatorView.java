package com.miui.video.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * @author zzc
 *
 */
public class IndicatorView extends LinearLayout {
	public static final String TAG = "IndicatorView";

	private int mItemInterval;
	private Drawable mDefaultNormalDrawable, mDefaultSelectedDrawable;
	private SparseArray<Drawable> mNormalDrawables, mSelectedDrawables;
	private int mSelectedIndex = 0;

	public IndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public IndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public IndicatorView(Context context) {
		super(context);
		init();
	}

	private void init() {
//		mItemInterval = getResources().getDimensionPixelSize(R.dimen.margin_9);
//		mDefaultNormalDrawable = getResources().getDrawable(R.drawable.banner_dot_dark);
//		mDefaultSelectedDrawable = getResources().getDrawable(R.drawable.banner_dot_light);

		mNormalDrawables = new SparseArray<Drawable>();
		mSelectedDrawables = new SparseArray<Drawable>();
	}

	public void setItemDefaultResource(int normalRes, int selectedRes) {
		Drawable normal = getResources().getDrawable(normalRes);
		Drawable selected = getResources().getDrawable(selectedRes);
		setItemDefaultDrawable(normal, selected);
	}

	public void setItemDefaultDrawable(Drawable normal, Drawable selected) {
		mDefaultNormalDrawable = normal;
		mDefaultSelectedDrawable = selected;
		refreshAllDrawables();
	}

	public void setItemResource(int normalRes, int selectedRes, int index) {
		Drawable normal = getResources().getDrawable(normalRes);
		Drawable selected = getResources().getDrawable(selectedRes);
		setItemDrawable(normal, selected, index);
	}

	public void setItemDrawable(Drawable normal, Drawable selected, int index) {
		mNormalDrawables.put(index, normal);
		mSelectedDrawables.put(index, selected);
		refreshAllDrawables();
	}

	public void setSelectedItem(int index) {
		mSelectedIndex = index;
		refreshAllDrawables();
	}

	private void refreshAllDrawables() {
		for (int i = 0; i < getChildCount(); i++) {
			ImageView item = (ImageView) getChildAt(i);
			refreshItemDrawable(i, item);
		}
	}

	private void refreshItemDrawable(int index, ImageView item) {
		if (item == null) {
			Log.i(TAG, "item: " + item);
			return;
		}
		if (index == mSelectedIndex) {
			item.setImageDrawable(getSelectedDrawable(index));
		} else {
			item.setImageDrawable(getNormalDrawable(index));
		}
	}

	private Drawable getSelectedDrawable(int index) {
		if (index < 0 || index >= mSelectedDrawables.size()) {
			return mDefaultSelectedDrawable;
		}
		return mSelectedDrawables.get(index);
	}

	private Drawable getNormalDrawable(int index) {
		if (index < 0 || index >= mNormalDrawables.size()) {
			return mDefaultNormalDrawable;
		}
		return mNormalDrawables.get(index);
	}

	public void setItemInterval(int interval) {
		mItemInterval = interval;
		fillIndicator(getChildCount());
	}

	public void setItemCount(int count) {
		fillIndicator(count);
	}

	private void fillIndicator(int count) {
		removeAllViews();
		for (int i = 0; i < count; i++) {
			ImageView item = new ImageView(getContext());
			refreshItemDrawable(i, item);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			if (i != 0) {
				params.leftMargin = mItemInterval;
			}
			addView(item, params);
		}
	}

}
