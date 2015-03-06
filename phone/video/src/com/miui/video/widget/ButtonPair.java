package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;

public class ButtonPair extends FrameLayout {

	private OnPairClickListener mListener;

	private TextView mLeftTv;
	private TextView mRightTv;

	public ButtonPair(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ButtonPair(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ButtonPair(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		View view = View.inflate(context, R.layout.button_pair_bottom, this);
		mLeftTv = (TextView) view.findViewById(R.id.button_left);
		mLeftTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onLeftClick();
				}
			}
		});
		mRightTv = (TextView) view.findViewById(R.id.button_right);
		mRightTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null && mRightTv.isEnabled()) {
					mListener.onRightClick();
				}
			}
		});
	}

	public void setLeftText(String text) {
		mLeftTv.setText(text);
	}

	public void setRightText(String text) {
		mRightTv.setText(text);
	}

	public void setLeftText(int res) {
		mLeftTv.setText(res);
	}

	public void setRightText(int res) {
		mRightTv.setText(res);
	}

//	public void setRightTextEnable(boolean enabled){
//		mRightTv.setEnabled(enabled);
//		if(enabled){
//			mRightTv.setTextColor(getResources().getColor(R.color.white));
//		}else{
//			mRightTv.setTextColor(getResources().getColor(R.color.p_30_black));
//		}
//	}
	
	public void setRightButtonEnable(boolean enable){
	    mRightTv.setEnabled(enable);
	}
	
	public void setOnPairClickListener(OnPairClickListener l) {
		mListener = l;
	}

	public static interface OnPairClickListener {
		public void onLeftClick();

		public void onRightClick();
	}

}
