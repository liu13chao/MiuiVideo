/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   RatingView.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-17 
 */
package com.miui.video.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.miui.video.R;

/**
 * @author tianli
 * 
 */
public class RatingView extends LinearLayout {

	private float mScore = 0;
	private int mDefaultScore = 10;

	public final static int STAR_TYPE_SMALL = 0;
	public final static int STAR_TYPE_NORMAL = 1;

	private int mStarType = STAR_TYPE_NORMAL;

	private final static int STAR_COUNT = 5;
	public final static float SCORE_PER_STAR = 10 / (float) STAR_COUNT;

	private ImageView[] mStarViews = new ImageView[STAR_COUNT];
	private float[] mStarScores = new float[STAR_COUNT];
	
	private Context context;
	
	private int mStarViewWidth;
	private int mStarViewHeight;

	//TODO: 
	public RatingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RatingStar, defStyle, 0);
		if( a != null) {
			int count = a.getIndexCount();
			for(int i = 0; i < count; i++){
				int attr = a.getIndex(i);
				switch(attr) {
				case R.styleable.RatingStar_ratingStarType:
					mStarType = a.getInt(attr, STAR_TYPE_NORMAL);
					break;
				case R.styleable.RatingStar_ratingStarScore:
					mDefaultScore = a.getInt(attr, mDefaultScore);
					mScore = mDefaultScore;
					break;
				}
			}
			a.recycle();
		}
		init();
	}

	public RatingView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.ratingStarStyle);
	}

	public RatingView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private void init() {
		initDimen();
		setOrientation(HORIZONTAL);
		LayoutParams params;
		for (int i = 0; i < STAR_COUNT; i++) {
			ImageView image = new ImageView(context);
			params = new LayoutParams(mStarViewWidth, mStarViewHeight);
			params.gravity = Gravity.CENTER_VERTICAL;
			image.setLayoutParams(params);
			image.setImageDrawable(getNullStarDrawable());
			addView(image);
			mStarViews[i] = image;
		}
		setScore(mScore);
	}
	
	private void initDimen() {
		if (mStarType == STAR_TYPE_NORMAL) {
			mStarViewWidth = context.getResources().getDimensionPixelSize(R.dimen.rating_view_normal_width);
			mStarViewHeight = context.getResources().getDimensionPixelSize(R.dimen.rating_view_normal_height);
		} else {
			mStarViewWidth = context.getResources().getDimensionPixelSize(R.dimen.rating_view_small_width);
			mStarViewHeight = context.getResources().getDimensionPixelSize(R.dimen.rating_view_small_height);
		}
		
	}

	private Drawable getFullStarDrawable() {
		if (mStarType == STAR_TYPE_NORMAL) {
			return context.getResources().getDrawable(R.drawable.rate_normal_full_star);
		} else {
			return context.getResources().getDrawable(R.drawable.rate_small_full_star);
		}
	}

	private Drawable getNullStarDrawable() {
		if (mStarType == STAR_TYPE_NORMAL) {
			return context.getResources().getDrawable(R.drawable.rate_normal_null_star);
		} else {
			return context.getResources().getDrawable(R.drawable.rate_small_null_star);
		}
	}

	public float getScore() {
		return mScore;
	}

	public void setScore(float score) {
		score = Math.min(10, score);
		score = Math.max(0, score);
		if (score >= 0 && score <= 10) {
			this.mScore = score;
			float[] newStarScores = new float[STAR_COUNT];
			for (int i = 0; i < STAR_COUNT; i++) {
				if (score >= SCORE_PER_STAR) {
					newStarScores[i] = 1.0f;
				} else {
					newStarScores[i] = score / SCORE_PER_STAR;
				}
				score -= SCORE_PER_STAR;
				if (score < 0) {
					score = 0;
				}
			}
			for (int i = 0; i < STAR_COUNT; i++) {
				if (Math.abs(newStarScores[i] - mStarScores[i]) > 0.001) {
					// star changes
					if (Math.abs(newStarScores[i]) < 0.001) {
						mStarViews[i].setImageDrawable(getNullStarDrawable());
					} else if (Math.abs(newStarScores[i] - 1) < 0.001) {
						mStarViews[i].setImageDrawable(getFullStarDrawable());
					} else {
						Bitmap fullStar = ((BitmapDrawable) getFullStarDrawable())
								.getBitmap();
						Bitmap nullStar = ((BitmapDrawable) getNullStarDrawable())
								.getBitmap();
						Bitmap star = Bitmap.createBitmap(fullStar.getWidth(),
								fullStar.getHeight(), Config.ARGB_8888);
						Canvas canvas = new Canvas(star);
						Rect rc = new Rect();
						rc.top = 0;
						rc.bottom = fullStar.getHeight();
						rc.left = 0;
						rc.right = (int) (newStarScores[i] * fullStar
								.getWidth());
						Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
						canvas.drawBitmap(fullStar, rc, rc, paint);
						rc.left = (int) (newStarScores[i] * fullStar.getWidth());
						rc.right = fullStar.getWidth();
						canvas.drawBitmap(nullStar, rc, rc, paint);
						mStarViews[i].setImageBitmap(star);
					}
				}
				mStarScores[i] = newStarScores[i];
			}
		}
	}
}
