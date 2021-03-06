package com.miui.video.widget.actionmode;

import com.miui.video.R;
import com.miui.video.widget.actionmode.ActionModeBottomMenuItem.ActionModeItemClickListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 *@author tangfuling
 *
 */

public class ActionModeBottomView extends FrameLayout {
	
	private ActionModeBottomMenu mMenu;
	private int mMenuHeight;
	
	public ActionModeBottomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public ActionModeBottomView(Context context, ActionModeBottomMenu menu) {
		super(context);
		this.mMenu = menu;
		
		init();
	}
	
	protected void animationInInner() {
		if(mMenu != null) {
			mMenu.animationIn();
		}
	}
	
	protected void setActionModeItemClickListener(ActionModeItemClickListener listener) {
		if(mMenu == null || listener == null) {
			return;
		}
		mMenu.setActionModeItemClickListener(listener);
	}
	
	protected void setIsEnabled(boolean enabled) {
		if(mMenu != null) {
			mMenu.setIsEnabled(enabled);
		}
	}
	
	//init
	private void init() {
		initDimen();
		initView();
	}
	
	private void initDimen() {
		mMenuHeight = getResources().getDimensionPixelSize(R.dimen.action_mode_bottom_menu_height);
	}
	
	private void initView() {
		if(mMenu == null) {
			return;
		}
		LayoutParams itemWrapperParams = new LayoutParams(LayoutParams.WRAP_CONTENT, mMenuHeight);
		itemWrapperParams.gravity = Gravity.CENTER;
		addView(mMenu, itemWrapperParams);
	}
	
	//UI callback
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}
}
