package com.miui.video.widget.actionmode;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;

/**
 *@author tangfuling
 *
 */

public class ActionModeBottomMenuItem extends LinearLayout {
	
	private Context mContext;
	
	private ImageView mIcon;
	private TextView mText;
	
	private int mIconWidth;
	private int mIconHeight;
	private int mTextTopMargin;
	private int mTextSize;
	
	private boolean mIsLight;
	
	private ActionModeItemClickListener mListener;

	public ActionModeBottomMenuItem(Context context, boolean isLight) {
		super(context);
		this.mContext = context;
		this.mIsLight = isLight;
		init();
	}
	
	public void setIcon(int resId) {
		mIcon.setImageResource(resId);
	}
	
	public void setText(int resId) {
		mText.setText(resId);
	}
	
	protected void setActionModeItemClickListener(ActionModeItemClickListener listener) {
		this.mListener = listener;
	}
	
	protected void setIsEnabled(boolean enabled) {
		this.setEnabled(enabled);
		mIcon.setEnabled(enabled);
		if(enabled) {
			if (mIsLight) {
				mText.setTextColor(getResources().getColor(R.color.p_60_black));
			} else {
				mText.setTextColor(getResources().getColor(R.color.white));
			}
		} else {
			if (mIsLight) {
				mText.setTextColor(getResources().getColor(R.color.p_40_black));
			} else {
				mText.setTextColor(getResources().getColor(R.color.p_40_white));
			}
		}
	}

	//init
	private void init() {
		setOrientation(VERTICAL);
		initDimen();
		initView();
	}
	
	private void initDimen() {
		mIconWidth = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_menu_item_icon_width);
		mIconHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_menu_item_icon_height);
		mTextTopMargin = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_menu_item_text_top_margin);
		mTextSize = getResources().getDimensionPixelSize(R.dimen.font_size_13);
	}
	
	private void initView() {
		mIcon = new ImageView(mContext);
		if (mIsLight) {
			mIcon.setBackgroundResource(R.drawable.bottom_menu_item_bg_dark);
		} else {
			mIcon.setBackgroundResource(R.drawable.bottom_menu_item_bg_light);
		}
		LayoutParams iconParams = new LayoutParams(mIconWidth, mIconHeight);
		iconParams.gravity = Gravity.CENTER_HORIZONTAL;
		addView(mIcon, iconParams);
		
		mText = new TextView(mContext);
		if (mIsLight) {
			mText.setTextColor(getResources().getColor(R.color.p_60_black));
		} else {
			mText.setTextColor(getResources().getColor(R.color.white));			
		}
		mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		LayoutParams textParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.gravity = Gravity.CENTER_HORIZONTAL;
		textParams.topMargin = mTextTopMargin;
		addView(mText, textParams);
		
		this.setClickable(true);
		this.setOnClickListener(mOnClickListener);
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
			
		@Override
		public void onClick(View v) {
			if(mListener != null) {
				mListener.onActionModeItemClick(ActionModeBottomMenuItem.this);
			}
		}
	};
	
	//self def class
	public interface ActionModeItemClickListener {
		public void onActionModeItemClick(ActionModeBottomMenuItem menuItem);
	}
}
