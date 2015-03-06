package com.miui.video.widget.bg;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.miui.video.DKApp;
import com.miui.video.R;
import com.miui.video.widget.BlurImageView;

public class OnlineBg extends FrameLayout {

	private Context context;
	
	private BlurImageView lastImageView;
	private BlurImageView curImageView;
	private ImageView maskImage;
	
	private int ANIMATION_DURATION = 1000;
	
	public OnlineBg(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public OnlineBg(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public OnlineBg(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	public void refreshBg(boolean animation) {
		Bitmap lastBitmap = DKApp.getSingleton(OnlineBgBmpManager.class).getLastBlurBitmap();
		Bitmap curBitmap = DKApp.getSingleton(OnlineBgBmpManager.class).getCurBlurBitmap();
		if(lastBitmap == null) {
			lastImageView.setBackgroundResource(R.drawable.full_bg_online);
		} else {
			lastImageView.setImageBitmap(lastBitmap);
		}
		if(curBitmap == null) {
			curImageView.setBackgroundResource(R.drawable.full_bg_online);
		} else {
			curImageView.setImageBitmap(curBitmap);
		}
		
		if(animation) {
			animationOut();
			animationIn();
		} else {
			lastImageView.setAlpha(0f);
			curImageView.setAlpha(1f);
		}
	}
	
	//init
	private void init() {
		lastImageView = new BlurImageView(context);
		lastImageView.setScaleType(ScaleType.FIT_XY);
		lastImageView.setBackgroundResource(R.drawable.full_bg_online);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		addView(lastImageView, params);
		
		curImageView = new BlurImageView(context);
		curImageView.setScaleType(ScaleType.FIT_XY);
		curImageView.setBackgroundResource(R.drawable.full_bg_online);
		addView(curImageView, params);
		
		maskImage = new ImageView(context);
		maskImage.setImageResource(R.drawable.full_bg_mask);
		GradientDrawable d = (GradientDrawable) maskImage.getDrawable();
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        d.setGradientRadius(Math.max(metrics.heightPixels, metrics.widthPixels));
		addView(maskImage, params);
		
		refreshBg(false);
	}
	
	//packaged method
	private void animationOut() {
		ObjectAnimator animator = ObjectAnimator.ofFloat(lastImageView, "alpha", 1.0f, 0f);
		animator.setDuration(ANIMATION_DURATION);
		animator.start();
	}
	
	private void animationIn() {
		ObjectAnimator animator = ObjectAnimator.ofFloat(curImageView, "alpha", 0f, 1.0f);
		animator.setDuration(ANIMATION_DURATION);
		animator.start();
	}
}
