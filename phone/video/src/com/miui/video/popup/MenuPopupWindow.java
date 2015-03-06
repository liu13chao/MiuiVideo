package com.miui.video.popup;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.miui.video.AboutActivity;
import com.miui.video.R;
import com.miui.video.SettingActivity;

public class MenuPopupWindow {
	
	private Context mContext;
	private View mAnchor;
	
	private View mContentView;
	private PopupWindow mPopupWindow;
	
	private View mSetting;
	private View mAbout;
	
	private int mWidth;
	private int mXoff;
	private int mYoff;
	
	public MenuPopupWindow(Context context, View anchor) {
		this.mContext = context;
		this.mAnchor = anchor;
		init();
	}
	
	public void show() {
		mPopupWindow.showAsDropDown(mAnchor, mXoff, mYoff);
	}
	
	//init
	private void init() {
		mWidth = mContext.getResources().getDimensionPixelSize(R.dimen.menu_pop_window_width);
		mXoff = mContext.getResources().getDimensionPixelSize(R.dimen.menu_pop_window_xoff);
		mYoff = mContext.getResources().getDimensionPixelSize(R.dimen.menu_pop_window_yoff);
		mPopupWindow = new PopupWindow(mWidth, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(mContext.getResources().getColor(R.color.transparent)));
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		
		mContentView = View.inflate(mContext, R.layout.popup_menu, null);
		mPopupWindow.setContentView(mContentView);
		mSetting = mContentView.findViewById(R.id.popup_menu_setting);
		mAbout = mContentView.findViewById(R.id.popup_menu_about);
		mSetting.setOnClickListener(mOnClickListener);
		mAbout.setOnClickListener(mOnClickListener);
	}
	
	//packaged method
	private void startSettingActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, SettingActivity.class);
		mContext.startActivity(intent);
	}
	
	private void startAboutActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, AboutActivity.class);
		mContext.startActivity(intent);
	}
	
	private void dismiss() {
	    try{
	        mPopupWindow.dismiss();
	    }catch(Exception e){
	    }
	}
	
	//UI callback
	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			dismiss();
			if(v == mSetting) {
				startSettingActivity();
			} else if(v == mAbout) {
				startAboutActivity();
			}
		}
	};
}
