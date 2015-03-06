/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MenuView.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-8
 */

package com.miui.videoplayer.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.model.MenuItem;

/**
 * @author tianli
 *
 */
public class MenuViewNew extends FrameLayout {

	private TextView mView;
	private ImageView mImage;
	private LinearLayout llt;
	private MenuItem mMenuItem;
	
//	private int mIconWidth;
//	private int mIconHeight;
//	private int mTextLeftMargin;
//	private int mTextSize;
//	private int mDividerHeight;
//	private int mMenuItemHeight;
//	private int mMenuItemWidth;
	
	public MenuViewNew(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MenuViewNew(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MenuViewNew(Context context) {
		super(context);
	}

	public void init(){
//		mIconWidth = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_icon_width);
//		mIconHeight = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_icon_height);
//		mTextLeftMargin = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_text_left_margin);
//		mTextSize = getResources().getDimensionPixelSize(R.dimen.vp_font_size_15);
//		mDividerHeight = getResources().getDimensionPixelSize(R.dimen.vp_common_divider_height);
//		mMenuItemHeight = getResources().getDimensionPixelSize(R.dimen.vp_common_popup_item_height);
//		mMenuItemWidth = getResources().getDimensionPixelSize(R.dimen.vp_ctrl_menu_item_width);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		llt = (LinearLayout)findViewById(R.id.llt);
		mView = (TextView)llt.findViewById(R.id.menu_item);
		mImage = (ImageView)llt.findViewById(R.id.menu_item_pic);
	}
	
	public void setMenuItem(MenuItem menuItem){
		mMenuItem = menuItem;
		mView.setText(menuItem.mTitle);
//		mView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
		llt.setOnClickListener(menuItem.mClickListener);
		Drawable icon = getResources().getDrawable(menuItem.mIcon);
		mImage.setImageDrawable(icon);
	}
	
	public MenuItem getMenuItem(){
		return mMenuItem;
	}
	
}
