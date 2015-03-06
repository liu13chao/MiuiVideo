package com.miui.videoplayer.framework.popup;

import android.view.View;
import android.widget.PopupWindow;

public abstract class ManagedPopupWindow extends PopupWindow {

	public ManagedPopupWindow(View contentView, int width, int height) {
		super(contentView, width, height);
	}

	public ManagedPopupWindow(View contentView) {
		super(contentView);
	}

	public ManagedPopupWindow() {
		super();
	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		if(isShowing())   return;  
		PopupWindowManager.getInstance().addShowingPopupWindow(this);
		super.showAtLocation(parent, gravity, x, y);
	}

	@Override
	public void showAsDropDown(View anchor) {
		if(isShowing())   return;  
		PopupWindowManager.getInstance().addShowingPopupWindow(this);
		super.showAsDropDown(anchor);
	}

	@Override
	public void showAsDropDown(View anchor, int xoff, int yoff) {
		if(isShowing())   return;  
		PopupWindowManager.getInstance().addShowingPopupWindow(this);
		super.showAsDropDown(anchor, xoff, yoff);
	}

	@Override
	public void dismiss() {
		this.dismiss(true);
	}
	
	public void dismiss(boolean remove) {
		try{
			super.dismiss();
		}catch (Exception e) {
		}
		if (remove) {
			PopupWindowManager.getInstance().removeShowingPopupWindow(this);
		} 
	}
	
	public abstract void show(View anchor);
}
