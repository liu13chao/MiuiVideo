package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.PopupWindow;

import com.miui.video.R;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;
import com.miui.videoplayer.framework.utils.DisplayInformationFetcher;

public class ReceiveEventV5PopupWindow extends PopupWindow {
	private DuoKanMediaController mDuoKanMediaController;
	private Context mContext;
	
	public ReceiveEventV5PopupWindow(Context context, DuoKanMediaController duoKanMediaController) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_center_receive_event_v5, null));
		
		int height = (int) context.getResources().getDimension(R.dimen.popup_receive_event_height);
		this.setHeight(height);
//		this.setWidth(LayoutParams.MATCH_PARENT);
		int width = (int) context.getResources().getDimension(R.dimen.popup_receive_event_width);
		this.setWidth(width);
//		this.setBackgroundDrawable(context.getResources().getDrawable(R.color.vp_lightblue));
		
		this.mDuoKanMediaController = duoKanMediaController;
		this.mContext = context;
		
		setupViews();
	}

	private void setupViews() {
		View contentView = getContentView();
		contentView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mDuoKanMediaController.onTouchEvent(event);
				return false;
			}
		});
	}

//	@Override
//	public void showAtLocation(View parent, int gravity, int x, int y) {
//		super.showAtLocation(parent, gravity, x, y);
//	}

	public void show(View parent) {
		updateHeight();
		showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	private void updateHeight() {
		int height = (int) mContext.getResources().getDimension(R.dimen.popup_receive_event_height);
		this.setHeight(height);
		int width = (int) mContext.getResources().getDimension(R.dimen.popup_receive_event_width);
		this.setWidth(width);
//		int orientation = DisplayInformationFetcher.getInstance(mContext).getScreenOrientation();
//		if (orientation == DisplayInformationFetcher.SCREEN_LAND) {
//			this.setHeight(500);
//		} else {
//			this.setHeight(900);
//		}
	}
	
}
