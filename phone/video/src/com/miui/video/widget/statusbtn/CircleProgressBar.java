package com.miui.video.widget.statusbtn;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressBar extends View {

	private int DEFAULT_MAX_VALUE = 100;
    private int mMainCurProgress;
    private int mWidth;
    private int mHeight;
    private RectF mRectF;
    private Paint mPaint;
    
    private Bitmap mBackBitmap;
    private Canvas mMemBackCanvas;
    private Bitmap mMemBackBitmap;
    
    private Bitmap mForeBitmap;
    private Canvas mMemForeCanvas;
    private Bitmap mMemForeBitmap;
    
    public CircleProgressBar(Context context, int width, int height, BitmapDrawable backBitmap, BitmapDrawable foreBitmap) {
    	super(context);
    	this.mWidth = width;
    	this.mHeight = height;
    	this.mBackBitmap = backBitmap.getBitmap();
    	this.mForeBitmap = foreBitmap.getBitmap();
    	init();
	}
    
	public CircleProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
    
    public void setProgress(int progress) {
    	this.mMainCurProgress = progress;
    	invalidate();
    }
	
	//init
	private void init() {
		mRectF = new RectF(0, 0, mWidth, mHeight);
		mMemBackBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		mMemBackCanvas = new Canvas(mMemBackBitmap);
		
		mMemForeBitmap = Bitmap.createBitmap(mWidth, mHeight, Config.ARGB_8888);
		mMemForeCanvas = new Canvas(mMemForeBitmap);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.TRANSPARENT);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		
		mBackBitmap = Bitmap.createScaledBitmap(mBackBitmap, mWidth, mHeight, false);
		mForeBitmap = Bitmap.createScaledBitmap(mForeBitmap, mWidth, mHeight, false);
	}
	
	@Override
	public void onDraw(Canvas canvas) {    
        super.onDraw(canvas);  
        int sweep = mMainCurProgress * 360 / DEFAULT_MAX_VALUE;
        mMemBackBitmap.eraseColor(Color.TRANSPARENT);
        mMemBackCanvas.drawBitmap(mBackBitmap, 0, 0, null);
        mMemBackCanvas.drawArc(mRectF, 270, sweep, false, mPaint);
        canvas.drawBitmap(mMemBackBitmap, 0, 0, null);
        
        mMemForeBitmap.eraseColor(Color.TRANSPARENT);
        mMemForeCanvas.drawBitmap(mForeBitmap, 0, 0, null);
        mMemForeCanvas.drawArc(mRectF, sweep - 90, 360 - sweep, false, mPaint);
        canvas.drawBitmap(mMemForeBitmap, 0, 0, null);
    }  
}
