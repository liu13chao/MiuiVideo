package com.miui.videoplayer.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.miui.video.R;

public class BufferingView extends FrameLayout {

	private TextView mBufferedPercentTextView;
	public BufferingView(Context context) {
		super(context);
		init();
	}

	public BufferingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BufferingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init(){
		View result = View.inflate(getContext(), R.layout.vp_buffering_video_for_netplaying_v5, null);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER;
		this.addView(result, lp);
		setBackgroundResource(R.drawable.vp_fullscreen_play_backgroud_v5);
		mBufferedPercentTextView = (TextView)findViewById(R.id.buffer_percent_textview);
	}
	
	public void setBufferedPercent(int percent) {
		mBufferedPercentTextView.setText("(" + percent + "%)");
	}
}
