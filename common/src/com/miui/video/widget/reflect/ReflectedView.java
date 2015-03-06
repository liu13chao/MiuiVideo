package com.miui.video.widget.reflect;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ReflectedView extends LinearLayout {
	
	// self
	private View mSelfView = null;
	private int mSelfHeight = 0;
	private int mSelfWidth = 0;
	
	// mirror
	private int mMirrorGap = 0;
	private int mMirrorHeight = 0;
	private MirrorView mMirrorView;
	// mirror mask
	private float mMirrorAlpha = 1.0f;

	public ReflectedView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public ReflectedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ReflectedView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		setOrientation(VERTICAL);
		mMirrorView = new MirrorView(getContext());
	}
	
	public void setSelfView(View view){
		if(view != null){
			removeAllViews();
			mSelfView = view;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			addView(mSelfView, params);
			mMirrorView.setAlpha(mMirrorAlpha);
			mSelfView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			mSelfWidth = mSelfView.getMeasuredWidth();
			mSelfHeight = mSelfView.getMeasuredHeight();
			// default mirrorHeight is quarter of objective height
			if(mMirrorHeight == 0){
				setMirrorHeight(mSelfHeight/4);
			}else{
				setMirrorHeight(mMirrorHeight);
			}
			addView(mMirrorView);
		}
	}
	
	public void setMirrorHeight(int mirrorHeight) {
		if(mirrorHeight > 0){
			this.mMirrorHeight = mirrorHeight;
			LayoutParams params = new LayoutParams(mSelfWidth, mMirrorHeight);
			params.topMargin = mMirrorGap;
			mMirrorView.setLayoutParams(params);
			// TODO: if mSelfWidth == 0 ?
			mMirrorView.setMirrorSize(mSelfWidth, mSelfHeight, mSelfWidth, mMirrorHeight);
		}
	}
	
	public void setBlurRadius(int blurRadius) {
		mMirrorView.setBlurRadius(blurRadius);
	}
	
	public void setMirrorGap(int mirrorGap){
		this.mMirrorGap = mirrorGap;
		mMirrorView.setPadding(0, mMirrorGap, 0, 0);
	}
	
	/**
	 *  Get canvas of mirror bitmap, must call after {@link setMirrorHeight}
	 * 
	 */
	public Canvas getMirrorCanvas(){
		return mMirrorView.getCanvas();
	}
	
	public void setMirrorAlpha(float alpha){
		mMirrorAlpha = alpha;
		mMirrorView.setAlpha(alpha);
	}
	
	public void setMirrorMask(int resId){
		mMirrorView.setMirrorMask(resId);
	}
	
	public float getMirrorAlpha(){
		return mMirrorAlpha;
	}
	
	public View getMirrorView(){
		return mMirrorView;
	}
	
	public View getSelfView(){
		return mSelfView;
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		if(mSelfWidth == 0 || mSelfHeight == 0){
//			mSelfWidth = mSelfView.getMeasuredWidth();
//			mSelfHeight = mSelfView.getMeasuredHeight();
////			mMirrorView.setMirrorSize(mSelfWidth, mSelfHeight, mSelfWidth, mMirrorHeight);
//		}
//	}
	

}
