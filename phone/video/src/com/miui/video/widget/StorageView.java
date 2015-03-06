package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.video.util.Util;

public class StorageView extends FrameLayout {

	private TextView mLeftName;
	private TextView mLeftValue;
	private TextView mRightName;
	private TextView mRightValue;
	private ProgressBar mProgress;

	public StorageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public StorageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public StorageView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		View view = View.inflate(getContext(), R.layout.storage_bottom, this);
		mLeftName = (TextView) view.findViewById(R.id.storage_bar_left_name);
		mLeftValue = (TextView) view.findViewById(R.id.storage_bar_left_value);
		mRightName = (TextView) view.findViewById(R.id.storage_bar_right_name);
		mRightValue = (TextView) view.findViewById(R.id.storage_bar_right_value);
		mProgress = (ProgressBar) view.findViewById(R.id.storage_bar_progress);
		refresh();
	}
	
	public void setStorage(long total, long remain) {
		if (total <= 0 || remain < 0) {
			return;
		}
		Log.d("test", "setStorage " + total + ", " + remain);
		mProgress.setMax(100);
		mProgress.setProgress((int) (100 - 100 * remain / total));
		mRightValue.setText(Util.convertToFormateSize(total));
		mLeftValue.setText(Util.convertToFormateSize(remain));
	}
	
	public void refresh(){
        setStorage(Util.getSDAllSize(), Util.getSDAvailaleSize());  
	}

}
