/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   ViewPagerEx.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-25 
 */
package com.miui.video.widget.pager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;

import com.miui.video.base.BaseViewPager;

/**
 * @author tianli
 * 
 */
public class ViewPagerBanner extends BaseViewPager {
	
	public ViewPagerBanner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewPagerBanner(Context context) {
		super(context);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN) {
			ViewParent viewParent = getParent();
			if(viewParent != null) {
				viewParent.requestDisallowInterceptTouchEvent(true);
			}
		} else if(ev.getAction() == MotionEvent.ACTION_MOVE) {
			ViewParent viewParent = getParent();
			if(viewParent != null) {
				viewParent.requestDisallowInterceptTouchEvent(true);
			}
		}
		return super.onInterceptTouchEvent(ev);
	}
}
