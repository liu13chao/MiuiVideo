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

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.miui.video.R;
import com.miui.video.base.BaseViewPager;
import com.miui.video.widget.pager.PagerTitle.OnTitleSelectedListener;

/**
 * @author tianli
 * 
 */
public class PagerView extends FrameLayout {

	private Context context;
	
	private int pagerTitleHeight;
	private int pagerTopMargin;
	
	private PagerTitle pagerTitle;
	
	private ViewPagerEx pager;
	private int curPage = 0;
	private ViewFragmentPagerAdapter adapter;

	private OnPageChangeListener onPageChangedListener;

	private Resources mResource;
	
	public PagerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public PagerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public PagerView(Context context) {
		super(context);
		init(null);
	}
	
	public void disableTouchInterceptor(boolean disableTouchInterceptor) {
		pager.disableTouchInterceptor(disableTouchInterceptor);
	}

	private void init(AttributeSet attrs) {
		context = getContext();
		mResource = context.getResources();
		initDimen(attrs);
		initUI();
	}
	
	private void initDimen(AttributeSet attrs) {
		if(attrs != null){
			TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.PagerTitle);
			int type = tArray.getInt(R.styleable.PagerTitle_height, 0);
			if(type == 1){
				pagerTitleHeight = mResource.getDimensionPixelSize(R.dimen.pager_secondary_title_height);
				pagerTopMargin = mResource.getDimensionPixelSize(R.dimen.pager_secondary_top_margin);
			}else{
				pagerTitleHeight = mResource.getDimensionPixelSize(R.dimen.pager_title_height);
				pagerTopMargin = mResource.getDimensionPixelSize(R.dimen.pager_top_margin);
			}
			if(tArray != null){
			    tArray.recycle();
			}
		}else{
			pagerTitleHeight = mResource.getDimensionPixelSize(R.dimen.pager_title_height);
			pagerTopMargin = mResource.getDimensionPixelSize(R.dimen.pager_top_margin);
		}
	}
	
	private void initUI() {
		pager = (ViewPagerEx) View.inflate(context, R.layout.viewpagerex, null);
		pager.setBackgroundResource(R.drawable.transparent);
		LayoutParams pagerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		pagerParams.topMargin = pagerTopMargin;
		pager.setLayoutParams(pagerParams);
		pager.setVerticalScrollBarEnabled(false);
		pager.setOnPageChangeListener(mOnPageChangeListener);
		addView(pager);
		
		FrameLayout titleLayout = new FrameLayout(context);
		pagerTitle = new PagerTitle(context);
		pagerTitle.setOnTitleSelectedListener(mOnTitleSelectedListener);
		LayoutParams param = new LayoutParams(LayoutParams.MATCH_PARENT, (int) pagerTitleHeight);
		param.gravity = Gravity.CENTER;
		titleLayout.addView(pagerTitle, param);
		titleLayout.setBackgroundResource(R.drawable.com_bg_white_shadow);
		LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, (int) pagerTitleHeight);
		addView(titleLayout, titleParams);
	}
	
	public void setPagerTitleTextBold(boolean textBold) {
		pagerTitle.setPagerTitleTextBold(textBold);
	}
	
//	public void setPagerTitleIntervalH(int intervalH) {
//		pagerTitle.setPagerTitleIntervalH(intervalH);
//	}
	
//	public void setTitleCenter(CharSequence[] titles) {
//	    pagerTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, (int) pagerTitleHeight));
//	    pagerTitle.setTitleCenter(titles);
//    }
	
	public void setTitle(CharSequence[] titles) {
		pagerTitle.setTitle(titles);
	}
	
	public void setTitleWithPadding(String[] titles, int padding){
	    pagerTitle.setTitlePadding(padding);
	    pagerTitle.setTitle(titles);
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
//		if (onPageChangedListener != null) {
//			onPageChangedListener.onPageSelected(position);
//		}
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
	
	//UI callback
	private BaseViewPager.OnPageChangeListener 
		mOnPageChangeListener = new BaseViewPager.OnPageChangeListener() {

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
				pagerTitle.setCurPage(curPage);
				if (onPageChangedListener != null) {
					onPageChangedListener.onPageSelected(position);
				}
			}
	};
	
	private OnTitleSelectedListener mOnTitleSelectedListener = new OnTitleSelectedListener() {
		
		@Override
		public void onTitleSelected(int position) {
			setCurPage(position);
		}
	};
	
	//self def class
	public static interface OnPageChangeListener {
		public void onPageSelected(int page);
	}
	
	public interface OnDispatchTouchEventListener {
		public void onDispatchTouchEvent(MotionEvent ev);
	}
}
