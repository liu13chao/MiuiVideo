/**
 *   Copyright(c) 2014 XiaoMi TV Group
 *   
 *   MenuFragment.java
 *  
 *   @author tianli(tianli@xiaomi.com)
 * 
 *   @date 2014-7-7
 */

package com.miui.videoplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.miui.video.R;

/**
 * @author tianli
 *
 */
public class MenuView extends FrameLayout {

	protected LinearLayout mLeftMenuGroup;
	protected LinearLayout mRightMenuGroup;
	
	protected View mMenuRoot;
	
	public MenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MenuView(Context context) {
		super(context);
		init();
	}
	
	private void init() {
		mMenuRoot = View.inflate(getContext(), R.layout.vp_menu_layout, null);
		addView(mMenuRoot);
		mLeftMenuGroup = (LinearLayout) mMenuRoot.findViewById(R.id.left_menu);
		mRightMenuGroup = (LinearLayout) mMenuRoot.findViewById(R.id.right_menu);
	}
	
	public void addLeftMenu(MenuItemView item){
		mLeftMenuGroup.addView(item);
	}
	
	public void addRightMenu(MenuItemView item){
		mRightMenuGroup.addView(item);
	}
	
	public void showItem(MenuItemView item){
		if(item != null){
			item.setVisibility(View.VISIBLE);
		}
	}
	
	public void hideItem(MenuItemView item){
		if(item != null){
			item.setVisibility(View.GONE);
		}
	}
}
