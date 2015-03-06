package com.miui.videoplayer.framework.popup;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.miui.video.R;

public class BufferFullScreenPopupWindow extends ManagedPopupWindow {
	private Context mContext;
	//private TextView mToBePlayMediaTextView;
	private String mToBePlayString;
	private TextView mBufferedPercentTextView;
	
	public BufferFullScreenPopupWindow(Context context) {
		super(LayoutInflater.from(context).inflate(R.layout.vp_popup_buffer_fullscreen_v5, null), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.setAnimationStyle(R.style.full_screen_pause_popup_anim_style);
	
		this.mContext = context;
		
		setupViews();
	}
	
	private void setupViews() {
		//mToBePlayMediaTextView = (TextView) getContentView().findViewById(R.id.to_be_play_media_textview);
		mToBePlayString = mContext.getResources().getString(R.string.to_be_play_label_v5);
		
		mBufferedPercentTextView = (TextView) getContentView().findViewById(R.id.buffer_percent_textview);
	}

	public void show(View anchor, String mediaName) {
		mBufferedPercentTextView.setText("");
//		if (mediaName != null && !mediaName.trim().equals("")) {
//			mToBePlayMediaTextView.setText(mToBePlayString + mediaName); 
//		} else {
//			mToBePlayMediaTextView.setText(mToBePlayString);
//		}
		showAtLocation(anchor, Gravity.TOP, 0, 0);
	}

	@Override
	public void show(View anchor) {
	}

	public void setBufferedPercent(int percent) {
		mBufferedPercentTextView.setText("(" + percent + "%)");
	}
}
