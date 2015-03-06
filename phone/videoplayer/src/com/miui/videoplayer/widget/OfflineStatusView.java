package com.miui.videoplayer.widget;

import com.miui.video.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Region;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

public class OfflineStatusView extends View{

	Bitmap mDoneBitmap, mDowloadingBitmap, mWaitBitmap, mPauseBitmp, mFailBitmap, 
			mCurCircleBitmap, mCircleBitmapNormal, mCircleBitmapPress, mCirclePaceBitmap;
	int mCircleWidth, mCircleHeight;
	int mWidth, mHeight;
	int mStatus;
	public static final int OFFLINE_STATUS_NONE = 0;
	public static final int OFFLINE_STATUS_DONE = 1;
	public static final int OFFLINE_STATUS_DOWLOADING = 2;
	public static final int OFFLINE_STATUS_WAIT = 3;
	public static final int OFFLINE_STATUS_PAUSE = 4;
	public static final int OFFLINE_STATUS_ERROR = 5;
	public OfflineStatusView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDoneBitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.offline_finish_icon)).getBitmap();
		mDowloadingBitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.play_offline_x_icon)).getBitmap();
		mWaitBitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.play_offline_wait_pic)).getBitmap();
		mPauseBitmp = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.play_offline_z_icon)).getBitmap();
		mFailBitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.play_offline_s_pic)).getBitmap();
		mCircleBitmapNormal = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.play_offline_y_pic)).getBitmap();
		mCircleBitmapPress = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.play_offline_pressed_pic)).getBitmap();
		mCurCircleBitmap = mCircleBitmapNormal;
		mCirclePaceBitmap = ((BitmapDrawable)context.getResources().getDrawable(R.drawable.play_offline_c_pic)).getBitmap();
		mCircleWidth = mCurCircleBitmap.getWidth();
		mCircleHeight = mCurCircleBitmap.getHeight();
		mWidth = mCircleWidth;
		mHeight = mCircleHeight;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mWidth, mHeight);
	}
	
	public void setPressed(boolean pressed){
		mCurCircleBitmap = pressed ? mCircleBitmapPress : mCircleBitmapNormal;
		invalidate();
	}
	
	public void setStatus(int status){
		mStatus = status;
		invalidate();
	}
	
	public void setProgress(float progress){
		dis = (int) (360 * progress);
		invalidate();
	}
	
	RectF rectF = new RectF(0, 0, mWidth, mHeight);
	Path path = new Path();
	int dis = 0;
	protected void onDraw(Canvas canvas) {
		switch (mStatus) {
		case OFFLINE_STATUS_DONE:
			canvas.drawBitmap(mDoneBitmap, (mWidth - mDoneBitmap.getWidth()) / 2, 
					(mHeight - mDoneBitmap.getHeight()) / 2, null);
			break;
		case OFFLINE_STATUS_DOWLOADING:
			canvas.drawBitmap(mDowloadingBitmap, (mWidth - mDowloadingBitmap.getWidth()) / 2, 
					(mHeight - mDowloadingBitmap.getHeight()) / 2, null);
			canvas.drawBitmap(mCurCircleBitmap, (mWidth - mCircleWidth) / 2, (mHeight - mCircleHeight) / 2, null);
			path.reset();
			float p = mWidth / 2;
			float r = (float) Math.sqrt(p * p * 2);
			path.moveTo(p, p);
			int degree = 270 + dis;
			path.lineTo((float) (p + r * Math.cos(270 * Math.PI / 180)), 
					(float)(p + r * Math.sin(270 * Math.PI / 180)));
			path.lineTo(mWidth, 0);
			if(dis >= 90){
				path.lineTo(mWidth, mHeight);
			}
			if(dis >= 180){
				path.lineTo(0, mHeight);
			}
			if(dis >= 270){
				path.lineTo(0, 0);
			}
			path.lineTo((float)(p + r * Math.cos(degree * Math.PI / 180)), 
					(float)(p + r * Math.sin(degree * Math.PI / 180)));
			path.close();
			canvas.clipRect(0, 0, mWidth, mHeight);
			canvas.clipPath(path, Region.Op.INTERSECT);
			canvas.drawBitmap(mCirclePaceBitmap, (mWidth - mCircleWidth) / 2, (mHeight - mCircleWidth) / 2, null);
			break;
		case OFFLINE_STATUS_WAIT:
			canvas.drawBitmap(mWaitBitmap, (mWidth - mWaitBitmap.getWidth()) / 2,
					(mHeight - mWaitBitmap.getHeight()) / 2, null);
			canvas.drawBitmap(mCurCircleBitmap, (mWidth - mCircleWidth) / 2, (mHeight - mCircleHeight) / 2, null);
			break;
		case OFFLINE_STATUS_PAUSE:
			canvas.drawBitmap(mPauseBitmp, (mWidth - mPauseBitmp.getWidth()) / 2,
					(mHeight - mPauseBitmp.getHeight()) / 2, null);
			canvas.drawBitmap(mCurCircleBitmap, (mWidth - mCircleWidth) / 2, (mHeight - mCircleHeight) / 2, null);
			break;
		case OFFLINE_STATUS_ERROR:
			canvas.drawBitmap(mFailBitmap, (mWidth - mFailBitmap.getWidth()) / 2,
					(mHeight - mFailBitmap.getHeight()) / 2, null);
			canvas.drawBitmap(mCurCircleBitmap, (mWidth - mCircleWidth) / 2, (mHeight - mCircleHeight) / 2, null);
			break;
		default:
			canvas.drawBitmap(mDowloadingBitmap, (mWidth - mDowloadingBitmap.getWidth()) / 2, 
					(mHeight - mDowloadingBitmap.getHeight()) / 2, null);
			canvas.drawBitmap(mCurCircleBitmap, (mWidth - mCircleWidth) / 2, (mHeight - mCircleHeight) / 2, null);
			break;
		}
		
    }
	
}
