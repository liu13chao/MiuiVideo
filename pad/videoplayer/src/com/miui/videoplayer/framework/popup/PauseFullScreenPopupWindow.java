package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

import com.miui.video.R;
import com.miui.videoplayer.framework.DuoKanConstants;
import com.miui.videoplayer.framework.ui.DuoKanMediaController;

public class PauseFullScreenPopupWindow extends ManagedPopupWindow{

	private ImageView mFullScreenImageView;
	private DuoKanMediaController mDuoKanMediaController;

	public PauseFullScreenPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_pause_full_screen, null), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		
		setupWindows();
	}

	private void setupWindows() {
		this.setAnimationStyle(R.style.full_screen_pause_popup_anim_style);
		
		View root = getContentView();
		root.getBackground().setAlpha(179);
		
		mFullScreenImageView = (ImageView) root.findViewById(R.id.full_screen_play_iamgeview);
		
		if (DuoKanConstants.ENABLE_V5_UI) {
			root.getBackground().setAlpha(255);
			root.setBackgroundResource(R.drawable.vp_fullscreen_play_backgroud_v5);
			mFullScreenImageView.setImageResource(R.drawable.vp_fullscreen_play_imageview_bg_v5);
		}	

		root.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mDuoKanMediaController.onTouchEvent(event);
				return false;
			}
		});
	}

	public void setOnImageViewClickListener(OnClickListener onClickListener) {
		mFullScreenImageView.setOnClickListener(onClickListener);
	}

	@Override
	public void show(View anchor) {
		showAtLocation(anchor, Gravity.TOP, 0, 0);
	}
	
    public void setDuoKanMediaController(DuoKanMediaController duoKanMediaController){
    	this.mDuoKanMediaController = duoKanMediaController;
    }


}
