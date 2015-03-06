package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;

import com.miui.video.R;
import com.miui.videoplayer.common.DuoKanConstants;

public class BottomControllerPopupWindow extends ManagedPopupWindow {

	private Context mContext;
	
	public BottomControllerPopupWindow(Context context, View contentView) {
		super(contentView);
		
		this.mContext = context;
		
		setWidth(LayoutParams.MATCH_PARENT);
		int height = 0;
		if (DuoKanConstants.ENABLE_V5_UI) {
			height = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_height_v5) ;
		} else {
			height = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_height);
		}
		setHeight(height);
			
		ColorDrawable backgroudColorDrawable = new ColorDrawable(context.getResources().getColor(R.color.vp_black));
		backgroudColorDrawable.setAlpha(179);
		this.setBackgroundDrawable(backgroudColorDrawable);
		
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
				height = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_height_v5) ;
			} else {
				height = (int) mContext.getResources().getDimension(R.dimen.popup_bottom_controller_height);
			}
		}
		setHeight(height);
	}
	
}
