package com.miui.videoplayer.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class VideoProgressView extends FrameLayout{

	float mProgress;
	Paint mPaint;
	public VideoProgressView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void setProgress(float progress){
		this.mProgress = progress;
		invalidate();
	}
	
	private void init(Context context){
		this.setWillNotDraw(false);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(0xfff95f22);
		setFocusable(false);
		setFocusableInTouchMode(false);
	}
	
	@Override
	protected void onMeasure(int widthSpec, int heightSpec) {
		super.onMeasure(widthSpec, heightSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(0, 0, mProgress * getWidth(), getHeight(), mPaint);
	}
	
}
