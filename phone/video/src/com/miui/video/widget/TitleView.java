package com.miui.video.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;

public class TitleView extends FrameLayout {
	public static final String TAG = "TitleView";

	private OnBackClickListener mListener;
	private OnEditClickListener mEditClickListener;

//	private ImageButton mBackImageButton;
	private TextView mNameTextView;

	private TextView mHintTextView;

	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TitleView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		p.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
		View left = View.inflate(context, R.layout.title_top, null);
		left.setBackground(null);
		left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				DKLog.e(TAG, "back clicked");
				if (mListener != null) {
					mListener.onBackClick();
				}
			}
		});
		addView(left, p);
//		mBackImageButton = (ImageButton) left.findViewById(R.id.title_top_back);
		mNameTextView = (TextView) left.findViewById(R.id.title_top_name);

		LayoutParams params = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.offline_title_btn_width),
				getResources().getDimensionPixelSize(R.dimen.offline_title_btn_height));
		params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		params.rightMargin = getResources().getDimensionPixelSize(
				R.dimen.size_30);
		mHintTextView = new TextView(context);
		mHintTextView.setTextColor(getResources().getColor(R.color.text_color_deep_dark));
		mHintTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_size_36));
		mHintTextView.setMaxLines(1);
		mHintTextView.setGravity(Gravity.CENTER);
		mHintTextView.setBackgroundResource(R.drawable.editable_title_com_btn_bg);
		mHintTextView.setClickable(true);
		mHintTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				DKLog.e(TAG, "edit clicked");
				if (mEditClickListener != null) {
					mEditClickListener.onEditClick();
				}
			}
		});
		addView(mHintTextView, params);
	}

	public void setName(String name) {
		mNameTextView.setText(name);
	}

	public void setName(int nameId) {
		mNameTextView.setText(nameId);
	}

	public void setHint(String hint) {
		mHintTextView.setText(hint);
	}

	public void setHint(int hintId) {
		mHintTextView.setText(hintId);
	}

	public void setHintVisibility(int visibility){
		mHintTextView.setVisibility(visibility);
	}
	
	public void setOnBackClickListener(OnBackClickListener l) {
		mListener = l;
	}

	public static interface OnBackClickListener {
		public void onBackClick();
	}

	public void setOnEditClickListener(OnEditClickListener l) {
		mEditClickListener = l;
	}

	public static interface OnEditClickListener {
		public void onEditClick();
	}

}
