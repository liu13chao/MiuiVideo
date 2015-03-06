/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ScoringView.java
 *
 *   @author xuanmingliu(liuxuanming@duokan.com)
 *
 *   2012-8-31
 */

package com.miui.video.widget;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.miui.video.R;

/**
 *@author xuanmingliu
 *
 */

public class ScoringView extends LinearLayout implements View.OnClickListener{

	public static final String TAG = ScoringView.class.getName();
	private Context context;
	private int score = 0;
	private static final int STAR_COUNT = 5;
	private static final int SCORE_PER_STAR = 2;
	
	private int scoringViewPadding;

	private ArrayList<ImageView>  starList = new ArrayList<ImageView>();
	
	private boolean isUserRated = false;

	public ScoringView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ScoringView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScoringView(Context context) {
		super(context);
		init();
	}
	
	//public method
	public int geCurtScore() {
		return score;
	}
	
	public boolean isUserRated() {
		return isUserRated;
	}

	//init
	private void init() {
		initDimen();
		initUI();
	}
	
	private void initDimen() {
		context = getContext();
		scoringViewPadding = context.getResources().getDimensionPixelSize(R.dimen.scoring_view_padding);
	}
	
	private void initUI() {
		setOrientation(HORIZONTAL);
		ImageView starView = null;
		LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = scoringViewPadding;
		layoutParams.rightMargin = scoringViewPadding;
		for (int i = 0; i < STAR_COUNT; i++) {
			starView = new ImageView(context);
			starList.add(starView);
			starView.setClickable(true);
			starView.setOnClickListener(this);
			if ((i + 1) * 2 <= score) {
				starView.setBackgroundResource(R.drawable.score_view_checked);
			} else {
				starView.setBackgroundResource(R.drawable.score_view_unchecked);
			}
			addView(starView, layoutParams);
		}
	}

	//packaged method
	private void computeScore(int starIndex) {
		if( starIndex < 0 || starIndex >= STAR_COUNT) {
			score = 0;
			return;
		}

		score = SCORE_PER_STAR * ( starIndex + 1);
		for(int i = 0; i < STAR_COUNT; i++) {
			ImageView starView = starList.get(i);
			if( i <= starIndex)
				starView.setImageResource(R.drawable.score_view_checked);
			else
				starView.setImageResource(R.drawable.score_view_unchecked);
		}
	}
	
	//UI callback
	@Override
	public void onClick(View v) {
		if( v instanceof ImageView) {
			isUserRated = true;
			for(int i = 0; i < STAR_COUNT; i++) {
				if( v == starList.get(i)) {
					computeScore(i);
					break;
				}
			}
		}
	}
}


