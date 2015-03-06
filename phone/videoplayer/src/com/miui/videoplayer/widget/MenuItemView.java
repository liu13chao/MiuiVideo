package com.miui.videoplayer.widget;

import com.miui.video.R;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 *@author tangfuling
 *
 */

public class MenuItemView extends FrameLayout {
		
	private Context mContext;
	
	private LinearLayout mContainer;
	private ImageView mIcon;
	private TextView mText;
	private View mDivider;
	
	private int mIconWidth;
	private int mIconHeight;
	private int mTextLeftMargin;
	private int mTextSize;
	private int mDividerHeight;
	private int mMenuItemHeight;
	private int mMenuItemWidth;
	
	private boolean mIsEnabled = true;

	public MenuItemView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void setIcon(int resId) {
		mIcon.setImageResource(resId);
	}
	
	public void setText(int resId) {
		mText.setText(resId);
	}
	
	public void setIsEnabled(boolean enabled) {
		mIsEnabled = enabled;
		refresh();
	}
	
	public void setDividerVisibility(int visibility) {
		mDivider.setVisibility(visibility);
	}

	//init
	private void init() {
		initDimen();
		initView();
	}
	
	private void initDimen() {
		mIconWidth = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_icon_width);
		mIconHeight = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_icon_height);
		mTextLeftMargin = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_text_left_margin);
		mTextSize = getResources().getDimensionPixelSize(R.dimen.vp_font_size_15);
		mDividerHeight = getResources().getDimensionPixelSize(R.dimen.vp_common_divider_height);
		mMenuItemHeight = getResources().getDimensionPixelSize(R.dimen.vp_common_popup_item_height);
		mMenuItemWidth = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_width);
	}
	
	private void initView() {
		mContainer = new LinearLayout(mContext);
		mContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		mIcon = new ImageView(mContext);
		LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(mIconWidth, mIconHeight);
		iconParams.gravity = Gravity.CENTER_VERTICAL;
		mContainer.addView(mIcon, iconParams);
		
		mText = new TextView(mContext);
		mText.setTextColor(getResources().getColor(R.color.vp_white));
		mText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textParams.gravity = Gravity.CENTER_VERTICAL;
		textParams.leftMargin = mTextLeftMargin;
		mContainer.addView(mText, textParams);
		
		FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		containerLayoutParams.gravity = Gravity.CENTER_VERTICAL;
		addView(mContainer, containerLayoutParams);
		
		mDivider = new View(mContext);
		mDivider.setBackgroundResource(R.drawable.vp_divider_bg_30);
		FrameLayout.LayoutParams dividerParams = new FrameLayout.LayoutParams(mMenuItemWidth, mDividerHeight);
		dividerParams.topMargin = mMenuItemHeight - mDividerHeight;
		addView(mDivider, dividerParams);
		
		this.setBackgroundResource(R.drawable.vp_list_item_bg);
	}
	
	//packaged method
	private void refresh() {
		if(mIsEnabled) {
			mText.setTextColor(getResources().getColor(R.color.vp_white));
		} else {
			mText.setTextColor(getResources().getColor(R.color.vp_30_white));
		}
	}
}
