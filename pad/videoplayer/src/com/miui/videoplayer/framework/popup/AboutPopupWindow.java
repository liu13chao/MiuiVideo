package com.miui.videoplayer.framework.popup;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.R;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

public class AboutPopupWindow extends PauseMediaPlayerPopupWindow {
	private static final String TAG = AboutPopupWindow.class.getCanonicalName();
	
	private static final int WIDTH = 720;
	private static final int HEIGHT = 1280;
	
	private Activity mActivity;
	
	private int mRequestedOrientation;
	public AboutPopupWindow(Activity activity) {
		super(activity, LayoutInflater.from(activity).inflate(R.layout.vp_popup_about, null));
		this.mActivity = activity;
		this.setFocusable(true);
		this.setTouchable(true);
		this.setBackgroundDrawable(new ColorDrawable());
		
		setupViews();
	}

	private void setupViews() {
		View contentView = this.getContentView();
		TextView aboutAppNameTextView = (TextView) contentView.findViewById(R.id.about_app_name_textview);
		int shadowColor = mActivity.getResources().getColor(R.color.about_app_name_shadow_color);
		int aboutShadowColor = mActivity.getResources().getColor(R.color.about_shadow_color); 
				
		TextView aboutTextView = (TextView) contentView.findViewById(R.id.about_textview);
		
		
		aboutTextView.setShadowLayer(8f, 0, 7, aboutShadowColor);
		aboutAppNameTextView.setShadowLayer(10f, 0, 18, shadowColor);
	}

	@Override
	public void show(View anchor) {
		super.show(anchor);
		View containerView = this.getContentView().findViewById(R.id.about_container);
		Log.i(TAG, "show");
		
		mRequestedOrientation = mActivity.getRequestedOrientation();
		int screenOrirentation = DisplayInformationFetcher.getInstance(mActivity).getScreenOrientation();
	
		if (screenOrirentation == DisplayInformationFetcher.SCREEN_LAND) {
			mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			this.setWidth(HEIGHT);
			this.setHeight(HEIGHT);
			containerView.setRotation(-90);
			LayoutParams lp = (LayoutParams) containerView.getLayoutParams();
			lp.width = WIDTH;
			lp.height = HEIGHT;
			lp.leftMargin = 280;
			lp.topMargin = -280;
			containerView.setLayoutParams(lp);
			this.showAtLocation(anchor, Gravity.LEFT, 0, 0);
		} else {
			mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			this.setWidth(WIDTH);
			this.setHeight(HEIGHT);
			containerView.setRotation(0);
			LayoutParams lp = (LayoutParams) containerView.getLayoutParams();
			lp.width = WIDTH;
			lp.height = HEIGHT;
			lp.leftMargin = 0;
			lp.topMargin = 0;
			this.showAtLocation(anchor, Gravity.TOP, 0, 0);
		}
	}
	
	@Override
	public void dismiss() {
		Log.i(TAG, "dismiss");
		mActivity.setRequestedOrientation(mRequestedOrientation);
		super.dismiss();
	}
}
