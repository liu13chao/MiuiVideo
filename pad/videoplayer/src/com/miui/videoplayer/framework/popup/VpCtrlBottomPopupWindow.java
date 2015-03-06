package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;

public class VpCtrlBottomPopupWindow extends ManagedPopupWindow {

	private Context mContext;
	
	public VpCtrlBottomPopupWindow(Context context, View contentView) {
		super(contentView);
		
		this.mContext = context;
		
		setWidth(LayoutParams.MATCH_PARENT);
		int height = 0;
		if (DuoKanConstants.ENABLE_V5_UI) {
			height = (int) mContext.getResources().getDimension(R.dimen.vp_ctrl_bottom_pop_height_v5) ;
		} else {
			height = (int) mContext.getResources().getDimension(R.dimen.vp_ctrl_bottom_pop_height);
		}
		setHeight(height);
	}

	@Override
	public void show(View anchor) {
		this.showAtLocation(anchor, Gravity.BOTTOM, 0, 0);
	}

	public void updateHeight(boolean airkan) {
		int height = 0;
		if (airkan) {
			height = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_big_height);
		} else {
			if (DuoKanConstants.ENABLE_V5_UI) {
				height = (int) mContext.getResources().getDimension(R.dimen.vp_ctrl_bottom_pop_height_v5) ;
			} else {
				height = (int) mContext.getResources().getDimension(R.dimen.vp_ctrl_bottom_pop_height);
			}
		}
		setHeight(height);
	}
	
}

