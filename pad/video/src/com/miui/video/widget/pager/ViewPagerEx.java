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

import miui.view.ViewPager;

import com.miui.video.helper.OnTouchInterceptor;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author tianli
 * 
 */
public class ViewPagerEx extends ViewPager {

	public final static String TAG = ViewPagerEx.class.getName();
	
	private float lastMotionX = Integer.MIN_VALUE;
	private float lastMotionY = Integer.MIN_VALUE;

	private OnTouchInterceptor onTouchInterceptor;
	
	//flags
	private boolean disableTouchInterceptor = false;

	public ViewPagerEx(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewPagerEx(Context context) {
		super(context);
	}
	
	public void disableTouchInterceptor(boolean disableTouchInterceptor) {
		this.disableTouchInterceptor = disableTouchInterceptor;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(disableTouchInterceptor) {
			return false;
		}
		
		if (onTouchInterceptor != null) {
			boolean isIntercepted = false;
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				lastMotionX = ev.getRawX();
				lastMotionY = ev.getRawY();
				onTouchInterceptor.onPreIntercept(ev);
				break;
			case MotionEvent.ACTION_MOVE:
				float x = ev.getRawX();
				float y = ev.getRawY();
				if (Math.pow(Math.abs(x - lastMotionX), 2)
						* Math.pow(Math.abs(y - lastMotionY), 2) < 2) {
					return false;
				}
				if (lastMotionX != Integer.MIN_VALUE
						&& lastMotionY != Integer.MIN_VALUE) {
					double angel = Math.atan(Math.abs(y - lastMotionY)
							/ Math.abs(x - lastMotionX));
					if (angel > -Math.PI / 4 && angel < Math.PI / 4) {
						// less than 45 degree, the direction is horizontal
						// scroll.
						isIntercepted = onTouchInterceptor
								.onIntercept(
										x < lastMotionX ? OnTouchInterceptor.SCROLL_LEFT
												: OnTouchInterceptor.SCROLL_RIGHT,
										ev);
					} else {
						isIntercepted = onTouchInterceptor.onIntercept(
								y < lastMotionY ? OnTouchInterceptor.SCROLL_UP
										: OnTouchInterceptor.SCROLL_DOWN, ev);
					}
				}
				
				lastMotionX = x;
				lastMotionY = y;
				if (isIntercepted) {
					return false;
				}
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	public OnTouchInterceptor getOnTouchInterceptor() {
		return onTouchInterceptor;
	}

	public void setOnTouchInterceptor(OnTouchInterceptor onTouchInterceptor) {
		this.onTouchInterceptor = onTouchInterceptor;
	}

}
