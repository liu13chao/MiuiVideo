/**
 *   Copyright(c) 2012 DuoKan TV Group
 *    
 *   PagerTitle.java
 *
 *   @author tianli(tianli@duokan.com)
 *
 *   2012-8-17 
 */
package com.miui.video.widget.pager;

import java.util.ArrayList;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.miui.video.R;
import com.miui.video.util.DKLog;

/**
 * @author tianli
 * 
 */
public class PagerTitle extends FrameLayout {
	
	public static final String TAG = PagerTitle.class.getName();
	
	private Context context;
	private CharSequence[] tabs;
	private LinearLayout tabGroup;
	private ArrayList<TextView> tabViews = new ArrayList<TextView>();
	private int curTab = 0;
	private View indicator = null;
	private OnPagerTitleListener onPagerTitleListener;
	private Scroller scroller;
	private float lastMotionX = 0;
	
	private int pageArrowBarHeight;
	private int pageArrowBarWidth;
	
	private boolean bIndicatorScrollEnabled = true;
	
	public OnPageIndicatorMoveListener onPageIndicatorMoveListener;
	public OnTabClickedListener  onTabClickedListener;
	public OnTabTouchListener  onTabTouchListener;
	
	private boolean bStartScroll;
	
	private int tabTextSize;
	private int tabNormalColor;
	private int tabFocusColor;
	
	private int tabBgResId;
	
	public interface OnPageIndicatorMoveListener {
		public void onPageIndicatorMoveStart(int startedPos);
		public void onPageIndicatorMoveStop(int stoppedPos);
	}
	
	public interface OnTabClickedListener {
		public void onTabClicked(int tabPos);
	}
	
	public interface OnTabTouchListener{
		public void onTabTouch(int tabPos, int touchAction);
	}
	
	public PagerTitle(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PagerTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PagerTitle(Context context) {
		super(context);
		init();
	}
	
	public void setTabBackgroudResource(int resId) {
		tabBgResId = resId;
	}
	
	public void setTabTextSize(int textSizeId) {
		tabTextSize = context.getResources().getDimensionPixelSize(textSizeId);
	}
	
	public void setTabTextColor(int normalColor, int focusColor) {
		Resources res = context.getResources();
		tabNormalColor = res.getColor(normalColor);
		tabFocusColor = res.getColor(focusColor);
	}
	
	public void setPagerTitleClickable(boolean clickable) {
		int count = tabViews.size();
		for(int i = 0; i < count; i++)
			tabViews.get(i).setClickable(clickable);
	}
	
	public void setIndicatorScrollEnabled(boolean bIndicatorScrollEnable) {
		bIndicatorScrollEnabled = bIndicatorScrollEnable;
	}
	
	public boolean isIndicatorScrollEnabled() {
		return bIndicatorScrollEnabled;
	}
	
	public void setIndicatorBackgroundResource(int resId) {
		indicator.setBackgroundResource(resId);
	}
	
	public void setIndicatorBackgroundResource(int resId, int indicatorW, int indicatorH) {
		indicator.setBackgroundResource(resId);
		pageArrowBarWidth = indicatorW;
		pageArrowBarHeight = indicatorH;
		LayoutParams params;
		params = new LayoutParams(pageArrowBarWidth, pageArrowBarHeight);
		params.gravity = Gravity.BOTTOM;
		indicator.setLayoutParams(params);
		requestLayout();
	}
	
	public void setIndicatorMoveListener(OnPageIndicatorMoveListener onPageIndicatorMoveListener) {
		this.onPageIndicatorMoveListener = onPageIndicatorMoveListener;
	}
	
	public void setTabOnTouchListener( OnTabTouchListener onTabTouchListener) {
		this.onTabTouchListener = onTabTouchListener;
	}

	private void init() {
		context = getContext();
//		tabBgResId = miui.R.drawable.v5_tab_bg_light;
		tabBgResId = R.drawable.transparent;
		Resources res = context.getResources();
		tabTextSize = res.getDimensionPixelSize(R.dimen.font_size_17);
		tabNormalColor = res.getColor(R.color.half_white);
		tabFocusColor = res.getColor(R.color.white);
		
		scroller = new Scroller(context);
		
		LayoutParams params;
		tabGroup = new LinearLayout(context);
		params = new LayoutParams(LayoutParams.MATCH_PARENT, context
				.getResources().getDimensionPixelSize(R.dimen.pager_title_height));
		tabGroup.setLayoutParams(params);
		addView(tabGroup);
		
		pageArrowBarWidth = res.getDimensionPixelSize(R.dimen.page_indicator_arrowbar_width);
		pageArrowBarHeight = res.getDimensionPixelSize(R.dimen.page_indicator_arrowbar_height);
		indicator = new View(context);
		indicator.setBackgroundResource(R.drawable.page_indicator_arrowbar);
		params = new LayoutParams(pageArrowBarWidth, pageArrowBarHeight);
		params.gravity = Gravity.BOTTOM;
		indicator.setLayoutParams(params);
		addView(indicator);
		getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
	}

	private int startPixel = -1;

	public void onPageScrollStateChanged(int state) {
		if (state != ViewPager.SCROLL_STATE_DRAGGING) {
			startPixel = -1;
			if (!scroller.computeScrollOffset() && this.curTab >= 0
					&& this.curTab < tabViews.size()) {
				TextView tabView = this.tabViews.get(this.curTab);
				int indicatorCenterX = indicator.getLeft() + pageArrowBarWidth/2;
				int tabViewCenterX = tabView.getLeft() + tabView.getWidth()/2;
				if ( tabViewCenterX != indicatorCenterX) {
					if( bIndicatorScrollEnabled) {
						bStartScroll = true;
						lastMotionX = indicatorCenterX;
						scroller.startScroll(indicatorCenterX, 0, tabViewCenterX- indicatorCenterX, 0);
					} else {
						if( onPageIndicatorMoveListener != null) {
							onPageIndicatorMoveListener.onPageIndicatorMoveStop(this.curTab);
						}
						indicator.offsetLeftAndRight(tabViewCenterX - indicatorCenterX);
					}
					invalidate();
				} 
			}
		}
	}

	public void onPageScrolled(int tab, int offsetPixels) {
		if (!scroller.computeScrollOffset() && tab >= 0
				&& tab < tabViews.size()) {
			if (offsetPixels == 0) {
				TextView tabView = tabViews.get(tab);
				int tabViewCenterX = tabView.getLeft() + tabView.getWidth()/2;
				int indicatorCenterX = indicator.getLeft() + pageArrowBarWidth/2;
				if( tabViewCenterX != indicatorCenterX) {
					indicator.offsetLeftAndRight(tabViewCenterX - indicatorCenterX);
				}
				return;
			}
			
			if( !bIndicatorScrollEnabled) 
				return;
			
			if (startPixel != -1) {
				View tabView = tabViews.get(tab);
				int curTabViewCenterX = tabView.getLeft() + tabView.getWidth() / 2;
				int delta = 0;
				int oldCenterX = indicator.getLeft() + pageArrowBarWidth/2;
				int targetCenterX = oldCenterX;
				if (offsetPixels - startPixel > 0) {
					if (tab < tabViews.size() - 1) {
						View nextView = tabViews.get(tab + 1);
						int nextTabViewCenterX = nextView.getLeft() + nextView.getWidth() / 2;
						float percent = (offsetPixels - startPixel) / (float) getWidth();
						delta = (int) (( nextTabViewCenterX - curTabViewCenterX) * percent);
						targetCenterX = nextTabViewCenterX;
					} else {
						// should not enter here.
					}
				} else if (offsetPixels - startPixel < 0) {
					if (tab < tabViews.size() - 1) {
						View lastView = tabViews.get(tab + 1);
						int lastTabViewCenterX = lastView.getLeft() + lastView.getWidth() / 2;
						float percent = (startPixel - offsetPixels) / (float) getWidth();
						delta = (int) ((curTabViewCenterX - lastTabViewCenterX) * percent);
						targetCenterX = curTabViewCenterX;
					} else {
						// should not enter here.
					}
				}
				if (delta != 0) {
					if (delta > 0) {
						delta = (int) Math.min(
								targetCenterX - oldCenterX, delta);
					} else {
						delta = (int) Math.max(
								targetCenterX - oldCenterX, delta);
					}
					indicator.offsetLeftAndRight(delta);
					startPixel = offsetPixels;
				}
				
			} else {
				startPixel = offsetPixels;
			}
		}
	}

	public void setCurrentTab(int position) {
		if (this.curTab != position && position >= 0
				&& position < tabViews.size()) {
			
			if( onPageIndicatorMoveListener != null) {
				onPageIndicatorMoveListener.onPageIndicatorMoveStart(this.curTab);
			}
			
			this.curTab = position;
			TextView tabView = tabViews.get(position);
			int tabViewCenterX = tabView.getLeft() + tabView.getWidth()/2;
			int indicatorCenterX = indicator.getLeft() + pageArrowBarWidth/2;
			if( tabViewCenterX != indicatorCenterX) {
				if( bIndicatorScrollEnabled) {
					bStartScroll = true;
					lastMotionX = indicatorCenterX;
					scroller.startScroll(indicatorCenterX, 0, tabViewCenterX - indicatorCenterX, 0);
				} else {	
					if( onPageIndicatorMoveListener != null) {
						onPageIndicatorMoveListener.onPageIndicatorMoveStop(this.curTab);
					}
					indicator.offsetLeftAndRight(tabViewCenterX - indicatorCenterX);
				}
				invalidate();
			}
			
			if (onPagerTitleListener != null) {
				onPagerTitleListener.onPageTitleSelected(this, this.curTab);
			}
		}
	}

	public CharSequence[] getTabs() {
		return tabs;
	}

	public void setTabs(CharSequence[] tabs) {
		this.tabs = tabs;
		initTabs();
	}

	private void initTabs() {
		if (tabs != null) {
			tabGroup.removeAllViews();
			tabViews.clear();
			curTab = 0;
			Resources res = context.getResources();
			int bottomPadding = res.getDimensionPixelSize(R.dimen.pager_title_padding_bottom);
			int shadowColor = res.getColor(R.color.text_shadow_color);
			int shadowXOffset = res.getInteger(R.integer.tab_text_shadow_dx);
			int shadowYOffset = res.getInteger(R.integer.tab_text_shadow_dy);
			int shadowRadius = res.getInteger(R.integer.tab_text_shadow_radius);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					0, LayoutParams.MATCH_PARENT);
			params.weight = 1;
			for (int i = 0; i < tabs.length; i++) {
				TextView tabView = new TextView(context);
				tabView.setClickable(true);
				tabView.setOnClickListener(mOnClickListener);
				tabView.setOnTouchListener(mOnTouchListener);
				tabView.setBackgroundResource(tabBgResId);
				tabViews.add(tabView);
				tabGroup.addView(tabView);
				tabView.setLayoutParams(params);
				tabView.setPadding(0, 0, 0, bottomPadding);

				tabView.setGravity(Gravity.CENTER);
				tabView.setText(tabs[i]);
				TextPaint tvPaint = tabView.getPaint();
				tvPaint.setFakeBoldText(true);
				tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
				tabView.setShadowLayer(shadowRadius, shadowXOffset, shadowYOffset, shadowColor);
				if (i == 0) {
					tabView.setTextColor(tabFocusColor);
				} else {
					tabView.setTextColor(tabNormalColor);
				}
			}
		}
	}

	public OnPagerTitleListener getOnPagerTitleListener() {
		return onPagerTitleListener;
	}

	public void setOnPagerTitleListener(
			OnPagerTitleListener onPagerTitleListener) {
		this.onPagerTitleListener = onPagerTitleListener;
	}
	
	//UI callback
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {
			final float deltaX = scroller.getCurrX() - lastMotionX;
			if (Math.abs(deltaX) >= 1) {
				lastMotionX = scroller.getCurrX();
				indicator.offsetLeftAndRight((int) deltaX);
			}
			invalidate();
		} else {
			bStartScroll = false;
			if( onPageIndicatorMoveListener != null) {
				onPageIndicatorMoveListener.onPageIndicatorMoveStop(this.curTab);
			}
			lastMotionX = indicator.getLeft() + pageArrowBarWidth/2;
			if (this.curTab >= 0 && this.curTab < tabViews.size()) {
				for (int i = 0; i < tabViews.size(); i++) {
					TextView tabView = tabViews.get(i);
					if (i == this.curTab) {
						tabView.setTextColor(tabFocusColor);
					} else {
						tabView.setTextColor(tabNormalColor);
					}
				}
			}
			
		//	DKLog.e("test", "================================================");
		}
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int tab = tabViews.indexOf(v);
			if (tab >= 0) {
				setCurrentTab(tab);
				
				if( onTabClickedListener != null)
					onTabClickedListener.onTabClicked(tab);
			}
		}
	};
	
	private OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
		
		@Override
		public void onGlobalLayout() {
			if ( tabViews.size() > 0) {
				int indicatorCenterX = indicator.getLeft() + pageArrowBarWidth/2;
				TextView initalTabView = tabViews.get(curTab);
				int initialTabCenterX = initalTabView.getLeft() + initalTabView.getWidth() / 2;
				if( indicatorCenterX != initialTabCenterX && !bStartScroll) {
					indicator.offsetLeftAndRight( initialTabCenterX - indicatorCenterX);
				}
			}
		}
	};

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int tab = tabViews.indexOf(v);
			if( onTabTouchListener != null && tab >= 0) {
				try{
					onTabTouchListener.onTabTouch(tabViews.indexOf(v), event.getAction());
				} catch(Exception e) {
					DKLog.d(TAG, e.getLocalizedMessage());
				}
			}
			return false;
		}
	};
	
	//self def class
	public static interface OnPagerTitleListener {
		public void onPageTitleSelected(PagerTitle pagerTitle, int position);
	}
}
