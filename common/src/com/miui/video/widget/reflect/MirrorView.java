/**
 *   Copyright(c) 2013 XiaoMi TV Group
 *    
 *   MirrorView.java
 *
 *   @author tianli(tianli@xiaomi.com)
 *
 *   2013-1-31 
 */
package com.miui.video.widget.reflect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author tianli
 *
 */
public class MirrorView extends FrameLayout {

	private Canvas mMirrorCanvas;
	private Bitmap mMirrorBitmap = null;
//	private Paint mMirrorPaint;
	private ImageView mMirrorView;
	
//	private Bitmap mMaskBitmap;
	private View mMaskView;
	
//	private int mBlurRadius;
	
	public MirrorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public MirrorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MirrorView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		mMirrorView = new ImageView(getContext());
		addView(mMirrorView);
		mMaskView = new View(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(mMaskView, params);
		setWillNotDraw(false);
	}
	
	protected void setBlurRadius(int blurRadius) {
//		this.mBlurRadius = blurRadius;
	}
	
	protected void setMirrorSize(int selfWidth, int selfHeight, int width, int height){
		if(mMirrorBitmap != null){
			 mMirrorBitmap.recycle();
		}
		mMirrorBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mMirrorView.setImageBitmap(mMirrorBitmap);
		LayoutParams params = new LayoutParams(width, height);
		mMirrorView.setLayoutParams(params);
		mMirrorCanvas = new Canvas();
		mMirrorCanvas.setBitmap(mMirrorBitmap);
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
        mMirrorCanvas.translate(0, selfHeight);
		mMirrorCanvas.concat(matrix);
	}
	
	protected void setMirrorMask(int resId){
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		mMaskView.setBackgroundResource(resId);
		mMaskView.setLayerType(LAYER_TYPE_HARDWARE, paint);
	}

	protected Canvas getCanvas(){
		return mMirrorCanvas;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
}
