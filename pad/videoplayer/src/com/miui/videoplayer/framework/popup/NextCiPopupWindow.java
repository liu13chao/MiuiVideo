package com.miui.videoplayer.framework.popup;

import com.miui.video.R;
import com.miui.videoplayer.VideoPlayerActivity;
import com.miui.videoplayer.framework.views.OriginMediaController;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class NextCiPopupWindow extends ManagedPopupWindow {
	Handler mHandler;
	int margin = 0;
    public NextCiPopupWindow(Context context, Handler handler) {
        super(LayoutInflater.from(context).inflate(R.layout.vp_popup_next_ci, null));
        
		//margin = (int) context.getResources().getDimension(R.dimen.ci_popup_screen_margin);
		this.setWidth(android.app.ActionBar.LayoutParams.WRAP_CONTENT);
		this.setHeight(android.app.ActionBar.LayoutParams.WRAP_CONTENT);
		this.setTouchable(true);
		this.setOutsideTouchable(true);
		//setOutsideTouchable(true);
		mHandler = handler;

		View rootView = getContentView();
		View selectCiButton = rootView.findViewById(R.id.btn_next_ci);
		if (selectCiButton != null) {
			selectCiButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
					if (mHandler != null) {
						mHandler.sendEmptyMessage(OriginMediaController.NEXT_CI);
					}
				}
			});
		}
	}

	@Override
	public void show(View anchor) {
		// TODO Auto-generated method stub
		if (VideoPlayerActivity.isShowNextCi()) {
			this.showAtLocation(anchor, Gravity.RIGHT, margin, 0);
		} else {
			if (isShowing()) {
				try {
					dismiss();				
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
}
