/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   PagerView.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-24 
 */
package com.miui.video.widget.pager;

import java.util.ArrayList;

import miui.view.ViewPager;
import android.app.Fragment;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.miui.video.R;

/**
 * @author tianli
 * 
 */
public class PagerView extends LinearLayout {

	private Context context;
	private ViewPagerEx pager;
	private int curPage = 0;
	private ViewFragmentPagerAdapter adapter;

	private OnPageChangeListener onPageChangedListener;
	private OnDispatchTouchEventListener onDispatchTouchEventListener;

	public PagerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PagerView(Context context) {
		super(context);
		init();
	}
	
	public void disableTouchInterceptor(boolean disableTouchInterceptor) {
		pager.disableTouchInterceptor(disableTouchInterceptor);
	}

	private void init() {
		context = getContext();
		this.setOrientation(VERTICAL);
		pager = (ViewPagerEx) View.inflate(context, R.layout.viewpagerex, null);
		pager.setBackgroundResource(R.drawable.transparent);
		pager.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		pager.setVerticalScrollBarEnabled(false);
		pager.setOnPageChangeListener(mOnPageChangeListener);
		addView(pager);
	}
	
	public void setOffscreenPageLimit(int totalCount) {
		pager.setOffscreenPageLimit(totalCount);
	}
	
	public void setViewPageBackgroundResource(int resId) {
		pager.setBackgroundResource(resId);
	}

	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int position) {
		this.curPage = position;
		pager.setCurrentItem(position, true);
		if (onPageChangedListener != null) {
			onPageChangedListener.onPageSelected(position);
		}
	}
	
	public void setViewPagerAdapter(ViewFragmentPagerAdapter adapter) {
		this.adapter = adapter;
		pager.setAdapter(adapter);
	}

	public void setPageViews(ArrayList<Fragment> views) {
		adapter.setPages(views);
	}

	public void setPageViews(Fragment[] views) {
		adapter.setPages(views);
	}

	public ViewPagerEx getPager() {
		return pager;
	}
	
	public OnPageChangeListener getOnPageChangedListener() {
		return onPageChangedListener;
	}

	public void setOnPageChangedListener(
			OnPageChangeListener onPageChangedListener) {
		this.onPageChangedListener = onPageChangedListener;
	}
	
	public void setOnDispatchTouchEventListener(
			OnDispatchTouchEventListener onDispatchTouchEventListener) {
		this.onDispatchTouchEventListener = onDispatchTouchEventListener;
	}
	
	//UI callback
	private ViewPager.OnPageChangeListener 
		mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			}
		

			@Override
			public void onPageSelected(int position) {
				curPage = position;
				if (onPageChangedListener != null) {
					onPageChangedListener.onPageSelected(position);
				}
			}
	};
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if(onDispatchTouchEventListener != null) {
			onDispatchTouchEventListener.onDispatchTouchEvent(ev);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/*
	private OnPagerTitleListener mOnPagerTitleListener = new OnPagerTitleListener() {
		
		@Override
		public void onPageTitleSelected(PagerTitle pagerTitle, int position) {
			setCurPage(position);
		}
	};
	*/
	
	//self def class
	public static interface OnPageChangeListener {
		public void onPageSelected(int page);
	}
	
	public interface OnDispatchTouchEventListener {
		public void onDispatchTouchEvent(MotionEvent ev);
	}
}
